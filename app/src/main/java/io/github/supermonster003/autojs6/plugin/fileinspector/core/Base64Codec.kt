package io.github.supermonster003.autojs6.plugin.fileinspector.core

/** Strict RFC 4648 standard-base64 decoding without relying on an Android API-level-specific codec. */
internal object Base64Codec {

    fun decode(value: String): ByteArray? {
        if (value.isEmpty()) return ByteArray(0)

        val paddingStart = value.indexOf('=')
        val contentLength = if (paddingStart >= 0) paddingStart else value.length
        val paddingCount = value.length - contentLength
        if (paddingCount > MAX_PADDING_CHARACTERS) return null
        if (paddingStart >= 0) {
            if (value.length % CHARACTERS_PER_QUANTUM != 0) return null
            if (value.substring(paddingStart).any { it != '=' }) return null
        }

        val remainder = contentLength % CHARACTERS_PER_QUANTUM
        if (remainder == INVALID_REMAINDER) return null
        val requiredPadding = when (remainder) {
            0 -> 0
            2 -> 2
            else -> 1
        }
        if (paddingCount != 0 && paddingCount != requiredPadding) return null

        val output = ByteArray(contentLength * BITS_PER_CHARACTER / BITS_PER_BYTE)
        var bitBuffer = 0
        var bitCount = 0
        var outputIndex = 0
        for (index in 0 until contentLength) {
            val decoded = value[index].decodeBase64Character() ?: return null
            bitBuffer = (bitBuffer shl BITS_PER_CHARACTER) or decoded
            bitCount += BITS_PER_CHARACTER
            if (bitCount >= BITS_PER_BYTE) {
                bitCount -= BITS_PER_BYTE
                output[outputIndex++] = (bitBuffer ushr bitCount).toByte()
                bitBuffer = bitBuffer and ((1 shl bitCount) - 1)
            }
        }

        // Reject non-zero unused bits so one digest has only its canonical Base64 spelling.
        if (bitBuffer != 0 || outputIndex != output.size) return null
        return output
    }

    private fun Char.decodeBase64Character(): Int? = when (this) {
        in 'A'..'Z' -> code - 'A'.code
        in 'a'..'z' -> code - 'a'.code + 26
        in '0'..'9' -> code - '0'.code + 52
        '+' -> 62
        '/' -> 63
        else -> null
    }

    private const val BITS_PER_BYTE = 8
    private const val BITS_PER_CHARACTER = 6
    private const val CHARACTERS_PER_QUANTUM = 4
    private const val INVALID_REMAINDER = 1
    private const val MAX_PADDING_CHARACTERS = 2
}
