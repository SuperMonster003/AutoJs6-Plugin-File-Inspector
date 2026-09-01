package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentAnalyzerTest {

    @Test
    fun classifiesEmptyAsciiUtf8AndBomText() {
        val empty = HeaderInspector.inspect(byteArrayOf()).content
        val ascii = HeaderInspector.inspect("plain text\nwith two lines".encodeToByteArray()).content
        val utf8 = HeaderInspector.inspect("你好，世界\n".encodeToByteArray()).content
        val utf16 = HeaderInspector.inspect(
            bytes(0xFF, 0xFE) + "Hello\n".toByteArray(Charsets.UTF_16LE),
        ).content

        assertEquals(ContentKind.EMPTY, empty.kind)
        listOf(ascii, utf8, utf16).forEach { analysis ->
            assertEquals(ContentKind.LIKELY_TEXT, analysis.kind)
            assertEquals(1.0, analysis.printableRatio, 0.000_001)
        }
    }

    @Test
    fun toleratesUtf8CharacterCutByBoundedSample() {
        val data = ByteArray(InspectionPolicy.ANALYSIS_SAMPLE_BYTES + 8) { 'a'.code.toByte() }
        val leadByte = "你".encodeToByteArray().first()
        data[InspectionPolicy.ANALYSIS_SAMPLE_BYTES - 1] = leadByte

        val analysis = HeaderInspector.inspect(data).content

        assertEquals(ContentKind.LIKELY_TEXT, analysis.kind)
        assertEquals(InspectionPolicy.ANALYSIS_SAMPLE_BYTES, analysis.sampleSize)
        assertEquals(1.0, analysis.printableRatio, 0.000_001)
    }

    @Test
    fun distinguishesLowEntropyBinaryAndHighEntropyData() {
        val lowEntropy = HeaderInspector.inspect(ByteArray(1024)).content
        val highEntropy = HeaderInspector.inspect(
            ByteArray(InspectionPolicy.ANALYSIS_SAMPLE_BYTES) { index -> index.toByte() },
        ).content

        assertEquals(ContentKind.LIKELY_BINARY, lowEntropy.kind)
        assertEquals(0.0, lowEntropy.entropyBitsPerByte, 0.0)
        assertEquals(ContentKind.HIGH_ENTROPY, highEntropy.kind)
        assertEquals(8.0, highEntropy.entropyBitsPerByte, 0.000_001)
        assertTrue(highEntropy.printableRatio < 0.5)
    }

    private fun bytes(vararg values: Int): ByteArray = ByteArray(values.size) { index ->
        values[index].toByte()
    }
}
