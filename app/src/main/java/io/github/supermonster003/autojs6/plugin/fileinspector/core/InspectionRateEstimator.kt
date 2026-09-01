package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.util.ArrayDeque
import kotlin.math.ceil
import kotlin.math.roundToLong

internal data class InspectionRate(
    val bytesPerSecond: Long,
    val remainingMillis: Long?,
)

/** Rolling throughput estimator driven by a caller-provided monotonic clock. */
internal class InspectionRateEstimator(
    private val averagingWindowMillis: Long = DEFAULT_AVERAGING_WINDOW_MILLIS,
    private val minimumSampleMillis: Long = DEFAULT_MINIMUM_SAMPLE_MILLIS,
) {

    private data class Sample(
        val bytesRead: Long,
        val elapsedRealtimeMillis: Long,
    )

    private val samples = ArrayDeque<Sample>()

    init {
        require(averagingWindowMillis > 0L) { "Averaging window must be positive" }
        require(minimumSampleMillis > 0L) { "Minimum sample duration must be positive" }
        require(minimumSampleMillis <= averagingWindowMillis) {
            "Minimum sample duration cannot exceed the averaging window"
        }
    }

    fun update(
        bytesRead: Long,
        totalBytes: Long?,
        elapsedRealtimeMillis: Long,
    ): InspectionRate? {
        require(bytesRead >= 0L) { "Bytes read cannot be negative" }
        require(totalBytes == null || totalBytes >= 0L) { "Total bytes cannot be negative" }
        require(elapsedRealtimeMillis >= 0L) { "Elapsed realtime cannot be negative" }

        val previous = samples.peekLast()
        if (
            previous != null &&
            (bytesRead < previous.bytesRead || elapsedRealtimeMillis < previous.elapsedRealtimeMillis)
        ) {
            samples.clear()
        }

        val latest = samples.peekLast()
        if (
            latest == null ||
            latest.bytesRead != bytesRead ||
            latest.elapsedRealtimeMillis != elapsedRealtimeMillis
        ) {
            samples.addLast(Sample(bytesRead, elapsedRealtimeMillis))
        }
        trimWindow(elapsedRealtimeMillis)

        val baseline = samples.peekFirst() ?: return null
        val current = samples.peekLast() ?: return null
        val elapsedMillis = current.elapsedRealtimeMillis - baseline.elapsedRealtimeMillis
        val byteDelta = current.bytesRead - baseline.bytesRead
        if (elapsedMillis < minimumSampleMillis || byteDelta <= 0L) return null

        val bytesPerSecond = (
            byteDelta.toDouble() * MILLIS_PER_SECOND / elapsedMillis.toDouble()
        ).roundToLong().coerceAtLeast(1L)
        val remainingMillis = totalBytes?.let { total ->
            val remainingBytes = (total - current.bytesRead).coerceAtLeast(0L)
            if (remainingBytes == 0L) {
                0L
            } else {
                ceil(
                    remainingBytes.toDouble() * MILLIS_PER_SECOND / bytesPerSecond.toDouble(),
                ).toLong().coerceAtLeast(1L)
            }
        }
        return InspectionRate(bytesPerSecond, remainingMillis)
    }

    private fun trimWindow(nowMillis: Long) {
        while (
            samples.size > MINIMUM_WINDOW_SAMPLE_COUNT &&
            nowMillis - requireNotNull(samples.peekFirst()).elapsedRealtimeMillis > averagingWindowMillis
        ) {
            samples.removeFirst()
        }
    }

    private companion object {
        const val DEFAULT_AVERAGING_WINDOW_MILLIS = 5_000L
        const val DEFAULT_MINIMUM_SAMPLE_MILLIS = 100L
        const val MILLIS_PER_SECOND = 1_000.0
        const val MINIMUM_WINDOW_SAMPLE_COUNT = 2
    }
}
