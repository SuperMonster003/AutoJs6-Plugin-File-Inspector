package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HeaderInspectorTest {

    @Test
    fun recognizesSupportedFileSignaturesAtOffsetZero() {
        val cases = mapOf(
            FileSignature.ZIP to bytes(0x50, 0x4B, 0x03, 0x04, 0x01),
            FileSignature.GZIP to bytes(0x1F, 0x8B, 0x08),
            FileSignature.PDF to "%PDF-1.7".encodeToByteArray(),
            FileSignature.PNG to bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01),
            FileSignature.JPEG to bytes(0xFF, 0xD8, 0xFF, 0xE0),
            FileSignature.GIF87A to "GIF87a...".encodeToByteArray(),
            FileSignature.GIF89A to "GIF89a...".encodeToByteArray(),
            FileSignature.ELF to bytes(0x7F, 0x45, 0x4C, 0x46, 0x02),
            FileSignature.DEX to bytes(0x64, 0x65, 0x78, 0x0A, 0x30, 0x33, 0x35, 0x00),
            FileSignature.SQLITE3 to "SQLite format 3\u0000tail".encodeToByteArray(),
        )

        cases.forEach { (signature, input) ->
            assertEquals(listOf(signature), HeaderInspector.inspect(input).signatures)
            assertTrue(HeaderInspector.inspect(byteArrayOf(0) + input).signatures.isEmpty())
        }
    }

    @Test
    fun recognizesAllStandardZipLeadSignatures() {
        val signatures = listOf(
            bytes(0x50, 0x4B, 0x03, 0x04),
            bytes(0x50, 0x4B, 0x05, 0x06),
            bytes(0x50, 0x4B, 0x07, 0x08),
        )

        signatures.forEach { signature ->
            assertEquals(listOf(FileSignature.ZIP), HeaderInspector.inspect(signature).signatures)
        }
    }

    @Test
    fun recognizesExpandedSignaturesIncludingFixedOffsets() {
        val cases = mapOf(
            FileSignature.SEVEN_Z to bytes(0x37, 0x7A, 0xBC, 0xAF, 0x27, 0x1C),
            FileSignature.RAR4 to bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x00),
            FileSignature.RAR5 to bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01, 0x00),
            FileSignature.XZ to bytes(0xFD, 0x37, 0x7A, 0x58, 0x5A, 0x00),
            FileSignature.BZIP2 to "BZh9".encodeToByteArray(),
            FileSignature.ZSTD to bytes(0x28, 0xB5, 0x2F, 0xFD),
            FileSignature.LZ4 to bytes(0x04, 0x22, 0x4D, 0x18),
            FileSignature.TAR to tarHeader(),
            FileSignature.WEBP to "RIFF\u0004\u0000\u0000\u0000WEBP".encodeToByteArray(),
            FileSignature.ISO_BMFF to isoBmffHeader(),
            FileSignature.EBML to bytes(0x1A, 0x45, 0xDF, 0xA3),
            FileSignature.JAVA_CLASS to javaClassHeader(),
            FileSignature.MACH_O to bytes(0xCF, 0xFA, 0xED, 0xFE),
            FileSignature.PE to peHeader(peOffset = 64, totalSize = 68),
            FileSignature.WOFF to "wOFF".encodeToByteArray(),
            FileSignature.WOFF2 to "wOF2".encodeToByteArray(),
        )

        cases.forEach { (signature, input) ->
            assertEquals(signature.name, listOf(signature), HeaderInspector.inspect(input).signatures)
            assertTrue(signature.name, HeaderInspector.inspect(byteArrayOf(0) + input).signatures.isEmpty())
        }
    }

    @Test
    fun recognizesLegacyLz4AndGnuTarVariants() {
        val legacyLz4 = bytes(0x02, 0x21, 0x4C, 0x18)
        val gnuTar = ByteArray(512).apply {
            "ustar ".encodeToByteArray().copyInto(this, destinationOffset = 257)
        }

        assertEquals(listOf(FileSignature.LZ4), HeaderInspector.inspect(legacyLz4).signatures)
        assertEquals(listOf(FileSignature.TAR), HeaderInspector.inspect(gnuTar).signatures)
    }

    @Test
    fun disambiguatesJavaClassAndFatMachOHeaders() {
        val javaClass = javaClassHeader()
        val fatMachO = bytes(
            0xCA, 0xFE, 0xBA, 0xBE,
            0x00, 0x00, 0x00, 0x01,
            0x01, 0x00, 0x00, 0x0C,
        )

        assertEquals(listOf(FileSignature.JAVA_CLASS), HeaderInspector.inspect(javaClass).signatures)
        assertEquals(listOf(FileSignature.MACH_O), HeaderInspector.inspect(fatMachO).signatures)
        assertTrue(HeaderInspector.inspect(bytes(0xCA, 0xFE, 0xBA, 0xBE)).signatures.isEmpty())
    }

    @Test
    fun rejectsExpandedSignatureNearMissesAndTruncation() {
        val nearMisses = listOf(
            "BZh0".encodeToByteArray(),
            "RIFF\u0004\u0000\u0000\u0000NOTP".encodeToByteArray(),
            bytes(0x00, 0x00, 0x00, 0x0C) + "ftypisom".encodeToByteArray(),
            ByteArray(263).apply { "ustarX".encodeToByteArray().copyInto(this, 257) },
            peHeader(peOffset = 64, totalSize = 68).apply { this[67] = 1 },
        )
        val truncated = listOf(
            bytes(0x37, 0x7A, 0xBC, 0xAF, 0x27),
            bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07),
            bytes(0x52, 0x61, 0x72, 0x21, 0x1A, 0x07, 0x01),
            bytes(0xFD, 0x37, 0x7A, 0x58, 0x5A),
            "BZh".encodeToByteArray(),
            bytes(0x28, 0xB5, 0x2F),
            bytes(0x04, 0x22, 0x4D),
            tarHeader().copyOf(262),
            "RIFF\u0004\u0000\u0000\u0000WEB".encodeToByteArray(),
            isoBmffHeader().copyOf(15),
            bytes(0x1A, 0x45, 0xDF),
            javaClassHeader().copyOf(9),
            bytes(0xCF, 0xFA, 0xED),
            peHeader(peOffset = 64, totalSize = 68).copyOf(67),
            "wOF".encodeToByteArray(),
        )

        (nearMisses + truncated).forEach { input ->
            assertTrue(HeaderInspector.inspect(input).signatures.isEmpty())
        }
    }

    @Test
    fun rejectsTruncatedSignaturesAndMalformedDexVersion() {
        val signatures = listOf(
            bytes(0x50, 0x4B, 0x03, 0x04),
            bytes(0x1F, 0x8B),
            "%PDF-".encodeToByteArray(),
            bytes(0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A),
            bytes(0xFF, 0xD8, 0xFF),
            "GIF87a".encodeToByteArray(),
            "GIF89a".encodeToByteArray(),
            bytes(0x7F, 0x45, 0x4C, 0x46),
            "SQLite format 3\u0000".encodeToByteArray(),
        )

        signatures.forEach { signature ->
            assertTrue(HeaderInspector.inspect(signature.copyOf(signature.size - 1)).signatures.isEmpty())
        }
        assertTrue(
            HeaderInspector.inspect(bytes(0x64, 0x65, 0x78, 0x0A, 0x30, 0x78, 0x35, 0x00))
                .signatures.isEmpty(),
        )
        assertTrue(
            HeaderInspector.inspect(bytes(0x64, 0x65, 0x78, 0x0A, 0x30, 0x33, 0x35, 0x01))
                .signatures.isEmpty(),
        )
    }

    @Test
    fun detectsBomLongestFirst() {
        val cases = mapOf(
            BomKind.UTF8 to bytes(0xEF, 0xBB, 0xBF, 0x41),
            BomKind.UTF16_LE to bytes(0xFF, 0xFE, 0x41, 0x00),
            BomKind.UTF16_BE to bytes(0xFE, 0xFF, 0x00, 0x41),
            BomKind.UTF32_LE to bytes(0xFF, 0xFE, 0x00, 0x00, 0x41),
            BomKind.UTF32_BE to bytes(0x00, 0x00, 0xFE, 0xFF, 0x41),
        )

        cases.forEach { (bom, input) -> assertEquals(bom, HeaderInspector.inspect(input).bom) }
        assertEquals(BomKind.UTF32_LE, HeaderInspector.inspect(bytes(0xFF, 0xFE, 0x00, 0x00)).bom)
        assertNull(HeaderInspector.inspect(bytes(0xEF, 0xBB)).bom)
    }

    @Test
    fun truncatesAndDefensivelyCopiesHeader() {
        val original = ByteArray(InspectionPolicy.ANALYSIS_SAMPLE_BYTES + 100) { index -> index.toByte() }
        val snapshot = HeaderInspector.inspect(original)
        val returned = snapshot.bytes

        original.fill(0x7F)
        returned.fill(0x55)

        assertEquals(InspectionPolicy.HEADER_BYTES, snapshot.bytes.size)
        assertEquals(0.toByte(), snapshot.bytes.first())
        assertEquals(63.toByte(), snapshot.bytes.last())
        assertEquals(InspectionPolicy.ANALYSIS_SAMPLE_BYTES, snapshot.content.sampleSize)
        assertTrue(snapshot.hex.startsWith("00 01 02 03"))
    }

    private fun tarHeader(): ByteArray = ByteArray(512).apply {
        "ustar\u0000".encodeToByteArray().copyInto(this, destinationOffset = 257)
    }

    private fun isoBmffHeader(): ByteArray = bytes(
        0x00, 0x00, 0x00, 0x10,
        0x66, 0x74, 0x79, 0x70,
        0x69, 0x73, 0x6F, 0x6D,
        0x00, 0x00, 0x00, 0x00,
    )

    private fun javaClassHeader(): ByteArray = bytes(
        0xCA, 0xFE, 0xBA, 0xBE,
        0x00, 0x00, 0x00, 0x3D,
        0x00, 0x01,
    )

    private fun peHeader(peOffset: Int, totalSize: Int): ByteArray = ByteArray(totalSize).apply {
        this[0] = 'M'.code.toByte()
        this[1] = 'Z'.code.toByte()
        this[0x3C] = peOffset.toByte()
        this[0x3D] = (peOffset ushr 8).toByte()
        this[0x3E] = (peOffset ushr 16).toByte()
        this[0x3F] = (peOffset ushr 24).toByte()
        bytes(0x50, 0x45, 0x00, 0x00).copyInto(this, destinationOffset = peOffset)
    }

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
        values[index].toByte()
    }
}
