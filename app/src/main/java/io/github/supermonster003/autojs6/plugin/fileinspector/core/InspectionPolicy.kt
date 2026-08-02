package io.github.supermonster003.autojs6.plugin.fileinspector.core

data class InspectionPolicy(
    val maxBytes: Long = DEFAULT_MAX_BYTES,
    val bufferBytes: Int = DEFAULT_BUFFER_BYTES,
    val progressEveryBytes: Long = DEFAULT_PROGRESS_EVERY_BYTES,
) {

    init {
        require(maxBytes >= 0L && maxBytes < Long.MAX_VALUE) {
            "maxBytes must be in 0 until Long.MAX_VALUE"
        }
        require(bufferBytes in 1..MAX_BUFFER_BYTES) {
            "bufferBytes must be in 1..$MAX_BUFFER_BYTES"
        }
        require(progressEveryBytes > 0L) {
            "progressEveryBytes must be positive"
        }
    }

    companion object {
        const val HEADER_BYTES = 64
        const val DEFAULT_BUFFER_BYTES = 256 * 1024
        const val MAX_BUFFER_BYTES = 1024 * 1024
        const val DEFAULT_PROGRESS_EVERY_BYTES = 1024L * 1024L
        const val DEFAULT_MAX_BYTES = 4L * 1024L * 1024L * 1024L
    }
}
