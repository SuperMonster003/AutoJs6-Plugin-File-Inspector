package io.github.supermonster003.autojs6.plugin.fileinspector.core

internal class InspectionSample private constructor(
    prefix: ByteArray,
    peSignature: ByteArray?,
) {

    val prefix: ByteArray = prefix.copyOf()
    val peSignature: ByteArray? = peSignature?.copyOf()

    companion object {
        fun from(bytes: ByteArray): InspectionSample = InspectionSampleCollector().run {
            accept(fileOffset = 0L, source = bytes, count = bytes.size)
            snapshot()
        }

        fun create(prefix: ByteArray, peSignature: ByteArray?): InspectionSample =
            InspectionSample(prefix, peSignature)
    }
}

/**
 * Captures the bounded byte windows needed for inspection while the source is
 * already moving past in one direction. No stream seek or second read is used.
 */
internal class InspectionSampleCollector {

    private val prefix = ByteArray(InspectionPolicy.ANALYSIS_SAMPLE_BYTES)
    private var prefixSize = 0
    private var peHeaderOffset: Long? = null
    private var peHeaderOffsetResolved = false
    private val peSignature = ByteArray(PE_SIGNATURE_BYTES)
    private var peSignatureSize = 0

    fun accept(
        fileOffset: Long,
        source: ByteArray,
        sourceOffset: Int = 0,
        count: Int,
    ) {
        require(fileOffset >= 0L) { "fileOffset must not be negative" }
        require(sourceOffset >= 0 && count >= 0 && sourceOffset <= source.size - count) {
            "sourceOffset and count must describe a valid source range"
        }
        if (isComplete()) return

        prefixSize = maxOf(
            prefixSize,
            copyIntersection(
                windowOffset = 0L,
                target = prefix,
                fileOffset = fileOffset,
                source = source,
                sourceOffset = sourceOffset,
                count = count,
            ),
        )

        discoverPeHeaderOffset()
        peHeaderOffset?.let { offset ->
            peSignatureSize = maxOf(
                peSignatureSize,
                copyIntersection(
                    windowOffset = offset,
                    target = peSignature,
                    fileOffset = fileOffset,
                    source = source,
                    sourceOffset = sourceOffset,
                    count = count,
                ),
            )
        }
    }

    fun snapshot(): InspectionSample = InspectionSample.create(
        prefix = prefix.copyOf(prefixSize),
        peSignature = peSignature.takeIf { peSignatureSize == it.size },
    )

    private fun discoverPeHeaderOffset() {
        if (peHeaderOffsetResolved || prefixSize < DOS_HEADER_BYTES) return
        peHeaderOffsetResolved = true
        if (prefix[0] != 'M'.code.toByte() || prefix[1] != 'Z'.code.toByte()) return

        val candidate = prefix.readUInt32LittleEndian(PE_OFFSET_FIELD)
        if (candidate >= DOS_HEADER_BYTES) peHeaderOffset = candidate
    }

    private fun isComplete(): Boolean {
        val peWindowComplete = peHeaderOffsetResolved &&
                (peHeaderOffset == null || peSignatureSize == peSignature.size)
        return prefixSize == prefix.size && peWindowComplete
    }

    private fun copyIntersection(
        windowOffset: Long,
        target: ByteArray,
        fileOffset: Long,
        source: ByteArray,
        sourceOffset: Int,
        count: Int,
    ): Int {
        val sourceEnd = fileOffset + count.toLong()
        val windowEnd = windowOffset + target.size.toLong()
        val intersectionStart = maxOf(fileOffset, windowOffset)
        val intersectionEnd = minOf(sourceEnd, windowEnd)
        if (intersectionStart >= intersectionEnd) return 0

        val destinationStart = (intersectionStart - windowOffset).toInt()
        val copyStart = sourceOffset + (intersectionStart - fileOffset).toInt()
        val copyCount = (intersectionEnd - intersectionStart).toInt()
        source.copyInto(
            destination = target,
            destinationOffset = destinationStart,
            startIndex = copyStart,
            endIndex = copyStart + copyCount,
        )
        return destinationStart + copyCount
    }

    private fun ByteArray.readUInt32LittleEndian(offset: Int): Long {
        return (this[offset].toLong() and 0xFFL) or
                ((this[offset + 1].toLong() and 0xFFL) shl 8) or
                ((this[offset + 2].toLong() and 0xFFL) shl 16) or
                ((this[offset + 3].toLong() and 0xFFL) shl 24)
    }

    private companion object {
        const val DOS_HEADER_BYTES = 64L
        const val PE_OFFSET_FIELD = 0x3C
        const val PE_SIGNATURE_BYTES = 4
    }
}
