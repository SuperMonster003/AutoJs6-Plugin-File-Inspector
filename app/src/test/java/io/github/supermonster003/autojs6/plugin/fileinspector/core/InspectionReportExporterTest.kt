package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class InspectionReportExporterTest {

    @Test
    fun markdownContainsEveryVisibleSectionAndUsesSafeCodeFences() {
        val output = InspectionReportExporter.export(
            sampleModel(fileName = "archive ``` final.zip"),
            InspectionReportFormat.MARKDOWN,
        )

        assertTrue(output.startsWith("# File Inspector\n\n"))
        assertTrue(output.contains("````text\narchive ``` final.zip\n````"))
        assertTrue(output.contains("## File information"))
        assertTrue(output.contains("- MIME type: application/zip"))
        assertTrue(output.contains("- ZIP container hint: Android application package"))
        assertTrue(output.contains("## Checksums"))
        assertTrue(output.contains("- **MD5 (Legacy):** `00000000000000000000000000000000`"))
        assertTrue(output.contains("- **SHA-256:** `11111111111111111111111111111111`"))
        assertTrue(output.contains("## File header"))
        assertTrue(output.endsWith("```text\n00000000  50 4b 03 04  |PK..|\n```\n"))
    }

    @Test
    fun markdownEscapesDisplayMarkupWithoutChangingTechnicalValues() {
        val model = sampleModel().copy(
            fileInformationTitle = "File *information*",
            file = sampleModel().file.copy(
                mimeTypeDisplay = "MIME [type]: application/zip",
            ),
        )

        val output = InspectionReportExporter.export(model, InspectionReportFormat.MARKDOWN)

        assertTrue(output.contains("## File \\*information\\*"))
        assertTrue(output.contains("- MIME \\[type\\]: application/zip"))
        assertTrue(output.contains("`00000000000000000000000000000000`"))
    }

    @Test
    fun jsonContainsStableRawValuesAndLocalizedDisplayValues() {
        val output = InspectionReportExporter.export(
            sampleModel(fileName = "quote\" line\nslash\\\u0001"),
            InspectionReportFormat.JSON,
        )

        assertTrue(output.startsWith("{\n  \"schemaVersion\": 1,"))
        assertTrue(output.contains("\"name\": \"quote\\\" line\\nslash\\\\\\u0001\""))
        assertTrue(output.contains("\"declaredSize\": {\n      \"bytes\": 4,"))
        assertTrue(output.contains("\"signatureIds\": [\n        \"zip\""))
        assertTrue(output.contains("\"container\": {\n      \"kind\": \"android_package\""))
        assertTrue(output.contains("\"printableRatio\": 0.5"))
        assertTrue(output.contains("\"entropyBitsPerByte\": 2.0"))
        assertTrue(output.contains("\"algorithm\": \"md5\""))
        assertTrue(output.contains("\"legacy\": true"))
        assertTrue(output.endsWith("}\n"))
    }

    @Test
    fun jsonUsesNullForAbsentOptionalRawValues() {
        val base = sampleModel()
        val model = base.copy(
            file = base.file.copy(extension = null),
            analysis = base.analysis.copy(
                containerKind = null,
                containerHintDisplay = null,
                bomKind = null,
            ),
        )

        val output = InspectionReportExporter.export(model, InspectionReportFormat.JSON)

        assertTrue(output.contains("\"extension\": {\n      \"value\": null,"))
        assertTrue(output.contains("\"container\": null"))
        assertTrue(output.contains("\"byteOrderMark\": {\n      \"kind\": null,"))
    }

    @Test
    fun jsonNumbersAreLocaleIndependent() {
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.GERMANY)
            val output = InspectionReportExporter.export(
                sampleModel().copy(
                    analysis = sampleModel().analysis.copy(
                        printableRatio = 0.125,
                        entropyBitsPerByte = 7.75,
                    ),
                ),
                InspectionReportFormat.JSON,
            )

            assertTrue(output.contains("\"printableRatio\": 0.125"))
            assertTrue(output.contains("\"entropyBitsPerByte\": 7.75"))
            assertFalse(output.contains("0,125"))
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun jsonRejectsNonFiniteAnalysisNumbers() {
        val base = sampleModel()
        val model = base.copy(
            analysis = base.analysis.copy(entropyBitsPerByte = Double.NaN),
        )

        assertThrows(IllegalArgumentException::class.java) {
            InspectionReportExporter.export(model, InspectionReportFormat.JSON)
        }
    }

    private fun sampleModel(fileName: String = "sample.apk") = InspectionReportExportModel(
        title = "File Inspector",
        fileInformationTitle = "File information",
        checksumsTitle = "Checksums",
        fileHeaderTitle = "File header",
        legacyLabel = "Legacy",
        file = InspectionExportFile(
            name = fileName,
            mimeType = "application/zip",
            extension = "apk",
            declaredSizeBytes = 4L,
            actualSizeBytes = 4L,
            mimeTypeDisplay = "MIME type: application/zip",
            extensionDisplay = "Extension: apk",
            declaredSizeDisplay = "Declared size: 4 B",
            actualSizeDisplay = "Actual size: 4 B",
        ),
        analysis = InspectionExportAnalysis(
            signatureIds = listOf("zip"),
            detectedFormatDisplay = "Detected format: ZIP container signature",
            containerKind = "android_package",
            containerHintDisplay = "ZIP container hint: Android application package",
            contentKind = "likely_binary",
            sampleSizeBytes = 4,
            printableRatio = 0.5,
            entropyBitsPerByte = 2.0,
            contentAnalysisDisplay = "Content estimate: likely binary",
            bomKind = "utf8",
            textEncodingDisplay = "Byte order mark: UTF-8 BOM",
        ),
        checksums = listOf(
            InspectionExportChecksum(
                algorithm = "md5",
                label = "MD5",
                value = "0".repeat(32),
                legacy = true,
            ),
            InspectionExportChecksum(
                algorithm = "sha256",
                label = "SHA-256",
                value = "1".repeat(32),
                legacy = false,
            ),
        ),
        header = InspectionExportHeader(
            byteCount = 4,
            hexAscii = "00000000  50 4b 03 04  |PK..|",
            display = "00000000  50 4b 03 04  |PK..|",
        ),
    )
}
