package io.github.supermonster003.autojs6.plugin.fileinspector.core

import kotlin.math.max

internal enum class InspectionReportFormat {
    MARKDOWN,
    JSON,
}

internal data class InspectionReportExportModel(
    val title: String,
    val fileInformationTitle: String,
    val checksumsTitle: String,
    val fileHeaderTitle: String,
    val legacyLabel: String,
    val file: InspectionExportFile,
    val analysis: InspectionExportAnalysis,
    val checksums: List<InspectionExportChecksum>,
    val header: InspectionExportHeader,
)

internal data class InspectionExportFile(
    val name: String,
    val mimeType: String,
    val extension: String?,
    val declaredSizeBytes: Long,
    val actualSizeBytes: Long,
    val mimeTypeDisplay: String,
    val extensionDisplay: String,
    val declaredSizeDisplay: String,
    val actualSizeDisplay: String,
) {
    val displayLines: List<String>
        get() = listOf(mimeTypeDisplay, extensionDisplay, declaredSizeDisplay, actualSizeDisplay)
}

internal data class InspectionExportAnalysis(
    val signatureIds: List<String>,
    val detectedFormatDisplay: String,
    val containerKind: String?,
    val containerHintDisplay: String?,
    val contentKind: String,
    val sampleSizeBytes: Int,
    val printableRatio: Double,
    val entropyBitsPerByte: Double,
    val contentAnalysisDisplay: String,
    val bomKind: String?,
    val textEncodingDisplay: String,
) {
    val displayLines: List<String>
        get() = buildList {
            add(detectedFormatDisplay)
            containerHintDisplay?.let(::add)
            add(contentAnalysisDisplay)
            add(textEncodingDisplay)
        }
}

internal data class InspectionExportChecksum(
    val algorithm: String,
    val label: String,
    val value: String,
    val legacy: Boolean,
)

internal data class InspectionExportHeader(
    val byteCount: Int,
    val hexAscii: String,
    val display: String,
)

/** Produces self-contained report text without touching storage or Android framework APIs. */
internal object InspectionReportExporter {

    fun export(
        model: InspectionReportExportModel,
        format: InspectionReportFormat,
    ): String = when (format) {
        InspectionReportFormat.MARKDOWN -> toMarkdown(model)
        InspectionReportFormat.JSON -> toJson(model)
    }

    private fun toMarkdown(model: InspectionReportExportModel): String = buildString {
        append("# ")
        appendLine(model.title.escapeMarkdown())
        appendLine()
        appendLine(fencedText(model.file.name))
        appendLine()

        append("## ")
        appendLine(model.fileInformationTitle.escapeMarkdown())
        appendLine()
        (model.file.displayLines + model.analysis.displayLines).forEach { line ->
            append("- ")
            appendLine(line.escapeMarkdown())
        }
        appendLine()

        append("## ")
        appendLine(model.checksumsTitle.escapeMarkdown())
        appendLine()
        model.checksums.forEach { checksum ->
            append("- **")
            append(checksum.label.escapeMarkdown())
            if (checksum.legacy) {
                append(" (")
                append(model.legacyLabel.escapeMarkdown())
                append(')')
            }
            append(":** `")
            append(checksum.value)
            appendLine('`')
        }
        appendLine()

        append("## ")
        appendLine(model.fileHeaderTitle.escapeMarkdown())
        appendLine()
        appendLine(fencedText(model.header.display))
    }

    private fun toJson(model: InspectionReportExportModel): String {
        require(model.analysis.printableRatio.isFinite()) { "Printable ratio must be finite" }
        require(model.analysis.entropyBitsPerByte.isFinite()) { "Entropy must be finite" }

        val root = JsonObject(
            listOf(
                "schemaVersion" to JsonNumber("1"),
                "file" to JsonObject(
                    listOf(
                        "name" to JsonString(model.file.name),
                        "mimeType" to JsonObject(
                            listOf(
                                "value" to JsonString(model.file.mimeType),
                                "display" to JsonString(model.file.mimeTypeDisplay),
                            ),
                        ),
                        "extension" to JsonObject(
                            listOf(
                                "value" to model.file.extension.toJsonValue(),
                                "display" to JsonString(model.file.extensionDisplay),
                            ),
                        ),
                        "declaredSize" to JsonObject(
                            listOf(
                                "bytes" to JsonNumber(model.file.declaredSizeBytes.toString()),
                                "display" to JsonString(model.file.declaredSizeDisplay),
                            ),
                        ),
                        "actualSize" to JsonObject(
                            listOf(
                                "bytes" to JsonNumber(model.file.actualSizeBytes.toString()),
                                "display" to JsonString(model.file.actualSizeDisplay),
                            ),
                        ),
                    ),
                ),
                "analysis" to JsonObject(
                    listOf(
                        "detectedFormat" to JsonObject(
                            listOf(
                                "signatureIds" to JsonArray(
                                    model.analysis.signatureIds.map(::JsonString),
                                ),
                                "display" to JsonString(model.analysis.detectedFormatDisplay),
                            ),
                        ),
                        "container" to model.analysis.containerKind?.let { kind ->
                            JsonObject(
                                listOf(
                                    "kind" to JsonString(kind),
                                    "display" to JsonString(requireNotNull(model.analysis.containerHintDisplay)),
                                ),
                            )
                        }.toJsonValue(),
                        "content" to JsonObject(
                            listOf(
                                "kind" to JsonString(model.analysis.contentKind),
                                "sampleSizeBytes" to JsonNumber(model.analysis.sampleSizeBytes.toString()),
                                "printableRatio" to JsonNumber(model.analysis.printableRatio.toString()),
                                "entropyBitsPerByte" to JsonNumber(
                                    model.analysis.entropyBitsPerByte.toString(),
                                ),
                                "display" to JsonString(model.analysis.contentAnalysisDisplay),
                            ),
                        ),
                        "byteOrderMark" to JsonObject(
                            listOf(
                                "kind" to model.analysis.bomKind.toJsonValue(),
                                "display" to JsonString(model.analysis.textEncodingDisplay),
                            ),
                        ),
                    ),
                ),
                "checksums" to JsonArray(
                    model.checksums.map { checksum ->
                        JsonObject(
                            listOf(
                                "algorithm" to JsonString(checksum.algorithm),
                                "label" to JsonString(checksum.label),
                                "value" to JsonString(checksum.value),
                                "legacy" to JsonBoolean(checksum.legacy),
                            ),
                        )
                    },
                ),
                "header" to JsonObject(
                    listOf(
                        "byteCount" to JsonNumber(model.header.byteCount.toString()),
                        "hexAscii" to JsonString(model.header.hexAscii),
                        "display" to JsonString(model.header.display),
                    ),
                ),
                "display" to JsonObject(
                    listOf(
                        "title" to JsonString(model.title),
                        "fileInformationTitle" to JsonString(model.fileInformationTitle),
                        "checksumsTitle" to JsonString(model.checksumsTitle),
                        "fileHeaderTitle" to JsonString(model.fileHeaderTitle),
                        "legacyLabel" to JsonString(model.legacyLabel),
                    ),
                ),
            ),
        )
        return root.render() + "\n"
    }

    private fun String.escapeMarkdown(): String = buildString(length) {
        this@escapeMarkdown.forEach { character ->
            if (character in MARKDOWN_SPECIAL_CHARACTERS) append('\\')
            append(character)
        }
    }

    private fun fencedText(value: String): String {
        val longestRun = BACKTICK_RUN.findAll(value).maxOfOrNull { match -> match.value.length } ?: 0
        val fence = "`".repeat(max(MINIMUM_FENCE_LENGTH, longestRun + 1))
        return "$fence" + "text\n$value\n$fence"
    }

    private sealed interface JsonValue {
        fun appendTo(destination: StringBuilder, indent: Int)
    }

    private data class JsonObject(
        val properties: List<Pair<String, JsonValue>>,
    ) : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            with(destination) {
                append('{')
                if (properties.isNotEmpty()) {
                    appendLine()
                    properties.forEachIndexed { index, (name, value) ->
                        append(" ".repeat(indent + JSON_INDENT))
                        appendJsonString(name)
                        append(": ")
                        value.appendTo(this, indent + JSON_INDENT)
                        if (index != properties.lastIndex) append(',')
                        appendLine()
                    }
                    append(" ".repeat(indent))
                }
                append('}')
            }
        }
    }

    private data class JsonArray(
        val values: List<JsonValue>,
    ) : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            with(destination) {
                append('[')
                if (values.isNotEmpty()) {
                    appendLine()
                    values.forEachIndexed { index, value ->
                        append(" ".repeat(indent + JSON_INDENT))
                        value.appendTo(this, indent + JSON_INDENT)
                        if (index != values.lastIndex) append(',')
                        appendLine()
                    }
                    append(" ".repeat(indent))
                }
                append(']')
            }
        }
    }

    private data class JsonString(val value: String) : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            destination.appendJsonString(value)
        }
    }

    private data class JsonNumber(val value: String) : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            destination.append(value)
        }
    }

    private data class JsonBoolean(val value: Boolean) : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            destination.append(value)
        }
    }

    private data object JsonNull : JsonValue {
        override fun appendTo(destination: StringBuilder, indent: Int) {
            destination.append("null")
        }
    }

    private fun JsonValue.render(): String = buildString { appendTo(this, indent = 0) }

    private fun StringBuilder.appendJsonString(value: String) {
        append('"')
        value.forEach { character ->
            when (character) {
                '"' -> append("\\\"")
                '\\' -> append("\\\\")
                '\b' -> append("\\b")
                '\u000C' -> append("\\f")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> if (character.code < JSON_CONTROL_CHARACTER_LIMIT) {
                    append("\\u")
                    append(character.code.toString(16).padStart(JSON_CODE_UNIT_WIDTH, '0'))
                } else {
                    append(character)
                }
            }
        }
        append('"')
    }

    private fun String?.toJsonValue(): JsonValue = this?.let(::JsonString) ?: JsonNull

    private fun JsonValue?.toJsonValue(): JsonValue = this ?: JsonNull

    private const val MINIMUM_FENCE_LENGTH = 3
    private const val JSON_INDENT = 2
    private const val JSON_CONTROL_CHARACTER_LIMIT = 0x20
    private const val JSON_CODE_UNIT_WIDTH = 4
    private val BACKTICK_RUN = Regex("`+")
    private val MARKDOWN_SPECIAL_CHARACTERS = setOf('\\', '`', '*', '_', '[', ']', '<', '>')
}
