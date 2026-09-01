package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction
import kotlin.math.ln

enum class ContentKind {
    EMPTY,
    LIKELY_TEXT,
    LIKELY_BINARY,
    HIGH_ENTROPY,
}

data class ContentAnalysis(
    val kind: ContentKind,
    val sampleSize: Int,
    val printableRatio: Double,
    val entropyBitsPerByte: Double,
)

internal object ContentAnalyzer {

    fun inspect(sample: ByteArray, bom: BomKind?): ContentAnalysis {
        if (sample.isEmpty()) {
            return ContentAnalysis(
                kind = ContentKind.EMPTY,
                sampleSize = 0,
                printableRatio = 0.0,
                entropyBitsPerByte = 0.0,
            )
        }

        val printableRatio = decodedText(sample, bom)
            ?.let(::decodedPrintableRatio)
            ?: bytePrintableRatio(sample)
        val entropy = shannonEntropy(sample)
        val kind = when {
            printableRatio >= TEXT_PRINTABLE_THRESHOLD -> ContentKind.LIKELY_TEXT
            sample.size >= HIGH_ENTROPY_MIN_BYTES && entropy >= HIGH_ENTROPY_THRESHOLD ->
                ContentKind.HIGH_ENTROPY
            else -> ContentKind.LIKELY_BINARY
        }
        return ContentAnalysis(
            kind = kind,
            sampleSize = sample.size,
            printableRatio = printableRatio,
            entropyBitsPerByte = entropy,
        )
    }

    private fun decodedText(bytes: ByteArray, bom: BomKind?): String? = when (bom) {
        BomKind.UTF8 -> decodeWithCharset(bytes, offset = 3, charsetName = "UTF-8", unitBytes = 1)
        BomKind.UTF16_LE -> decodeWithCharset(bytes, offset = 2, charsetName = "UTF-16LE", unitBytes = 2)
        BomKind.UTF16_BE -> decodeWithCharset(bytes, offset = 2, charsetName = "UTF-16BE", unitBytes = 2)
        BomKind.UTF32_LE -> decodeUtf32(bytes, offset = 4, littleEndian = true)
        BomKind.UTF32_BE -> decodeUtf32(bytes, offset = 4, littleEndian = false)
        null -> decodeWithCharset(bytes, offset = 0, charsetName = "UTF-8", unitBytes = 1)
    }

    private fun decodeWithCharset(
        bytes: ByteArray,
        offset: Int,
        charsetName: String,
        unitBytes: Int,
    ): String? {
        if (offset > bytes.size) return null
        val available = bytes.size - offset
        val alignmentTrim = available % unitBytes
        val maxTrailingTrim = if (unitBytes == 1) MAX_UTF8_TRAILING_BYTES else unitBytes

        for (extraTrim in 0..maxTrailingTrim) {
            val length = available - alignmentTrim - extraTrim
            if (length < 0 || length % unitBytes != 0) continue
            try {
                return Charset.forName(charsetName)
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes, offset, length))
                    .toString()
            } catch (_: CharacterCodingException) {
                // A bounded sample can end halfway through a character.
            }
        }
        return null
    }

    private fun decodeUtf32(bytes: ByteArray, offset: Int, littleEndian: Boolean): String? {
        if (offset > bytes.size) return null
        val result = StringBuilder()
        var index = offset
        while (index + 4 <= bytes.size) {
            val value = if (littleEndian) {
                (bytes[index].toInt() and 0xFF) or
                        ((bytes[index + 1].toInt() and 0xFF) shl 8) or
                        ((bytes[index + 2].toInt() and 0xFF) shl 16) or
                        ((bytes[index + 3].toInt() and 0xFF) shl 24)
            } else {
                ((bytes[index].toInt() and 0xFF) shl 24) or
                        ((bytes[index + 1].toInt() and 0xFF) shl 16) or
                        ((bytes[index + 2].toInt() and 0xFF) shl 8) or
                        (bytes[index + 3].toInt() and 0xFF)
            }
            if (!Character.isValidCodePoint(value) || value in SURROGATE_CODE_POINTS) return null
            result.appendCodePoint(value)
            index += 4
        }
        return result.toString()
    }

    private fun decodedPrintableRatio(text: String): Double {
        if (text.isEmpty()) return 1.0
        var printable = 0
        var total = 0
        var index = 0
        while (index < text.length) {
            val codePoint = Character.codePointAt(text, index)
            if (!Character.isISOControl(codePoint) || Character.isWhitespace(codePoint)) printable += 1
            total += 1
            index += Character.charCount(codePoint)
        }
        return printable.toDouble() / total
    }

    private fun bytePrintableRatio(bytes: ByteArray): Double {
        val printable = bytes.count { byte ->
            val value = byte.toInt() and 0xFF
            value in PRINTABLE_ASCII || value in TEXT_WHITESPACE
        }
        return printable.toDouble() / bytes.size
    }

    private fun shannonEntropy(bytes: ByteArray): Double {
        val frequencies = IntArray(256)
        bytes.forEach { byte -> frequencies[byte.toInt() and 0xFF] += 1 }
        return frequencies.fold(0.0) { entropy, frequency ->
            if (frequency == 0) return@fold entropy
            val probability = frequency.toDouble() / bytes.size
            entropy - probability * (ln(probability) / LN_2)
        }
    }

    private const val TEXT_PRINTABLE_THRESHOLD = 0.85
    private const val HIGH_ENTROPY_MIN_BYTES = 256
    private const val HIGH_ENTROPY_THRESHOLD = 7.2
    private const val MAX_UTF8_TRAILING_BYTES = 3
    private val PRINTABLE_ASCII = 0x20..0x7E
    private val TEXT_WHITESPACE = setOf(0x09, 0x0A, 0x0C, 0x0D)
    private val SURROGATE_CODE_POINTS = 0xD800..0xDFFF
    private val LN_2 = ln(2.0)
}
