package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.Context
import android.text.format.Formatter
import io.github.supermonster003.autojs6.plugin.fileinspector.core.BomKind
import io.github.supermonster003.autojs6.plugin.fileinspector.core.ContentAnalysis
import io.github.supermonster003.autojs6.plugin.fileinspector.core.ContentKind
import io.github.supermonster003.autojs6.plugin.fileinspector.core.DigestAlgorithm
import io.github.supermonster003.autojs6.plugin.fileinspector.core.FileSignature
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionExportAnalysis
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionExportChecksum
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionExportFile
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionExportHeader
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReport
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReportExportModel
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReportExporter
import io.github.supermonster003.autojs6.plugin.fileinspector.core.InspectionReportFormat
import io.github.supermonster003.autojs6.plugin.fileinspector.core.ZipContainerInspector
import io.github.supermonster003.autojs6.plugin.fileinspector.core.ZipContainerKind
import java.util.Locale

internal fun DigestAlgorithm.label(context: Context): String = context.getString(
    when (this) {
        DigestAlgorithm.CRC32 -> R.string.algorithm_crc32
        DigestAlgorithm.MD5 -> R.string.algorithm_md5
        DigestAlgorithm.SHA1 -> R.string.algorithm_sha1
        DigestAlgorithm.SHA224 -> R.string.algorithm_sha224
        DigestAlgorithm.SHA256 -> R.string.algorithm_sha256
        DigestAlgorithm.SHA384 -> R.string.algorithm_sha384
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
                FileSignature.BZIP2 -> R.string.format_bzip2
                FileSignature.DEX -> R.string.format_dex
                FileSignature.EBML -> R.string.format_ebml
                FileSignature.ELF -> R.string.format_elf
                FileSignature.GIF87A -> R.string.format_gif87a
                FileSignature.GIF89A -> R.string.format_gif89a
                FileSignature.GZIP -> R.string.format_gzip
                FileSignature.ISO_BMFF -> R.string.format_iso_bmff
                FileSignature.JAVA_CLASS -> R.string.format_java_class
                FileSignature.JPEG -> R.string.format_jpeg
                FileSignature.LZ4 -> R.string.format_lz4
                FileSignature.MACH_O -> R.string.format_mach_o
                FileSignature.PDF -> R.string.format_pdf
                FileSignature.PE -> R.string.format_pe
                FileSignature.PNG -> R.string.format_png
                FileSignature.RAR4 -> R.string.format_rar4
                FileSignature.RAR5 -> R.string.format_rar5
                FileSignature.SEVEN_Z -> R.string.format_7z
                FileSignature.SQLITE3 -> R.string.format_sqlite3
                FileSignature.TAR -> R.string.format_tar
                FileSignature.WEBP -> R.string.format_webp
                FileSignature.WOFF -> R.string.format_woff
                FileSignature.WOFF2 -> R.string.format_woff2
                FileSignature.XZ -> R.string.format_xz
                FileSignature.ZIP -> R.string.format_zip
                FileSignature.ZSTD -> R.string.format_zstd
            },
        )
    }
}

internal fun ContentAnalysis.formatContentAnalysis(context: Context): String = context.getString(
    R.string.content_analysis,
    context.getString(
        when (kind) {
            ContentKind.EMPTY -> R.string.content_kind_empty
            ContentKind.HIGH_ENTROPY -> R.string.content_kind_high_entropy
            ContentKind.LIKELY_BINARY -> R.string.content_kind_likely_binary
            ContentKind.LIKELY_TEXT -> R.string.content_kind_likely_text
        },
    ),
    sampleSize,
    printableRatio * 100.0,
    entropyBitsPerByte,
)

internal fun ZipContainerKind.formatZipContainerHint(context: Context): String = context.getString(
    R.string.zip_container_hint,
    context.getString(
        when (this) {
            ZipContainerKind.ANDROID_PACKAGE -> R.string.zip_container_android
            ZipContainerKind.EPUB -> R.string.zip_container_epub
            ZipContainerKind.JAVA_ARCHIVE -> R.string.zip_container_java
            ZipContainerKind.OFFICE_OPEN_XML -> R.string.zip_container_office_open_xml
            ZipContainerKind.OPEN_DOCUMENT -> R.string.zip_container_open_document
        },
    ),
)

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
    format: InspectionReportFormat,
): String = InspectionReportExporter.export(buildInspectionReportModel(request, report), format)

internal fun Context.buildInspectionReportModel(
    request: FileInspectionRequest,
    report: InspectionReport,
): InspectionReportExportModel {
    val extension = request.displayName.fileExtensionOrNull()
    val declaredSizeDisplay = getString(
        R.string.declared_size,
        Formatter.formatFileSize(this, request.declaredSize),
    )
    val actualSizeDisplay = getString(
        R.string.file_size,
        Formatter.formatFileSize(this, report.bytesRead),
    )
    val signatures = report.header.signatures
    val zipContainerKind = ZipContainerInspector.inspect(request.displayName, signatures)
    val rawHeader = report.header.bytes.formatHeader()
    return InspectionReportExportModel(
        title = getString(R.string.app_name),
        fileInformationTitle = getString(R.string.file_information),
        checksumsTitle = getString(R.string.report_checksums),
        fileHeaderTitle = getString(R.string.file_header),
        legacyLabel = getString(R.string.digest_legacy_label),
        file = InspectionExportFile(
            name = request.displayName,
            mimeType = request.mimeType,
            extension = extension,
            declaredSizeBytes = request.declaredSize,
            actualSizeBytes = report.bytesRead,
            mimeTypeDisplay = getString(R.string.file_mime_type, request.mimeType),
            extensionDisplay = getString(
                R.string.file_extension,
                extension ?: getString(R.string.no_extension),
            ),
            declaredSizeDisplay = declaredSizeDisplay,
            actualSizeDisplay = actualSizeDisplay,
        ),
        analysis = InspectionExportAnalysis(
            signatureIds = signatures.map { signature -> signature.name.lowercase(Locale.ROOT) },
            detectedFormatDisplay = getString(
                R.string.detected_format,
                signatures.formatSignatures(this),
            ),
            containerKind = zipContainerKind?.name?.lowercase(Locale.ROOT),
            containerHintDisplay = zipContainerKind?.formatZipContainerHint(this),
            contentKind = report.header.content.kind.name.lowercase(Locale.ROOT),
            sampleSizeBytes = report.header.content.sampleSize,
            printableRatio = report.header.content.printableRatio,
            entropyBitsPerByte = report.header.content.entropyBitsPerByte,
            contentAnalysisDisplay = report.header.content.formatContentAnalysis(this),
            bomKind = report.header.bom?.name?.lowercase(Locale.ROOT),
            textEncodingDisplay = getString(
                R.string.text_encoding,
                report.header.bom.formatBom(this),
            ),
        ),
        checksums = DigestAlgorithm.entries.map { algorithm ->
            InspectionExportChecksum(
                algorithm = algorithm.id,
                label = algorithm.label(this),
                value = report[algorithm].hex,
                legacy = algorithm.isLegacy(),
            )
        },
        header = InspectionExportHeader(
            byteCount = report.header.bytes.size,
            hexAscii = rawHeader,
            display = rawHeader.ifEmpty { getString(R.string.header_empty) },
        ),
    )
}

internal fun String.fileExtensionOrNull(): String? {
    val delimiter = lastIndexOf('.')
    return if (delimiter <= 0 || delimiter == lastIndex) {
        null
    } else {
        substring(delimiter + 1).lowercase(Locale.ROOT)
    }
}

private const val HEADER_COLUMNS = 16
private val PRINTABLE_ASCII_RANGE = 0x20..0x7E
