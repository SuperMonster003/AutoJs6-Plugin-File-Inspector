package io.github.supermonster003.autojs6.plugin.fileinspector.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ZipContainerInspectorTest {

    @Test
    fun recognizesCommonZipContainerExtensionsCaseInsensitively() {
        val cases = mapOf(
            "release.APK" to ZipContainerKind.ANDROID_PACKAGE,
            "library.jar" to ZipContainerKind.JAVA_ARCHIVE,
            "report.docx" to ZipContainerKind.OFFICE_OPEN_XML,
            "slides.PPTM" to ZipContainerKind.OFFICE_OPEN_XML,
            "sheet.ods" to ZipContainerKind.OPEN_DOCUMENT,
            "book.epub" to ZipContainerKind.EPUB,
        )

        cases.forEach { (name, expected) ->
            assertEquals(expected, ZipContainerInspector.inspect(name, listOf(FileSignature.ZIP)))
        }
    }

    @Test
    fun requiresBothRecognizedExtensionAndZipSignature() {
        assertNull(ZipContainerInspector.inspect("release.apk", emptyList()))
        assertNull(ZipContainerInspector.inspect("archive.zip", listOf(FileSignature.ZIP)))
        assertNull(ZipContainerInspector.inspect("document.docx.txt", listOf(FileSignature.ZIP)))
        assertNull(ZipContainerInspector.inspect(".docx", listOf(FileSignature.ZIP)))
    }
}
