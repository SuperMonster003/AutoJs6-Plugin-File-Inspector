package io.github.supermonster003.autojs6.plugin.fileinspector.core

enum class BomKind {
    UTF8,
    UTF16_LE,
    UTF16_BE,
    UTF32_LE,
    UTF32_BE,
}

enum class FileSignature {
    ZIP,
    GZIP,
    PDF,
    PNG,
    JPEG,
    GIF87A,
    GIF89A,
    ELF,
    DEX,
    SQLITE3,
}

class HeaderSnapshot internal constructor(
    bytes: ByteArray,
    val bom: BomKind?,
    signatures: List<FileSignature>,
) {

    private val headerBytes = bytes.copyOf()

    val bytes: ByteArray
        get() = headerBytes.copyOf()

    val hex: String = HexCodec.encode(headerBytes, separator = " ")

    val signatures: List<FileSignature> = signatures.toList()
}

object HeaderInspector {

    fun inspect(prefix: ByteArray): HeaderSnapshot {
        val bytes = prefix.copyOf(minOf(prefix.size, InspectionPolicy.HEADER_BYTES))
        return HeaderSnapshot(
            bytes = bytes,
            bom = detectBom(bytes),
            signatures = detectSignatures(bytes),
        )
    }

    private fun detectBom(bytes: ByteArray): BomKind? = when {
        bytes.startsWith(UTF32_BE_BOM) -> BomKind.UTF32_BE
        bytes.startsWith(UTF32_LE_BOM) -> BomKind.UTF32_LE
        bytes.startsWith(UTF8_BOM) -> BomKind.UTF8
        bytes.startsWith(UTF16_BE_BOM) -> BomKind.UTF16_BE
        bytes.startsWith(UTF16_LE_BOM) -> BomKind.UTF16_LE
        else -> null
    }

    private fun detectSignatures(bytes: ByteArray): List<FileSignature> = buildList {
        if (ZIP_SIGNATURES.any { signature -> bytes.startsWith(signature) }) add(FileSignature.ZIP)
        if (bytes.startsWith(GZIP_SIGNATURE)) add(FileSignature.GZIP)
        if (bytes.startsWith(PDF_SIGNATURE)) add(FileSignature.PDF)
        if (bytes.startsWith(PNG_SIGNATURE)) add(FileSignature.PNG)
        if (bytes.startsWith(JPEG_SIGNATURE)) add(FileSignature.JPEG)
        if (bytes.startsWith(GIF87A_SIGNATURE)) add(FileSignature.GIF87A)
        if (bytes.startsWith(GIF89A_SIGNATURE)) add(FileSignature.GIF89A)
        if (bytes.startsWith(ELF_SIGNATURE)) add(FileSignature.ELF)
        if (isDex(bytes)) add(FileSignature.DEX)
        if (bytes.startsWith(SQLITE3_SIGNATURE)) add(FileSignature.SQLITE3)
    }

    private fun isDex(bytes: ByteArray): Boolean {
        return bytes.size >= DEX_HEADER_LENGTH &&
                bytes.startsWith(DEX_PREFIX) &&
                bytes[4].isAsciiDigit() &&
                bytes[5].isAsciiDigit() &&
                bytes[6].isAsciiDigit() &&
                bytes[7] == 0.toByte()
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (size < prefix.size) return false
        return prefix.indices.all { index -> this[index] == prefix[index] }
    }

    private fun Byte.isAsciiDigit(): Boolean = (toInt() and 0xFF) in '0'.code..'9'.code

    private val UTF8_BOM = bytes(0xEF, 0xBB, 0xBF)
    private val UTF16_LE_BOM = bytes(0xFF, 0xFE)
    private val UTF16_BE_BOM = bytes(0xFE, 0xFF)
    private val UTF32_LE_BOM = bytes(0xFF, 0xFE, 0x00, 0x00)
    private val UTF32_BE_BOM = bytes(0x00, 0x00, 0xFE, 0xFF)

    private val ZIP_SIGNATURES = listOf(
        bytes(0x50, 0x4B, 0x03, 0x04),
        bytes(0x50, 0x4B, 0x05, 0x06),
        bytes(0x50, 0x4B, 0x07, 0x08),
    )
    private val GZIP_SIGNATURE = bytes(0x1F, 0x8B)
    private val PDF_SIGNATURE = "%PDF-".encodeToByteArray()
    private val PNG_SIGNATURE = bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    private val JPEG_SIGNATURE = bytes(0xFF, 0xD8, 0xFF)
    private val GIF87A_SIGNATURE = "GIF87a".encodeToByteArray()
    private val GIF89A_SIGNATURE = "GIF89a".encodeToByteArray()
    private val ELF_SIGNATURE = bytes(0x7F, 0x45, 0x4C, 0x46)
    private val DEX_PREFIX = bytes(0x64, 0x65, 0x78, 0x0A)
    private const val DEX_HEADER_LENGTH = 8
    private val SQLITE3_SIGNATURE = "SQLite format 3\u0000".encodeToByteArray()

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
        values[index].toByte()
    }
}
