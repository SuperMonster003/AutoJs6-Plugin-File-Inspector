package io.github.supermonster003.autojs6.plugin.fileinspector.core

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class DigestVerifierTest {

    @Test
    fun verifiesNormalizedExpectedDigest() {
        val report = reportFor("123456789".encodeToByteArray())
        val expected = (DigestInputNormalizer.parse(
            "SHA-256:15E2B0D3C33891EBB0F1EF609EC419420C20E320CE94C65FBC8C3312448EB225",
        ) as DigestParseResult.Valid).value
        val mismatch = (DigestInputNormalizer.parse(
            "SHA-256:05E2B0D3C33891EBB0F1EF609EC419420C20E320CE94C65FBC8C3312448EB225",
        ) as DigestParseResult.Valid).value

        assertEquals(VerificationStatus.MATCH, DigestVerifier.verify(report, expected))
        assertEquals(VerificationStatus.MISMATCH, DigestVerifier.verify(report, mismatch))
    }

    @Test
    fun comparesEveryByteWithoutMismatchPositionShortCircuit() {
        val actual = ByteArray(32) { index -> index.toByte() }

        assertTrue(DigestVerifier.constantTimeEqualsSameLength(actual, actual.copyOf()))
        listOf(0, actual.lastIndex / 2, actual.lastIndex).forEach { index ->
            val different = actual.copyOf().apply { this[index] = (this[index] + 1).toByte() }
            assertFalse(DigestVerifier.constantTimeEqualsSameLength(actual, different))
        }
        assertFalse(DigestVerifier.constantTimeEqualsSameLength(actual, actual.copyOf(31)))
    }

    @Test
    fun digestValueDefensivelyCopiesInputAndOutput() {
        val original = ByteArray(DigestAlgorithm.MD5.byteCount) { it.toByte() }
        val value = DigestValue(DigestAlgorithm.MD5, original)
        val expectedHex = value.hex

        original.fill(0x7F)
        value.copyBytes().fill(0x55)

        assertEquals(expectedHex, value.hex)
        assertFalse(value.copyBytes().all { it == 0x55.toByte() })
    }

    private fun reportFor(data: ByteArray): InspectionReport = runBlocking {
        FileInspectionEngine().inspect(
            object : InspectorSource {
                override val declaredSize: Long = data.size.toLong()
                override fun openStream(): InputStream = ByteArrayInputStream(data)
            },
        )
    }
}
