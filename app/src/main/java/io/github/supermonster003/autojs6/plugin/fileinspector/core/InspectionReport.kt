package io.github.supermonster003.autojs6.plugin.fileinspector.core

data class InspectionProgress(
    val bytesRead: Long,
    val declaredSize: Long?,
    val isComplete: Boolean,
)

class InspectionReport internal constructor(
    val bytesRead: Long,
    digests: Map<DigestAlgorithm, DigestValue>,
    val header: HeaderSnapshot,
) {

    val digests: Map<DigestAlgorithm, DigestValue> = DigestAlgorithm.entries.associateWith { algorithm ->
        requireNotNull(digests[algorithm]) { "Missing ${algorithm.id} digest" }
    }

    operator fun get(algorithm: DigestAlgorithm): DigestValue = requireNotNull(digests[algorithm])
}
