package io.github.supermonster003.autojs6.plugin.fileinspector.core

class DigestValue internal constructor(
    val algorithm: DigestAlgorithm,
    bytes: ByteArray,
) {

    private val digestBytes = bytes.copyOf()

    init {
        require(digestBytes.size == algorithm.byteCount) {
            "${algorithm.id} requires ${algorithm.byteCount} bytes, but received ${digestBytes.size}"
        }
    }

    val hex: String = HexCodec.encode(digestBytes)

    fun copyBytes(): ByteArray = digestBytes.copyOf()

    internal fun copyBytesForComparison(): ByteArray = digestBytes.copyOf()

    override fun toString(): String = hex
}

internal object HexCodec {

    private val digits = "0123456789abcdef".toCharArray()

    fun encode(bytes: ByteArray, separator: String = ""): String {
        if (bytes.isEmpty()) return ""
        val separatorCharacters = separator.length * (bytes.size - 1)
        return buildString(bytes.size * 2 + separatorCharacters) {
            bytes.forEachIndexed { index, byte ->
                if (index > 0) append(separator)
                val value = byte.toInt() and 0xFF
                append(digits[value ushr 4])
                append(digits[value and 0x0F])
            }
        }
    }

    fun decode(value: String): ByteArray {
        require(value.length % 2 == 0) { "Hex input must contain an even number of characters" }
        return ByteArray(value.length / 2) { index ->
            val high = value[index * 2].digitToInt(16)
            val low = value[index * 2 + 1].digitToInt(16)
            ((high shl 4) or low).toByte()
        }
    }
}
