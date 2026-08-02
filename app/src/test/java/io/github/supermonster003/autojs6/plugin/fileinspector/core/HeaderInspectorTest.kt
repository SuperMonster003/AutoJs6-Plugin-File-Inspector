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
        val original = ByteArray(100) { index -> index.toByte() }
        val snapshot = HeaderInspector.inspect(original)
        val returned = snapshot.bytes

        original.fill(0x7F)
        returned.fill(0x55)

        assertEquals(InspectionPolicy.HEADER_BYTES, snapshot.bytes.size)
        assertEquals(0.toByte(), snapshot.bytes.first())
        assertEquals(63.toByte(), snapshot.bytes.last())
        assertTrue(snapshot.hex.startsWith("00 01 02 03"))
    }

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
        values[index].toByte()
    }
}
