package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class InspectionRateEstimatorTest {

    @Test
    fun estimatesThroughputAndRemainingTimeFromMonotonicSamples() {
        val estimator = InspectionRateEstimator()

        assertNull(estimator.update(bytesRead = 0L, totalBytes = 10_000L, elapsedRealtimeMillis = 1_000L))
        val rate = estimator.update(
            bytesRead = 2_000L,
            totalBytes = 10_000L,
            elapsedRealtimeMillis = 2_000L,
        )

        assertEquals(2_000L, rate?.bytesPerSecond)
        assertEquals(4_000L, rate?.remainingMillis)
    }

    @Test
    fun waitsForMinimumSampleDuration() {
        val estimator = InspectionRateEstimator(
            averagingWindowMillis = 1_000L,
            minimumSampleMillis = 100L,
        )

        assertNull(estimator.update(0L, 1_000L, 0L))
        assertNull(estimator.update(500L, 1_000L, 99L))
        val rate = estimator.update(600L, 1_000L, 100L)

        assertEquals(6_000L, rate?.bytesPerSecond)
        assertEquals(67L, rate?.remainingMillis)
    }

    @Test
    fun rollingWindowAdaptsToRecentSpeed() {
        val estimator = InspectionRateEstimator(
            averagingWindowMillis = 1_000L,
            minimumSampleMillis = 100L,
        )

        estimator.update(0L, 10_000L, 0L)
        assertEquals(1_000L, estimator.update(1_000L, 10_000L, 1_000L)?.bytesPerSecond)
        val recentRate = estimator.update(5_000L, 10_000L, 2_000L)

        assertEquals(4_000L, recentRate?.bytesPerSecond)
        assertEquals(1_250L, recentRate?.remainingMillis)
    }

    @Test
    fun unknownTotalHasNoRemainingTime() {
        val estimator = InspectionRateEstimator()

        estimator.update(0L, null, 0L)
        val rate = estimator.update(1_000L, null, 1_000L)

        assertEquals(1_000L, rate?.bytesPerSecond)
        assertNull(rate?.remainingMillis)
    }

    @Test
    fun completedSampleReportsZeroRemainingTime() {
        val estimator = InspectionRateEstimator()

        estimator.update(0L, 1_000L, 0L)
        val rate = estimator.update(1_000L, 1_000L, 1_000L)

        assertEquals(0L, rate?.remainingMillis)
    }

    @Test
    fun regressedInputStartsANewWindow() {
        val estimator = InspectionRateEstimator()

        estimator.update(0L, 10_000L, 0L)
        estimator.update(1_000L, 10_000L, 1_000L)
        assertNull(estimator.update(10L, 10_000L, 500L))
        val rate = estimator.update(510L, 10_000L, 1_000L)

        assertEquals(1_000L, rate?.bytesPerSecond)
    }

    @Test
    fun validatesConfigurationAndSamples() {
        assertThrows(IllegalArgumentException::class.java) {
            InspectionRateEstimator(averagingWindowMillis = 0L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            InspectionRateEstimator(averagingWindowMillis = 100L, minimumSampleMillis = 101L)
        }

        val estimator = InspectionRateEstimator()
        assertThrows(IllegalArgumentException::class.java) {
            estimator.update(bytesRead = -1L, totalBytes = 1L, elapsedRealtimeMillis = 0L)
        }
        assertThrows(IllegalArgumentException::class.java) {
            estimator.update(bytesRead = 0L, totalBytes = -1L, elapsedRealtimeMillis = 0L)
        }
    }
}
