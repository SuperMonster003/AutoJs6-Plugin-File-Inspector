package io.github.supermonster003.autojs6.plugin.fileinspector.core

enum class VerificationStatus {
    MATCH,
    MISMATCH,
}

object DigestVerifier {

    fun verify(
        report: InspectionReport,
        expected: ExpectedDigest,
    ): VerificationStatus {
        val actualBytes = report[expected.algorithm].copyBytesForComparison()
        val expectedBytes = expected.copyBytesForComparison()
        return if (constantTimeEqualsSameLength(actualBytes, expectedBytes)) {
            VerificationStatus.MATCH
        } else {
            VerificationStatus.MISMATCH
        }
    }

    internal fun constantTimeEqualsSameLength(
        actual: ByteArray,
        expected: ByteArray,
    ): Boolean {
        if (actual.size != expected.size) return false
        var difference = 0
        actual.indices.forEach { index ->
            difference = difference or (actual[index].toInt() xor expected[index].toInt())
        }
        return difference == 0
    }
}
