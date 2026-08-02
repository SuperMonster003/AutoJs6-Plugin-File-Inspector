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
    SHA256("sha256", 32, "SHA-256"),
    SHA512("sha512", 64, "SHA-512"),
    ;

    internal companion object {

        fun fromInputName(value: String): DigestAlgorithm? = when (value.lowercase(Locale.ROOT)) {
            "crc32" -> CRC32
            "md5" -> MD5
            "sha1", "sha-1" -> SHA1
            "sha256", "sha-256" -> SHA256
            "sha512", "sha-512" -> SHA512
            else -> null
        }

        fun fromHexLength(length: Int): DigestAlgorithm? = entries.singleOrNull {
            it.byteCount * HEX_CHARACTERS_PER_BYTE == length
        }

        private const val HEX_CHARACTERS_PER_BYTE = 2
    }
}
