package io.github.supermonster003.autojs6.plugin.fileinspector.core

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import java.security.MessageDigest
import java.util.EnumMap
import java.util.zip.CRC32

class FileInspectionEngine {

    suspend fun inspect(
        source: InspectorSource,
        policy: InspectionPolicy = InspectionPolicy(),
        onProgress: (InspectionProgress) -> Unit = {},
    ): InspectionReport {
        val declaredSize = source.declaredSize
        if (declaredSize != null && declaredSize < 0L) {
            throw InvalidDeclaredSizeException(declaredSize)
        }
        if (declaredSize != null && declaredSize > policy.maxBytes) {
            throw DeclaredSizeLimitExceededException(declaredSize, policy.maxBytes)
        }

        currentCoroutineContext().ensureActive()
        val crc32 = CRC32()
        val messageDigests = createMessageDigests()
        val buffer = ByteArray(policy.bufferBytes)
        val sampleCollector = InspectionSampleCollector()
        var bytesRead = 0L
        var lastProgressBytes = 0L

        onProgress(InspectionProgress(0L, declaredSize, isComplete = false))
        currentCoroutineContext().ensureActive()

        source.openStream().use { input ->
            while (true) {
                currentCoroutineContext().ensureActive()
                val hardRemaining = policy.maxBytes - bytesRead
                val declaredRemaining = declaredSize?.minus(bytesRead)
                val nearestBoundary = minOf(hardRemaining, declaredRemaining ?: Long.MAX_VALUE)
                val requestedBytes = minOf(
                    policy.bufferBytes.toLong(),
                    nearestBoundary + OVERFLOW_SENTINEL_BYTES,
                ).toInt()

                var count = input.read(buffer, 0, requestedBytes)
                if (count < 0) break
                if (count == 0) {
                    val singleByte = input.read()
                    if (singleByte < 0) break
                    buffer[0] = singleByte.toByte()
                    count = 1
                }

                currentCoroutineContext().ensureActive()
                val countLong = count.toLong()
                if (countLong > hardRemaining) {
                    throw StreamLimitExceededException(
                        limit = policy.maxBytes,
                        observedAtLeast = policy.maxBytes + OVERFLOW_SENTINEL_BYTES,
                    )
                }
                if (declaredRemaining != null && countLong > declaredRemaining) {
                    throw DeclaredSizeMismatchException(
                        declared = declaredSize,
                        observed = declaredSize + OVERFLOW_SENTINEL_BYTES,
                        direction = SizeMismatchDirection.TOO_LONG,
                    )
                }

                sampleCollector.accept(fileOffset = bytesRead, source = buffer, count = count)
                crc32.update(buffer, 0, count)
                messageDigests.values.forEach { digest -> digest.update(buffer, 0, count) }
                bytesRead += countLong

                if (bytesRead - lastProgressBytes >= policy.progressEveryBytes) {
                    onProgress(InspectionProgress(bytesRead, declaredSize, isComplete = false))
                    lastProgressBytes = bytesRead
                }
            }
        }

        if (declaredSize != null && bytesRead != declaredSize) {
            throw DeclaredSizeMismatchException(
                declared = declaredSize,
                observed = bytesRead,
                direction = SizeMismatchDirection.TOO_SHORT,
            )
        }
        currentCoroutineContext().ensureActive()

        val values = EnumMap<DigestAlgorithm, DigestValue>(DigestAlgorithm::class.java).apply {
            put(DigestAlgorithm.CRC32, DigestValue(DigestAlgorithm.CRC32, crc32.toBytes()))
            messageDigests.forEach { (algorithm, digest) ->
                put(algorithm, DigestValue(algorithm, digest.digest()))
            }
        }
        val report = InspectionReport(
            bytesRead = bytesRead,
            digests = values,
            header = HeaderInspector.inspect(sampleCollector.snapshot()),
        )
        onProgress(InspectionProgress(bytesRead, declaredSize, isComplete = true))
        return report
    }

    private fun createMessageDigests(): EnumMap<DigestAlgorithm, MessageDigest> {
        return EnumMap<DigestAlgorithm, MessageDigest>(DigestAlgorithm::class.java).apply {
            DigestAlgorithm.entries.forEach { algorithm ->
                algorithm.jcaName?.let { name -> put(algorithm, MessageDigest.getInstance(name)) }
            }
        }
    }

    private fun CRC32.toBytes(): ByteArray {
        val checksum = value
        return byteArrayOf(
            (checksum ushr 24).toByte(),
            (checksum ushr 16).toByte(),
            (checksum ushr 8).toByte(),
            checksum.toByte(),
        )
    }

    private companion object {
        const val OVERFLOW_SENTINEL_BYTES = 1L
    }
}
