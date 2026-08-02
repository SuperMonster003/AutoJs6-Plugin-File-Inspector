package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.Context
import android.text.format.Formatter
import io.github.supermonster003.autojs6.plugin.fileinspector.core.BomKind
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestAlgorithm
import io.github.supermonster003.autojs6.plugin.fileinspector.core.FileSignature
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import java.util.Locale

internal fun DigestAlgorithm.label(context: Context): String = context.getString(
    when (this) {
        DigestAlgorithm.CRC32 -> R.string.algorithm_crc32
        DigestAlgorithm.MD5 -> R.string.algorithm_md5
        DigestAlgorithm.SHA1 -> R.string.algorithm_sha1
        DigestAlgorithm.SHA256 -> R.string.algorithm_sha256
        DigestAlgorithm.SHA512 -> R.string.algorithm_sha512
    },
)

internal fun DigestAlgorithm.isLegacy(): Boolean =
    this == DigestAlgorithm.MD5 || this == DigestAlgorithm.SHA1

internal fun List<FileSignature>.formatSignatures(context: Context): String {
    if (isEmpty()) return context.getString(R.string.format_unknown)
    return joinToString(separator = ", ") { signature ->
        context.getString(
            when (signature) {
                FileSignature.DEX -> R.string.format_dex
                FileSignature.ELF -> R.string.format_elf
                FileSignature.GIF87A -> R.string.format_gif87a
                FileSignature.GIF89A -> R.string.format_gif89a
                FileSignature.GZIP -> R.string.format_gzip
                FileSignature.JPEG -> R.string.format_jpeg
                FileSignature.PDF -> R.string.format_pdf
                FileSignature.PNG -> R.string.format_png
                FileSignature.SQLITE3 -> R.string.format_sqlite3
                FileSignature.ZIP -> R.string.format_zip
            },
        )
    }
}

internal fun BomKind?.formatBom(context: Context): String = context.getString(
    when (this) {
        null -> R.string.bom_none
        BomKind.UTF8 -> R.string.bom_utf8
        BomKind.UTF16_BE -> R.string.bom_utf16_be
        BomKind.UTF16_LE -> R.string.bom_utf16_le
        BomKind.UTF32_BE -> R.string.bom_utf32_be
        BomKind.UTF32_LE -> R.string.bom_utf32_le
    },
)

internal fun ByteArray.formatHeader(): String = asList()
    .chunked(HEADER_COLUMNS)
    .mapIndexed { row, bytes ->
        val hex = bytes.joinToString(separator = " ") { byte ->
            String.format(Locale.ROOT, "%02x", byte.toInt() and 0xFF)
        }
        val paddedHex = hex.padEnd(HEADER_COLUMNS * 3 - 1)
        val ascii = bytes.joinToString(separator = "") { byte ->
            val value = byte.toInt() and 0xFF
            if (value in PRINTABLE_ASCII_RANGE) value.toChar().toString() else "."
        }
        String.format(Locale.ROOT, "%08x  %s  |%s|", row * HEADER_COLUMNS, paddedHex, ascii)
    }
    .joinToString(separator = "\n")

internal fun Context.buildInspectionReport(
    request: FileInspectionRequest,
    report: InspectionReport,
): String = buildString {
    appendLine(request.displayName)
    appendLine(getString(R.string.file_mime_type, request.mimeType))
    appendLine(
        getString(
            R.string.declared_size,
            Formatter.formatFileSize(this@buildInspectionReport, request.declaredSize),
        ),
    )
    appendLine(
        getString(
            R.string.file_size,
            Formatter.formatFileSize(this@buildInspectionReport, report.bytesRead),
        ),
    )
    appendLine(getString(R.string.detected_format, report.header.signatures.formatSignatures(this@buildInspectionReport)))
    appendLine(getString(R.string.text_encoding, report.header.bom.formatBom(this@buildInspectionReport)))
    appendLine()
    DigestAlgorithm.entries.forEach { algorithm ->
        append(algorithm.label(this@buildInspectionReport))
        append(": ")
        appendLine(report[algorithm].hex)
    }
    appendLine()
    appendLine(getString(R.string.file_header))
    append(report.header.bytes.formatHeader().ifEmpty { getString(R.string.header_empty) })
}

private const val HEADER_COLUMNS = 16
private val PRINTABLE_ASCII_RANGE = 0x20..0x7E
