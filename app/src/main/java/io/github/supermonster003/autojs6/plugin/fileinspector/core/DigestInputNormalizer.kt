package io.github.supermonster003.autojs6.plugin.fileinspector.core

class ExpectedDigest internal constructor(
    val algorithm: DigestAlgorithm,
    val canonicalHex: String,
    bytes: ByteArray,
    val sourceFileName: String? = null,
) {

    private val digestBytes = bytes.copyOf()

    init {
        require(canonicalHex.length == algorithm.byteCount * 2)
        require(digestBytes.size == algorithm.byteCount)
    }

    internal fun copyBytesForComparison(): ByteArray = digestBytes.copyOf()

    fun hasSourceFileNameMismatch(currentFileName: String): Boolean =
        sourceFileName != null && sourceFileName != currentFileName
}

sealed interface DigestParseResult {

    data class Valid(val value: ExpectedDigest) : DigestParseResult

    data class Invalid(val reason: DigestInputError) : DigestParseResult
}

enum class DigestInputError {
    EMPTY,
    TOO_LONG,
    MULTILINE,
    UNKNOWN_ALGORITHM,
    ALGORITHM_CONFLICT,
    INVALID_CHARACTER,
    INVALID_SEPARATOR,
    ODD_LENGTH,
    UNKNOWN_LENGTH,
    LENGTH_MISMATCH,
}

object DigestInputNormalizer {

    fun parse(
        raw: String,
        algorithmHint: DigestAlgorithm? = null,
    ): DigestParseResult {
        if (raw.length > MAX_INPUT_CHARACTERS) return invalid(DigestInputError.TOO_LONG)
        val trimmed = raw.trimAsciiWhitespace()
        if (trimmed.isEmpty()) return invalid(DigestInputError.EMPTY)
        if (trimmed.any { it == '\r' || it == '\n' }) return invalid(DigestInputError.MULTILINE)

        val checksumLine = extractCoreutilsLine(trimmed)
        if (checksumLine.sourceFileName?.any { it.isISOControl() } == true) {
            return invalid(DigestInputError.INVALID_CHARACTER)
        }
        if (checksumLine.digestText.any { !it.isAcceptedAscii() }) {
            return invalid(DigestInputError.INVALID_CHARACTER)
        }

        val prefix = extractSri(checksumLine.digestText) ?: extractPrefix(checksumLine.digestText)
        if (prefix.error != null) return invalid(prefix.error)
        if (prefix.algorithm != null && algorithmHint != null && prefix.algorithm != algorithmHint) {
            return invalid(DigestInputError.ALGORITHM_CONFLICT)
        }
        val requestedAlgorithm = prefix.algorithm ?: algorithmHint
        var digestText = prefix.digestText
        val isSupportedBase64 = Base64Codec.decode(digestText)?.let { bytes ->
            (requestedAlgorithm ?: DigestAlgorithm.fromByteCount(bytes.size))?.byteCount == bytes.size
        } == true
        val hasHexPrefix = prefix.encoding == DigestEncoding.AUTO &&
                digestText.startsWith("0x", ignoreCase = true) &&
                !isSupportedBase64
        if (hasHexPrefix) {
            if (requestedAlgorithm != null && requestedAlgorithm != DigestAlgorithm.CRC32) {
                return invalid(DigestInputError.ALGORITHM_CONFLICT)
            }
            digestText = digestText.substring(2)
        }
        if (digestText.isEmpty()) return invalid(DigestInputError.EMPTY)

        if (prefix.encoding == DigestEncoding.BASE64) {
            return parseBase64(
                value = digestText,
                requestedAlgorithm = requestedAlgorithm,
                sourceFileName = checksumLine.sourceFileName,
            )
        }

        val normalizedHex = normalizeHex(digestText)
        if (normalizedHex.error != null) {
            val decoded = Base64Codec.decode(digestText)
            val decodedAlgorithm = decoded?.let { bytes ->
                requestedAlgorithm ?: DigestAlgorithm.fromByteCount(bytes.size)
            }
            if (decoded != null && decodedAlgorithm != null) {
                if (decoded.size != decodedAlgorithm.byteCount) {
                    return invalid(DigestInputError.LENGTH_MISMATCH)
                }
                return valid(decodedAlgorithm, decoded, checksumLine.sourceFileName)
            }
            if (decoded != null && digestText.any { it == '+' || it == '/' || it == '=' }) {
                return invalid(DigestInputError.UNKNOWN_LENGTH)
            }
            return invalid(normalizedHex.error)
        }
        val hex = requireNotNull(normalizedHex.value)
        val base64Alternative = if (hasHexPrefix) {
            null
        } else {
            parseSupportedBase64(digestText, requestedAlgorithm, checksumLine.sourceFileName)
        }
        if (hex.length % 2 != 0) {
            return base64Alternative ?: invalid(DigestInputError.ODD_LENGTH)
        }

        val algorithm = requestedAlgorithm ?: DigestAlgorithm.fromHexLength(hex.length)
            ?: return base64Alternative ?: invalid(DigestInputError.UNKNOWN_LENGTH)
        if (hex.length != algorithm.byteCount * 2) {
            return base64Alternative ?: invalid(DigestInputError.LENGTH_MISMATCH)
        }
        if (hasHexPrefix && algorithm != DigestAlgorithm.CRC32) {
            return invalid(DigestInputError.ALGORITHM_CONFLICT)
        }

        return valid(
            algorithm = algorithm,
            bytes = HexCodec.decode(hex),
            sourceFileName = checksumLine.sourceFileName,
        )
    }

    private fun parseBase64(
        value: String,
        requestedAlgorithm: DigestAlgorithm?,
        sourceFileName: String?,
    ): DigestParseResult {
        val bytes = Base64Codec.decode(value) ?: return invalid(DigestInputError.INVALID_CHARACTER)
        val algorithm = requestedAlgorithm ?: DigestAlgorithm.fromByteCount(bytes.size)
            ?: return invalid(DigestInputError.UNKNOWN_LENGTH)
        if (bytes.size != algorithm.byteCount) return invalid(DigestInputError.LENGTH_MISMATCH)
        return valid(algorithm, bytes, sourceFileName)
    }

    private fun parseSupportedBase64(
        value: String,
        requestedAlgorithm: DigestAlgorithm?,
        sourceFileName: String?,
    ): DigestParseResult.Valid? {
        val bytes = Base64Codec.decode(value) ?: return null
        val algorithm = requestedAlgorithm ?: DigestAlgorithm.fromByteCount(bytes.size) ?: return null
        if (bytes.size != algorithm.byteCount) return null
        return valid(algorithm, bytes, sourceFileName)
    }

    private fun extractCoreutilsLine(value: String): ChecksumLineResult {
        val delimiterIndex = value.indexOf(' ')
        if (delimiterIndex <= 0 || delimiterIndex + 1 >= value.length) {
            return ChecksumLineResult(digestText = value)
        }
        val modeMarker = value[delimiterIndex + 1]
        if (modeMarker != ' ' && modeMarker != '*') {
            return ChecksumLineResult(digestText = value)
        }

        val digestText = value.substring(0, delimiterIndex)
        val isSupportedHexLength = digestText.all { it.isAsciiHexDigit() } &&
                DigestAlgorithm.entries.any { algorithm ->
                    digestText.length == algorithm.byteCount * HEX_CHARACTERS_PER_BYTE
                }
        val sourceFileName = value.substring(delimiterIndex + 2)
        return if (isSupportedHexLength && sourceFileName.isNotEmpty()) {
            ChecksumLineResult(digestText, sourceFileName)
        } else {
            ChecksumLineResult(digestText = value)
        }
    }

    private fun extractSri(value: String): PrefixResult? {
        val delimiterIndex = value.indexOf('-')
        if (delimiterIndex <= 0) return null
        val algorithm = DigestAlgorithm.fromInputName(value.substring(0, delimiterIndex)) ?: return null
        if (algorithm !in SRI_ALGORITHMS) return null
        return PrefixResult(
            algorithm = algorithm,
            digestText = value.substring(delimiterIndex + 1),
            encoding = DigestEncoding.BASE64,
        )
    }

    private fun extractPrefix(value: String): PrefixResult {
        val delimiterIndex = value.indexOfFirst { it == ':' || it == '=' }
        if (delimiterIndex <= 0) return PrefixResult(digestText = value)

        val candidate = value.substring(0, delimiterIndex).trimAsciiWhitespace()
        val algorithm = DigestAlgorithm.fromInputName(candidate)
        if (algorithm != null) {
            return PrefixResult(
                algorithm = algorithm,
                digestText = value.substring(delimiterIndex + 1).trimAsciiWhitespace(),
            )
        }

        val delimiter = value[delimiterIndex]
        val couldBeFingerprint = delimiter == ':' &&
                candidate.length == 2 &&
                candidate.all { character -> character.isAsciiHexDigit() }
        val couldBePaddedBase64 = delimiter == '=' &&
                Base64Codec.decode(value) != null
        return if (couldBeFingerprint) {
            PrefixResult(digestText = value)
        } else if (couldBePaddedBase64) {
            PrefixResult(digestText = value)
        } else {
            PrefixResult(digestText = value, error = DigestInputError.UNKNOWN_ALGORITHM)
        }
    }

    private fun normalizeHex(value: String): NormalizedHexResult {
        if (value.all { character -> character.isAsciiHexDigit() }) {
            return NormalizedHexResult(value = value)
        }

        val separators = value.filter { it == ':' || it == '-' || it == ' ' }.toSet()
        if (value.any { !it.isAsciiHexDigit() && it !in separators }) {
            return NormalizedHexResult(error = DigestInputError.INVALID_CHARACTER)
        }
        if (separators.size != 1) {
            return NormalizedHexResult(error = DigestInputError.INVALID_SEPARATOR)
        }
        val separator = separators.single()
        val parts = value.split(separator)
        if (parts.isEmpty() || parts.any { part ->
                part.length != 2 || !part.all { character -> character.isAsciiHexDigit() }
            }
        ) {
            return NormalizedHexResult(error = DigestInputError.INVALID_SEPARATOR)
        }
        return NormalizedHexResult(value = parts.joinToString(separator = ""))
    }

    private fun Char.isAcceptedAscii(): Boolean {
        return code in ASCII_SPACE..ASCII_TILDE
    }

    private fun Char.isAsciiHexDigit(): Boolean {
        return this in '0'..'9' || this in 'a'..'f' || this in 'A'..'F'
    }

    private fun String.trimAsciiWhitespace(): String = trim { character ->
        character == ' ' || character == '\t' || character == '\r' || character == '\n'
    }

    private fun invalid(error: DigestInputError) = DigestParseResult.Invalid(error)

    private fun valid(
        algorithm: DigestAlgorithm,
        bytes: ByteArray,
        sourceFileName: String?,
    ) = DigestParseResult.Valid(
        ExpectedDigest(
            algorithm = algorithm,
            canonicalHex = HexCodec.encode(bytes),
            bytes = bytes,
            sourceFileName = sourceFileName,
        ),
    )

    private data class PrefixResult(
        val algorithm: DigestAlgorithm? = null,
        val digestText: String,
        val encoding: DigestEncoding = DigestEncoding.AUTO,
        val error: DigestInputError? = null,
    )

    private data class ChecksumLineResult(
        val digestText: String,
        val sourceFileName: String? = null,
    )

    private data class NormalizedHexResult(
        val value: String? = null,
        val error: DigestInputError? = null,
    )

    private enum class DigestEncoding {
        AUTO,
        BASE64,
    }

    private const val MAX_INPUT_CHARACTERS = 512
    private const val HEX_CHARACTERS_PER_BYTE = 2
    private const val ASCII_SPACE = 0x20
    private const val ASCII_TILDE = 0x7E
    private val SRI_ALGORITHMS = setOf(
        DigestAlgorithm.SHA256,
        DigestAlgorithm.SHA384,
        DigestAlgorithm.SHA512,
    )
}
