package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.io.IOException

sealed class InspectionReadException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)

class InvalidDeclaredSizeException(
    val declared: Long,
) : InspectionReadException("Declared size must not be negative: $declared")

class DeclaredSizeLimitExceededException(
    val declared: Long,
    val limit: Long,
) : InspectionReadException("Declared size $declared exceeds the input limit $limit")

enum class SizeMismatchDirection {
    TOO_SHORT,
    TOO_LONG,
}

class DeclaredSizeMismatchException(
    val declared: Long,
    val observed: Long,
    val direction: SizeMismatchDirection,
) : InspectionReadException(
    when (direction) {
        SizeMismatchDirection.TOO_SHORT ->
            "Input ended at $observed bytes before its declared size $declared"
        SizeMismatchDirection.TOO_LONG ->
            "Input exceeded its declared size $declared; observed at least $observed bytes"
    },
)

class StreamLimitExceededException(
    val limit: Long,
    val observedAtLeast: Long,
) : InspectionReadException("Input exceeds the $limit-byte limit")
