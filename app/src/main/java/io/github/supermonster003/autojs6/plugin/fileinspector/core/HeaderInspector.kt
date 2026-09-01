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
    SEVEN_Z,
    RAR4,
    RAR5,
    GZIP,
    XZ,
    BZIP2,
    ZSTD,
    LZ4,
    TAR,
    PDF,
    PNG,
    JPEG,
    GIF87A,
    GIF89A,
    WEBP,
    ISO_BMFF,
    EBML,
    ELF,
    DEX,
    JAVA_CLASS,
    MACH_O,
    PE,
    SQLITE3,
    WOFF,
    WOFF2,
}

class HeaderSnapshot internal constructor(
    bytes: ByteArray,
    val bom: BomKind?,
    signatures: List<FileSignature>,
    val content: ContentAnalysis,
) {

    private val headerBytes = bytes.copyOf()

    val bytes: ByteArray
        get() = headerBytes.copyOf()

    val hex: String = HexCodec.encode(headerBytes, separator = " ")

    val signatures: List<FileSignature> = signatures.toList()
}

object HeaderInspector {

    fun inspect(bytes: ByteArray): HeaderSnapshot = inspect(InspectionSample.from(bytes))

    internal fun inspect(sample: InspectionSample): HeaderSnapshot {
        val sampledBytes = sample.prefix
        val bytes = sampledBytes.copyOf(minOf(sampledBytes.size, InspectionPolicy.HEADER_BYTES))
        val bom = detectBom(bytes)
        return HeaderSnapshot(
            bytes = bytes,
            bom = bom,
            signatures = detectSignatures(sample),
            content = ContentAnalyzer.inspect(sampledBytes, bom),
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

    private fun detectSignatures(sample: InspectionSample): List<FileSignature> = buildList {
        val bytes = sample.prefix
        if (ZIP_SIGNATURES.any { signature -> bytes.startsWith(signature) }) add(FileSignature.ZIP)
        if (bytes.startsWith(SEVEN_Z_SIGNATURE)) add(FileSignature.SEVEN_Z)
        if (bytes.startsWith(RAR4_SIGNATURE)) add(FileSignature.RAR4)
        if (bytes.startsWith(RAR5_SIGNATURE)) add(FileSignature.RAR5)
        if (bytes.startsWith(GZIP_SIGNATURE)) add(FileSignature.GZIP)
        if (bytes.startsWith(XZ_SIGNATURE)) add(FileSignature.XZ)
        if (isBzip2(bytes)) add(FileSignature.BZIP2)
        if (bytes.startsWith(ZSTD_SIGNATURE)) add(FileSignature.ZSTD)
        if (LZ4_SIGNATURES.any { signature -> bytes.startsWith(signature) }) add(FileSignature.LZ4)
        if (isTar(bytes)) add(FileSignature.TAR)
        if (bytes.startsWith(PDF_SIGNATURE)) add(FileSignature.PDF)
        if (bytes.startsWith(PNG_SIGNATURE)) add(FileSignature.PNG)
        if (bytes.startsWith(JPEG_SIGNATURE)) add(FileSignature.JPEG)
        if (bytes.startsWith(GIF87A_SIGNATURE)) add(FileSignature.GIF87A)
        if (bytes.startsWith(GIF89A_SIGNATURE)) add(FileSignature.GIF89A)
        if (isWebP(bytes)) add(FileSignature.WEBP)
        if (isIsoBmff(bytes)) add(FileSignature.ISO_BMFF)
        if (bytes.startsWith(EBML_SIGNATURE)) add(FileSignature.EBML)
        if (bytes.startsWith(ELF_SIGNATURE)) add(FileSignature.ELF)
        if (isDex(bytes)) add(FileSignature.DEX)
        if (isJavaClass(bytes)) add(FileSignature.JAVA_CLASS)
        if (isMachO(bytes)) add(FileSignature.MACH_O)
        if (sample.peSignature?.contentEquals(PE_SIGNATURE) == true) add(FileSignature.PE)
        if (bytes.startsWith(SQLITE3_SIGNATURE)) add(FileSignature.SQLITE3)
        if (bytes.startsWith(WOFF_SIGNATURE)) add(FileSignature.WOFF)
        if (bytes.startsWith(WOFF2_SIGNATURE)) add(FileSignature.WOFF2)
    }

    private fun isBzip2(bytes: ByteArray): Boolean {
        return bytes.startsWith(BZIP2_PREFIX) &&
                bytes.size >= BZIP2_HEADER_LENGTH &&
                (bytes[3].toInt() and 0xFF) in '1'.code..'9'.code
    }

    private fun isTar(bytes: ByteArray): Boolean {
        return bytes.matchesAt(TAR_MAGIC_OFFSET, TAR_POSIX_MAGIC) ||
                bytes.matchesAt(TAR_MAGIC_OFFSET, TAR_GNU_MAGIC)
    }

    private fun isWebP(bytes: ByteArray): Boolean {
        return bytes.startsWith(RIFF_SIGNATURE) && bytes.matchesAt(WEBP_FORM_TYPE_OFFSET, WEBP_FORM_TYPE)
    }

    private fun isIsoBmff(bytes: ByteArray): Boolean {
        if (!bytes.matchesAt(ISO_BMFF_TYPE_OFFSET, ISO_BMFF_FILE_TYPE) || bytes.size < ISO_BMFF_MIN_BOX_SIZE) {
            return false
        }
        val boxSize = bytes.readUInt32BigEndian(0)
        return when (boxSize) {
            0L -> true
            1L -> {
                val extendedSize = bytes.readUInt64BigEndianOrNull(8) ?: return false
                bytes.size >= ISO_BMFF_MIN_EXTENDED_BOX_SIZE &&
                        extendedSize >= ISO_BMFF_MIN_EXTENDED_BOX_SIZE &&
                        (extendedSize - ISO_BMFF_MIN_EXTENDED_BOX_SIZE) % ISO_BMFF_BRAND_BYTES == 0L
            }
            else -> boxSize >= ISO_BMFF_MIN_BOX_SIZE &&
                    (boxSize - ISO_BMFF_MIN_BOX_SIZE) % ISO_BMFF_BRAND_BYTES == 0L
        }
    }

    private fun isDex(bytes: ByteArray): Boolean {
        return bytes.size >= DEX_HEADER_LENGTH &&
                bytes.startsWith(DEX_PREFIX) &&
                bytes[4].isAsciiDigit() &&
                bytes[5].isAsciiDigit() &&
                bytes[6].isAsciiDigit() &&
                bytes[7] == 0.toByte()
    }

    private fun isJavaClass(bytes: ByteArray): Boolean {
        if (!bytes.startsWith(JAVA_CLASS_SIGNATURE) || bytes.size < JAVA_CLASS_MIN_HEADER_LENGTH) return false
        val majorVersion = bytes.readUInt16BigEndian(JAVA_CLASS_MAJOR_VERSION_OFFSET)
        val constantPoolCount = bytes.readUInt16BigEndian(JAVA_CLASS_CONSTANT_POOL_COUNT_OFFSET)
        return majorVersion >= JAVA_CLASS_FIRST_MAJOR_VERSION && constantPoolCount > 0
    }

    private fun isMachO(bytes: ByteArray): Boolean {
        if (MACH_O_THIN_SIGNATURES.any { signature -> bytes.startsWith(signature) }) return true
        return when {
            bytes.startsWith(MACH_O_FAT32_BE) || bytes.startsWith(MACH_O_FAT64_BE) ->
                hasPlausibleFatMachHeader(bytes, littleEndian = false)
            bytes.startsWith(MACH_O_FAT32_LE) || bytes.startsWith(MACH_O_FAT64_LE) ->
                hasPlausibleFatMachHeader(bytes, littleEndian = true)
            else -> false
        }
    }

    private fun hasPlausibleFatMachHeader(bytes: ByteArray, littleEndian: Boolean): Boolean {
        if (bytes.size < MACH_O_FAT_FIRST_ARCH_BYTES) return false
        val architectureCount = bytes.readUInt32(4, littleEndian)
        val cpuType = bytes.readUInt32(8, littleEndian)
        val baseCpuType = cpuType and MACH_O_CPU_TYPE_MASK
        return architectureCount in 1L..MACH_O_MAX_PLAUSIBLE_ARCHITECTURES &&
                baseCpuType in MACH_O_KNOWN_BASE_CPU_TYPES
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        return matchesAt(offset = 0, expected = prefix)
    }

    private fun ByteArray.matchesAt(offset: Int, expected: ByteArray): Boolean {
        if (offset < 0 || size - offset < expected.size) return false
        return expected.indices.all { index -> this[offset + index] == expected[index] }
    }

    private fun ByteArray.readUInt16BigEndian(offset: Int): Int {
        return ((this[offset].toInt() and 0xFF) shl 8) or
                (this[offset + 1].toInt() and 0xFF)
    }

    private fun ByteArray.readUInt32BigEndian(offset: Int): Long = readUInt32(offset, littleEndian = false)

    private fun ByteArray.readUInt32(offset: Int, littleEndian: Boolean): Long {
        var value = 0L
        val indices = if (littleEndian) (offset + 3 downTo offset) else (offset..offset + 3)
        indices.forEach { index -> value = (value shl 8) or (this[index].toLong() and 0xFFL) }
        return value
    }

    private fun ByteArray.readUInt64BigEndianOrNull(offset: Int): Long? {
        if (size - offset < Long.SIZE_BYTES || this[offset] < 0) return null
        var value = 0L
        repeat(Long.SIZE_BYTES) { index ->
            value = (value shl 8) or (this[offset + index].toLong() and 0xFFL)
        }
        return value
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
    private val SEVEN_Z_SIGNATURE = bytes(0x37, 0x7A, 0xBC, 0xAF, 0x27, 0x1C)
    private val RAR4_SIGNATURE = bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00)
    private val RAR5_SIGNATURE = bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00)
    private val GZIP_SIGNATURE = bytes(0x1F, 0x8B)
    private val XZ_SIGNATURE = bytes(0xFD, 0x37, 0x7A, 0x58, 0x5A, 0x00)
    private val BZIP2_PREFIX = "BZh".encodeToByteArray()
    private const val BZIP2_HEADER_LENGTH = 4
    private val ZSTD_SIGNATURE = bytes(0x28, 0xB5, 0x2F, 0xFD)
    private val LZ4_SIGNATURES = listOf(
        bytes(0x04, 0x22, 0x4D, 0x18),
        bytes(0x02, 0x21, 0x4C, 0x18),
    )
    private const val TAR_MAGIC_OFFSET = 257
    private val TAR_POSIX_MAGIC = bytes(0x75, 0x73, 0x74, 0x61, 0x72, 0x00)
    private val TAR_GNU_MAGIC = bytes(0x75, 0x73, 0x74, 0x61, 0x72, 0x20)
    private val PDF_SIGNATURE = "%PDF-".encodeToByteArray()
    private val PNG_SIGNATURE = bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    private val JPEG_SIGNATURE = bytes(0xFF, 0xD8, 0xFF)
    private val GIF87A_SIGNATURE = "GIF87a".encodeToByteArray()
    private val GIF89A_SIGNATURE = "GIF89a".encodeToByteArray()
    private val RIFF_SIGNATURE = "RIFF".encodeToByteArray()
    private const val WEBP_FORM_TYPE_OFFSET = 8
    private val WEBP_FORM_TYPE = "WEBP".encodeToByteArray()
    private const val ISO_BMFF_TYPE_OFFSET = 4
    private val ISO_BMFF_FILE_TYPE = "ftyp".encodeToByteArray()
    private const val ISO_BMFF_MIN_BOX_SIZE = 16
    private const val ISO_BMFF_MIN_EXTENDED_BOX_SIZE = 24L
    private const val ISO_BMFF_BRAND_BYTES = 4L
    private val EBML_SIGNATURE = bytes(0x1A, 0x45, 0xDF, 0xA3)
    private val ELF_SIGNATURE = bytes(0x7F, 0x45, 0x4C, 0x46)
    private val DEX_PREFIX = bytes(0x64, 0x65, 0x78, 0x0A)
    private const val DEX_HEADER_LENGTH = 8
    private val JAVA_CLASS_SIGNATURE = bytes(0xCA, 0xFE, 0xBA, 0xBE)
    private const val JAVA_CLASS_MIN_HEADER_LENGTH = 10
    private const val JAVA_CLASS_MAJOR_VERSION_OFFSET = 6
    private const val JAVA_CLASS_CONSTANT_POOL_COUNT_OFFSET = 8
    private const val JAVA_CLASS_FIRST_MAJOR_VERSION = 45
    private val MACH_O_THIN_SIGNATURES = listOf(
        bytes(0xFE, 0xED, 0xFA, 0xCE),
        bytes(0xCE, 0xFA, 0xED, 0xFE),
        bytes(0xFE, 0xED, 0xFA, 0xCF),
        bytes(0xCF, 0xFA, 0xED, 0xFE),
    )
    private val MACH_O_FAT32_BE = bytes(0xCA, 0xFE, 0xBA, 0xBE)
    private val MACH_O_FAT32_LE = bytes(0xBE, 0xBA, 0xFE, 0xCA)
    private val MACH_O_FAT64_BE = bytes(0xCA, 0xFE, 0xBA, 0xBF)
    private val MACH_O_FAT64_LE = bytes(0xBF, 0xBA, 0xFE, 0xCA)
    private const val MACH_O_FAT_FIRST_ARCH_BYTES = 12
    private const val MACH_O_MAX_PLAUSIBLE_ARCHITECTURES = 32L
    private const val MACH_O_CPU_TYPE_MASK = 0x00FF_FFFFL
    private val MACH_O_KNOWN_BASE_CPU_TYPES = setOf(7L, 12L, 18L)
    private val PE_SIGNATURE = bytes(0x50, 0x45, 0x00, 0x00)
    private val SQLITE3_SIGNATURE = "SQLite format 3\u0000".encodeToByteArray()
    private val WOFF_SIGNATURE = "wOFF".encodeToByteArray()
    private val WOFF2_SIGNATURE = "wOF2".encodeToByteArray()

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
        values[index].toByte()
    }
}
