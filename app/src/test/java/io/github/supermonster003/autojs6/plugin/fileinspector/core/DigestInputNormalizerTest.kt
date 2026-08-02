package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DigestInputNormalizerTest {

    @Test
    fun infersEveryAlgorithmFromUniqueHexLength() {
        DigestAlgorithm.entries.forEach { algorithm ->
            val input = "aB".repeat(algorithm.byteCount)

            val result = valid(input)

            assertEquals(algorithm, result.algorithm)
            assertEquals("ab".repeat(algorithm.byteCount), result.canonicalHex)
        }
    }

    @Test
    fun acceptsEverySupportedPrefixAlias() {
        val cases = mapOf(
            "CRC32" to DigestAlgorithm.CRC32,
            "md5" to DigestAlgorithm.MD5,
            "SHA1" to DigestAlgorithm.SHA1,
            "sha-1" to DigestAlgorithm.SHA1,
            "SHA256" to DigestAlgorithm.SHA256,
            "sha-256" to DigestAlgorithm.SHA256,
            "SHA512" to DigestAlgorithm.SHA512,
            "sha-512" to DigestAlgorithm.SHA512,
        )

        cases.forEach { (prefix, algorithm) ->
            val result = valid("$prefix: ${"AB".repeat(algorithm.byteCount)}")
            assertEquals(algorithm, result.algorithm)
        }
    }

    @Test
    fun acceptsEqualsPrefixFingerprintSeparatorsAndCrcHexPrefix() {
        val sha256 = "01".repeat(DigestAlgorithm.SHA256.byteCount)
        val colonFingerprint = sha256.chunked(2).joinToString(":")
        val dashFingerprint = sha256.chunked(2).joinToString("-")
        val spaceFingerprint = sha256.chunked(2).joinToString(" ")

        assertEquals(sha256, valid("SHA-256=$colonFingerprint").canonicalHex)
        assertEquals(sha256, valid(dashFingerprint).canonicalHex)
        assertEquals(sha256, valid(spaceFingerprint).canonicalHex)
        assertEquals("00abcdef", valid("  CRC32: 0x00ABCDEF\r\n").canonicalHex)
    }

    @Test
    fun algorithmHintIsUsedAndConflictsAreRejected() {
        val md5 = "00".repeat(DigestAlgorithm.MD5.byteCount)

        assertEquals(DigestAlgorithm.MD5, valid(md5, DigestAlgorithm.MD5).algorithm)
        assertInvalid(
            raw = "SHA-256:$md5",
            expected = DigestInputError.ALGORITHM_CONFLICT,
            hint = DigestAlgorithm.MD5,
        )
        assertInvalid(
            raw = "0x$md5",
            expected = DigestInputError.ALGORITHM_CONFLICT,
            hint = DigestAlgorithm.MD5,
        )
    }

    @Test
    fun rejectsMalformedOrAmbiguousInputs() {
        val sha256 = "00".repeat(DigestAlgorithm.SHA256.byteCount)
        val cases = listOf(
            "" to DigestInputError.EMPTY,
            "   \r\n" to DigestInputError.EMPTY,
            "abc" to DigestInputError.ODD_LENGTH,
            "00" to DigestInputError.UNKNOWN_LENGTH,
            "gg".repeat(16) to DigestInputError.INVALID_CHARACTER,
            "sha999:$sha256" to DigestInputError.UNKNOWN_ALGORITHM,
            "00:00-${"00".repeat(DigestAlgorithm.SHA256.byteCount - 2)}" to
                    DigestInputError.INVALID_SEPARATOR,
            "$sha256\n$sha256" to DigestInputError.MULTILINE,
            "$sha256 file.bin" to DigestInputError.INVALID_CHARACTER,
            "１２３４５６７８" to DigestInputError.INVALID_CHARACTER,
            "00\u200B00" to DigestInputError.INVALID_CHARACTER,
            "a".repeat(513) to DigestInputError.TOO_LONG,
        )

        cases.forEach { (raw, error) -> assertInvalid(raw, error) }
    }

    @Test
    fun rejectsWrongLengthForExplicitAlgorithm() {
        assertInvalid(
            raw = "SHA-256:${"00".repeat(DigestAlgorithm.MD5.byteCount)}",
            expected = DigestInputError.LENGTH_MISMATCH,
        )
        assertInvalid(
            raw = "00".repeat(DigestAlgorithm.MD5.byteCount),
            expected = DigestInputError.LENGTH_MISMATCH,
            hint = DigestAlgorithm.SHA256,
        )
    }

    private fun valid(
        raw: String,
        hint: DigestAlgorithm? = null,
    ): ExpectedDigest {
        val result = DigestInputNormalizer.parse(raw, hint)
        assertTrue("Expected valid input but got $result", result is DigestParseResult.Valid)
        return (result as DigestParseResult.Valid).value
    }

    private fun assertInvalid(
        raw: String,
        expected: DigestInputError,
        hint: DigestAlgorithm? = null,
    ) {
        val result = DigestInputNormalizer.parse(raw, hint)
        assertTrue("Expected invalid input but got $result", result is DigestParseResult.Invalid)
        assertEquals(expected, (result as DigestParseResult.Invalid).reason)
    }
}
