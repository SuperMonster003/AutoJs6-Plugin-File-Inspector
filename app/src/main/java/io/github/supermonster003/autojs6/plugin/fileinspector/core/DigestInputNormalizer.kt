package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.util.Locale

class ExpectedDigest internal constructor(
    val algorithm: DigestAlgorithm,
    val canonicalHex: String,
    bytes: ByteArray,
) {

    private val digestBytes = bytes.copyOf()

    init {
        require(canonicalHex.length == algorithm.byteCount * 2)
        require(digestBytes.size == algorithm.byteCount)
    }

    internal fun copyBytesForComparison(): ByteArray = digestBytes.copyOf()
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
        if (trimmed.any { !it.isAcceptedAscii() }) return invalid(DigestInputError.INVALID_CHARACTER)

        val prefix = extractPrefix(trimmed)
        if (prefix.error != null) return invalid(prefix.error)
        if (prefix.algorithm != null && algorithmHint != null && prefix.algorithm != algorithmHint) {
            return invalid(DigestInputError.ALGORITHM_CONFLICT)
        }
        val requestedAlgorithm = prefix.algorithm ?: algorithmHint
        var digestText = prefix.digestText
        val hasHexPrefix = digestText.startsWith("0x", ignoreCase = true)
        if (hasHexPrefix) {
            if (requestedAlgorithm != null && requestedAlgorithm != DigestAlgorithm.CRC32) {
                return invalid(DigestInputError.ALGORITHM_CONFLICT)
            }
            digestText = digestText.substring(2)
        }
        if (digestText.isEmpty()) return invalid(DigestInputError.EMPTY)

        val normalizedHex = normalizeHex(digestText)
        if (normalizedHex.error != null) return invalid(normalizedHex.error)
        val hex = requireNotNull(normalizedHex.value)
        if (hex.length % 2 != 0) return invalid(DigestInputError.ODD_LENGTH)

        val algorithm = requestedAlgorithm ?: DigestAlgorithm.fromHexLength(hex.length)
            ?: return invalid(DigestInputError.UNKNOWN_LENGTH)
        if (hex.length != algorithm.byteCount * 2) return invalid(DigestInputError.LENGTH_MISMATCH)
        if (hasHexPrefix && algorithm != DigestAlgorithm.CRC32) {
            return invalid(DigestInputError.ALGORITHM_CONFLICT)
        }

        return DigestParseResult.Valid(
            ExpectedDigest(
                algorithm = algorithm,
                canonicalHex = hex.lowercase(Locale.ROOT),
                bytes = HexCodec.decode(hex),
            ),
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
        return if (couldBeFingerprint) {
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

    private data class PrefixResult(
        val algorithm: DigestAlgorithm? = null,
        val digestText: String,
        val error: DigestInputError? = null,
    )

    private data class NormalizedHexResult(
        val value: String? = null,
        val error: DigestInputError? = null,
    )

    private const val MAX_INPUT_CHARACTERS = 512
    private const val ASCII_SPACE = 0x20
    private const val ASCII_TILDE = 0x7E
}
