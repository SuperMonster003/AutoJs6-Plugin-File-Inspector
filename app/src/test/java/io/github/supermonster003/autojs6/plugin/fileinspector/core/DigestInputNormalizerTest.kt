package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

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
            "SHA224" to DigestAlgorithm.SHA224,
            "sha-224" to DigestAlgorithm.SHA224,
            "SHA256" to DigestAlgorithm.SHA256,
            "sha-256" to DigestAlgorithm.SHA256,
            "SHA384" to DigestAlgorithm.SHA384,
            "sha-384" to DigestAlgorithm.SHA384,
            "SHA512" to DigestAlgorithm.SHA512,
            "sha-512" to DigestAlgorithm.SHA512,
        )

        cases.forEach { (prefix, algorithm) ->
            val result = valid("$prefix: ${"AB".repeat(algorithm.byteCount)}")
            assertEquals(algorithm, result.algorithm)
        }
    }

    @Test
    fun acceptsCoreutilsTextAndBinaryOutputLinesAndPreservesFileName() {
        val sha256 = "12".repeat(DigestAlgorithm.SHA256.byteCount)
        val md5 = "34".repeat(DigestAlgorithm.MD5.byteCount)

        val textMode = valid("$sha256  release 文件.apk")
        val binaryMode = valid("$md5 *archive.bin")

        assertEquals(DigestAlgorithm.SHA256, textMode.algorithm)
        assertEquals(sha256, textMode.canonicalHex)
        assertEquals("release 文件.apk", textMode.sourceFileName)
        assertFalse(textMode.hasSourceFileNameMismatch("release 文件.apk"))
        assertTrue(textMode.hasSourceFileNameMismatch("another.apk"))
        assertEquals(DigestAlgorithm.MD5, binaryMode.algorithm)
        assertEquals("archive.bin", binaryMode.sourceFileName)
        assertNull(valid(sha256).sourceFileName)
    }

    @Test
    fun acceptsPaddedAndUnpaddedBase64ForEveryAlgorithm() {
        DigestAlgorithm.entries.forEach { algorithm ->
            val expectedHex = "ab".repeat(algorithm.byteCount)
            val encoded = Base64.getEncoder().encodeToString(HexCodec.decode(expectedHex))

            listOf(
                encoded,
                encoded.trimEnd('='),
                "${algorithm.id}:$encoded",
                "${algorithm.id}=$encoded",
            ).forEach { input ->
                val result = valid(input)
                assertEquals(algorithm, result.algorithm)
                assertEquals(expectedHex, result.canonicalHex)
            }
        }

        val base64StartingWithCrcPrefix = "0x${"A".repeat(41)}="
        assertEquals(
            DigestAlgorithm.SHA256,
            valid(base64StartingWithCrcPrefix).algorithm,
        )

        val hexOnlyUnpaddedBase64 = "A".repeat(43)
        assertEquals(
            "00".repeat(DigestAlgorithm.SHA256.byteCount),
            valid(hexOnlyUnpaddedBase64).canonicalHex,
        )

        val ambiguousHexAndBase64 = "A".repeat(64)
        assertEquals(DigestAlgorithm.SHA256, valid(ambiguousHexAndBase64).algorithm)
        assertEquals(
            "00".repeat(DigestAlgorithm.SHA384.byteCount),
            valid("sha384:$ambiguousHexAndBase64").canonicalHex,
        )
    }

    @Test
    fun acceptsStandardSriSha2Forms() {
        listOf(
            DigestAlgorithm.SHA256,
            DigestAlgorithm.SHA384,
            DigestAlgorithm.SHA512,
        ).forEach { algorithm ->
            val expectedHex = "5a".repeat(algorithm.byteCount)
            val encoded = Base64.getEncoder().encodeToString(HexCodec.decode(expectedHex))

            val result = valid("${algorithm.id}-$encoded")

            assertEquals(algorithm, result.algorithm)
            assertEquals(expectedHex, result.canonicalHex)
        }
    }

    @Test
    fun acceptsKnownCoreutilsAndOpenSslInteroperabilityVectors() {
        val expectedHex = "15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225"

        val coreutils = valid("$expectedHex  payload.bin")
        val opensslSri = valid("sha256-FeKw08M4keuw8e9gnsQZQgwg4yDOlMZfvIwzEkSOsiU=")

        assertEquals(DigestAlgorithm.SHA256, coreutils.algorithm)
        assertEquals("payload.bin", coreutils.sourceFileName)
        assertEquals(expectedHex, coreutils.canonicalHex)
        assertEquals(DigestAlgorithm.SHA256, opensslSri.algorithm)
        assertEquals(expectedHex, opensslSri.canonicalHex)
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
        val sha256Base64 = Base64.getEncoder().encodeToString(
            ByteArray(DigestAlgorithm.SHA256.byteCount),
        )
        assertInvalid(
            raw = "sha256-$sha256Base64",
            expected = DigestInputError.ALGORITHM_CONFLICT,
            hint = DigestAlgorithm.SHA384,
        )
    }

    @Test
    fun rejectsMalformedOrAmbiguousInputs() {
        val sha256 = "00".repeat(DigestAlgorithm.SHA256.byteCount)
        val fullWidthDigits = (0xFF11..0xFF18).map(Int::toChar).joinToString(separator = "")
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
            fullWidthDigits to DigestInputError.INVALID_CHARACTER,
            "00\u200B00" to DigestInputError.INVALID_CHARACTER,
            "sha256-AB==" to DigestInputError.INVALID_CHARACTER,
            "sha256-AA==" to DigestInputError.LENGTH_MISMATCH,
            "AA==" to DigestInputError.UNKNOWN_LENGTH,
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
