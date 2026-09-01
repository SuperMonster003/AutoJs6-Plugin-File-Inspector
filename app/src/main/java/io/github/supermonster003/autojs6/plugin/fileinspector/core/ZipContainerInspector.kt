package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.util.Locale

enum class ZipContainerKind {
    ANDROID_PACKAGE,
    JAVA_ARCHIVE,
    OFFICE_OPEN_XML,
    OPEN_DOCUMENT,
    EPUB,
}

object ZipContainerInspector {

    fun inspect(displayName: String, signatures: Collection<FileSignature>): ZipContainerKind? {
        if (FileSignature.ZIP !in signatures) return null
        return when (displayName.extension()) {
            in ANDROID_EXTENSIONS -> ZipContainerKind.ANDROID_PACKAGE
            in JAVA_EXTENSIONS -> ZipContainerKind.JAVA_ARCHIVE
            in OFFICE_OPEN_XML_EXTENSIONS -> ZipContainerKind.OFFICE_OPEN_XML
            in OPEN_DOCUMENT_EXTENSIONS -> ZipContainerKind.OPEN_DOCUMENT
            "epub" -> ZipContainerKind.EPUB
            else -> null
        }
    }

    private fun String.extension(): String? {
        val separator = lastIndexOf('.')
        return takeIf { separator > 0 && separator < lastIndex }
            ?.substring(separator + 1)
            ?.lowercase(Locale.ROOT)
    }

    private val ANDROID_EXTENSIONS = setOf("apk", "aab", "apks", "xapk")
    private val JAVA_EXTENSIONS = setOf("jar", "war", "ear")
    private val OFFICE_OPEN_XML_EXTENSIONS = setOf(
        "docx", "docm", "dotx", "dotm",
        "xlsx", "xlsm", "xltx", "xltm",
        "pptx", "pptm", "potx", "potm", "ppsx", "ppsm",
    )
    private val OPEN_DOCUMENT_EXTENSIONS = setOf(
        "odt", "ott", "ods", "ots", "odp", "otp", "odg", "otg",
    )
}
