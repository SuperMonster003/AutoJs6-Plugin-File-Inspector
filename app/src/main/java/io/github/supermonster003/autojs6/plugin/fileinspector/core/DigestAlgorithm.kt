package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.util.Locale

enum class DigestAlgorithm(
    val id: String,
    val byteCount: Int,
    internal val jcaName: String?,
) {
    CRC32("crc32", 4, null),
    MD5("md5", 16, "MD5"),
    SHA1("sha1", 20, "SHA-1"),
    SHA224("sha224", 28, "SHA-224"),
    SHA256("sha256", 32, "SHA-256"),
    SHA384("sha384", 48, "SHA-384"),
    SHA512("sha512", 64, "SHA-512"),
    ;

    internal companion object {

        fun fromInputName(value: String): DigestAlgorithm? = when (value.lowercase(Locale.ROOT)) {
            "crc32" -> CRC32
            "md5" -> MD5
            "sha1", "sha-1" -> SHA1
            "sha224", "sha-224" -> SHA224
            "sha256", "sha-256" -> SHA256
            "sha384", "sha-384" -> SHA384
            "sha512", "sha-512" -> SHA512
            else -> null
        }

        fun fromHexLength(length: Int): DigestAlgorithm? =
            fromByteCount(length / HEX_CHARACTERS_PER_BYTE)
                ?.takeIf { length % HEX_CHARACTERS_PER_BYTE == 0 }

        fun fromByteCount(byteCount: Int): DigestAlgorithm? = entries.singleOrNull {
            it.byteCount == byteCount
        }

        private const val HEX_CHARACTERS_PER_BYTE = 2
    }
}
