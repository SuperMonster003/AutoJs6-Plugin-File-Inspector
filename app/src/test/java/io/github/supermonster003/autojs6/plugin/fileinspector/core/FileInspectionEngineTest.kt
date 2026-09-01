package io.github.supermonster003.autojs6.plugin.fileinspector.core

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException
import java.io.InputStream

class FileInspectionEngineTest {

    @Test
    fun computesKnownEmptyVector() {
        val report = inspect(TestSource(byteArrayOf(), declaredSize = 0L))

        assertEquals(0L, report.bytesRead)
        assertEquals("00000000", report[DigestAlgorithm.CRC32].hex)
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", report[DigestAlgorithm.MD5].hex)
        assertEquals("da39a3ee5e6b4b0d3255bfef95601890afd80709", report[DigestAlgorithm.SHA1].hex)
        assertEquals(
            "d14a028c2a3a2bc9476102bb288234c415a2b01f828ea62ac5b3e42f",
            report[DigestAlgorithm.SHA224].hex,
        )
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", report[DigestAlgorithm.SHA256].hex)
        assertEquals(
            "38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da" +
                    "274edebfe76f65fbd51ad2f14898b95b",
            report[DigestAlgorithm.SHA384].hex,
        )
        assertEquals(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
                    "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            report[DigestAlgorithm.SHA512].hex,
        )
    }

    @Test
    fun computesAllKnownDigestsInOneShortReadPass() {
        val source = TestSource(
            data = "123456789".encodeToByteArray(),
            declaredSize = 9L,
            streamFactory = { data -> TrackingInputStream(data, maxChunk = 2) },
        )

        val report = inspect(source, InspectionPolicy(bufferBytes = 7))

        assertEquals(1, source.openCount)
        assertEquals(9, source.lastStream?.position)
        assertEquals("cbf43926", report[DigestAlgorithm.CRC32].hex)
        assertEquals("25f9e794323b453885f5181f1b624d0b", report[DigestAlgorithm.MD5].hex)
        assertEquals("f7c3bc1d808e04732adf679965ccc34ca7ae3441", report[DigestAlgorithm.SHA1].hex)
        assertEquals(
            "9b3e61bf29f17c75572fae2e86e17809a4513d07c8a18152acf34521",
            report[DigestAlgorithm.SHA224].hex,
        )
        assertEquals("15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225", report[DigestAlgorithm.SHA256].hex)
        assertEquals(
            "eb455d56d2c1a69de64e832011f3393d45f3fa31d6842f21af92d2fe469c499" +
                    "da5e3179847334a18479c8d1dedea1be3",
            report[DigestAlgorithm.SHA384].hex,
        )
        assertEquals(
            "d9e6762dd1c8eaf6d61b3c6192fc408d4d6d5f1176d0c29169bc24e71c3f274" +
                    "ad27fcd5811b313d681f7e55ec02d73d499c95455b6b5bb503acf574fba8ffe85",
            report[DigestAlgorithm.SHA512].hex,
        )
        assertTrue(source.lastStream?.closed == true)
    }

    @Test
    fun digestIsIndependentOfBufferBoundariesAndZeroLengthBulkRead() {
        val data = ByteArray(257) { index -> (index * 37).toByte() }
        val normal = inspect(TestSource(data, data.size.toLong()), InspectionPolicy(bufferBytes = 32))
        val short = inspect(
            TestSource(
                data,
                data.size.toLong(),
                streamFactory = { bytes ->
                    TrackingInputStream(bytes, maxChunk = 3, returnZeroForFirstBulkRead = true)
                },
            ),
            InspectionPolicy(bufferBytes = 31),
        )

        DigestAlgorithm.entries.forEach { algorithm ->
            assertEquals(normal[algorithm].hex, short[algorithm].hex)
        }
    }

    @Test
    fun declaredSizeAboveLimitFailsBeforeOpeningStream() {
        val source = TestSource(ByteArray(2), declaredSize = 11L)

        val error = assertThrows(DeclaredSizeLimitExceededException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 10L, bufferBytes = 4))
        }

        assertEquals(11L, error.declared)
        assertEquals(10L, error.limit)
        assertEquals(0, source.openCount)
        assertNull(source.lastStream)
    }

    @Test
    fun negativeDeclaredSizeFailsBeforeOpeningStream() {
        val source = TestSource(ByteArray(0), declaredSize = -2L)

        val error = assertThrows(InvalidDeclaredSizeException::class.java) {
            inspect(source)
        }

        assertEquals(-2L, error.declared)
        assertEquals(0, source.openCount)
    }

    @Test
    fun shorterStreamFailsAtEofAndCloses() {
        val source = TestSource(ByteArray(5), declaredSize = 8L)

        val error = assertThrows(DeclaredSizeMismatchException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 20L, bufferBytes = 4, progressEveryBytes = 1L))
        }

        assertEquals(SizeMismatchDirection.TOO_SHORT, error.direction)
        assertEquals(8L, error.declared)
        assertEquals(5L, error.observed)
        assertTrue(source.lastStream?.closed == true)
    }

    @Test
    fun longerStreamReadsOnlyOneBytePastDeclarationAndCloses() {
        val source = TestSource(ByteArray(100), declaredSize = 5L)

        val error = assertThrows(DeclaredSizeMismatchException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 50L, bufferBytes = 32))
        }

        assertEquals(SizeMismatchDirection.TOO_LONG, error.direction)
        assertEquals(6L, error.observed)
        assertEquals(6, source.lastStream?.position)
        assertTrue(source.lastStream?.closed == true)
    }

    @Test
    fun unknownSizeStreamReadsOnlyOneBytePastHardLimit() {
        val source = TestSource(ByteArray(100), declaredSize = null)

        val error = assertThrows(StreamLimitExceededException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 5L, bufferBytes = 32))
        }

        assertEquals(5L, error.limit)
        assertEquals(6L, error.observedAtLeast)
        assertEquals(6, source.lastStream?.position)
        assertTrue(source.lastStream?.closed == true)
    }

    @Test
    fun hardLimitWinsWhenDeclarationAndLimitAreExceededTogether() {
        val source = TestSource(ByteArray(7), declaredSize = 5L)

        assertThrows(StreamLimitExceededException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 5L, bufferBytes = 16))
        }

        assertEquals(6, source.lastStream?.position)
    }

    @Test
    fun exactHardLimitAndZeroByteLimitAreAccepted() {
        val exact = inspect(
            TestSource(ByteArray(5) { it.toByte() }, declaredSize = null),
            InspectionPolicy(maxBytes = 5L, bufferBytes = 16),
        )
        val empty = inspect(
            TestSource(byteArrayOf(), declaredSize = null),
            InspectionPolicy(maxBytes = 0L, bufferBytes = 1),
        )

        assertEquals(5L, exact.bytesRead)
        assertEquals(0L, empty.bytesRead)
    }

    @Test
    fun zeroByteLimitRejectsFirstByte() {
        val source = TestSource(byteArrayOf(1), declaredSize = null)

        val error = assertThrows(StreamLimitExceededException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 0L, bufferBytes = 8))
        }

        assertEquals(1L, error.observedAtLeast)
        assertEquals(1, source.lastStream?.position)
    }

    @Test
    fun emitsMonotonicThrottledProgressAndOneCompletion() {
        val events = mutableListOf<InspectionProgress>()

        val report = runBlocking {
            FileInspectionEngine().inspect(
                source = TestSource(ByteArray(10), declaredSize = 10L),
                policy = InspectionPolicy(maxBytes = 10L, bufferBytes = 2, progressEveryBytes = 4L),
                onProgress = events::add,
            )
        }

        assertEquals(10L, report.bytesRead)
        assertEquals(listOf(0L, 4L, 8L, 10L), events.map(InspectionProgress::bytesRead))
        assertEquals(listOf(false, false, false, true), events.map(InspectionProgress::isComplete))
        assertTrue(events.zipWithNext().all { (first, second) -> first.bytesRead <= second.bytesRead })
        assertTrue(events.all { it.declaredSize == 10L })
    }

    @Test
    fun failureDoesNotEmitCompletion() {
        val events = mutableListOf<InspectionProgress>()

        assertThrows(DeclaredSizeMismatchException::class.java) {
            runBlocking {
                FileInspectionEngine().inspect(
                    source = TestSource(ByteArray(3), declaredSize = 5L),
                    policy = InspectionPolicy(maxBytes = 5L, bufferBytes = 1, progressEveryBytes = 1L),
                    onProgress = events::add,
                )
            }
        }

        assertFalse(events.any(InspectionProgress::isComplete))
        assertEquals(listOf(0L, 1L, 2L, 3L), events.map(InspectionProgress::bytesRead))
    }

    @Test
    fun cancellationClosesStreamAndReturnsNoCompletion() = runBlocking {
        val source = TestSource(ByteArray(32), declaredSize = 32L)
        val events = mutableListOf<InspectionProgress>()
        val isolatedJob = Job()

        try {
            withContext(isolatedJob) {
                FileInspectionEngine().inspect(
                    source = source,
                    policy = InspectionPolicy(maxBytes = 32L, bufferBytes = 2, progressEveryBytes = 4L),
                ) { progress ->
                    events += progress
                    if (progress.bytesRead >= 4L) isolatedJob.cancel()
                }
            }
            fail("Expected cancellation")
        } catch (_: CancellationException) {
            // Expected.
        }

        assertTrue(source.lastStream?.closed == true)
        assertFalse(events.any(InspectionProgress::isComplete))
        assertTrue(source.lastStream?.position in 4..6)
    }

    @Test
    fun ioFailureClosesStream() {
        val source = TestSource(
            data = ByteArray(8),
            declaredSize = 8L,
            streamFactory = { data -> TrackingInputStream(data, failAtPosition = 3) },
        )

        assertThrows(IOException::class.java) {
            inspect(source, InspectionPolicy(maxBytes = 8L, bufferBytes = 8))
        }

        assertTrue(source.lastStream?.closed == true)
    }

    @Test
    fun capturesOnlyFirst64HeaderBytes() {
        val data = ByteArray(100) { index -> index.toByte() }

        val report = inspect(TestSource(data, data.size.toLong()))

        assertEquals(InspectionPolicy.HEADER_BYTES, report.header.bytes.size)
        assertTrue(report.header.bytes.contentEquals(data.copyOf(InspectionPolicy.HEADER_BYTES)))
    }

    @Test
    fun capturesTarOffsetAndAnalysisSampleAcrossSmallReadChunks() {
        val data = ByteArray(512).apply {
            "ustar\u0000".encodeToByteArray().copyInto(this, destinationOffset = 257)
        }
        val source = TestSource(
            data = data,
            declaredSize = data.size.toLong(),
            streamFactory = { bytes -> TrackingInputStream(bytes, maxChunk = 7) },
        )

        val report = inspect(source, InspectionPolicy(bufferBytes = 11))

        assertEquals(listOf(FileSignature.TAR), report.header.signatures)
        assertEquals(InspectionPolicy.HEADER_BYTES, report.header.bytes.size)
        assertEquals(data.size, report.header.content.sampleSize)
        assertEquals(1, source.openCount)
        assertEquals(data.size, source.lastStream?.position)
    }

    @Test
    fun capturesPeSignatureBeyondAnalysisPrefixWithoutSeekingOrSecondPass() {
        val peOffset = InspectionPolicy.ANALYSIS_SAMPLE_BYTES + 317
        val data = ByteArray(peOffset + 4).apply {
            this[0] = 'M'.code.toByte()
            this[1] = 'Z'.code.toByte()
            this[0x3C] = peOffset.toByte()
            this[0x3D] = (peOffset ushr 8).toByte()
            this[0x3E] = (peOffset ushr 16).toByte()
            this[0x3F] = (peOffset ushr 24).toByte()
            byteArrayOf('P'.code.toByte(), 'E'.code.toByte(), 0, 0)
                .copyInto(this, destinationOffset = peOffset)
        }
        val source = TestSource(
            data = data,
            declaredSize = data.size.toLong(),
            streamFactory = { bytes -> TrackingInputStream(bytes, maxChunk = 13) },
        )

        val report = inspect(source, InspectionPolicy(bufferBytes = 29))

        assertEquals(listOf(FileSignature.PE), report.header.signatures)
        assertEquals(InspectionPolicy.ANALYSIS_SAMPLE_BYTES, report.header.content.sampleSize)
        assertEquals(1, source.openCount)
        assertEquals(data.size, source.lastStream?.position)
    }

    private fun inspect(
        source: InspectorSource,
        policy: InspectionPolicy = InspectionPolicy(),
    ): InspectionReport = runBlocking {
        FileInspectionEngine().inspect(source, policy)
    }

    private class TestSource(
        private val data: ByteArray,
        override val declaredSize: Long?,
        private val streamFactory: (ByteArray) -> TrackingInputStream = { TrackingInputStream(it) },
    ) : InspectorSource {

        var openCount = 0
            private set
        var lastStream: TrackingInputStream? = null
            private set

        override fun openStream(): InputStream {
            openCount += 1
            return streamFactory(data).also { lastStream = it }
        }
    }

    private class TrackingInputStream(
        private val data: ByteArray,
        private val maxChunk: Int = Int.MAX_VALUE,
        private var returnZeroForFirstBulkRead: Boolean = false,
        private val failAtPosition: Int? = null,
    ) : InputStream() {

        var position: Int = 0
            private set
        var closed: Boolean = false
            private set

        override fun read(): Int {
            throwIfConfigured()
            if (position >= data.size) return -1
            return data[position++].toInt() and 0xFF
        }

        override fun read(target: ByteArray, offset: Int, length: Int): Int {
            if (returnZeroForFirstBulkRead) {
                returnZeroForFirstBulkRead = false
                return 0
            }
            throwIfConfigured()
            if (position >= data.size) return -1
            val beforeFailure = failAtPosition?.minus(position) ?: Int.MAX_VALUE
            val count = minOf(length, maxChunk, data.size - position, beforeFailure)
            if (count <= 0) throwIfConfigured()
            data.copyInto(target, destinationOffset = offset, startIndex = position, endIndex = position + count)
            position += count
            return count
        }

        override fun close() {
            closed = true
        }

        private fun throwIfConfigured() {
            if (failAtPosition != null && position >= failAtPosition) {
                throw IOException("Synthetic read failure")
            }
        }
    }
}
