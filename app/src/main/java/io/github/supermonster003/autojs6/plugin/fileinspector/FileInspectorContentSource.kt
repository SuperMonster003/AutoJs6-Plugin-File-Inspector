package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

/** Opens the validated target URI at most once, including failed open attempts. */
internal class FileInspectorContentSource internal constructor(
    val request: FileInspectionRequest,
    private val opener: (Uri) -> InputStream?,
) {

    constructor(contentResolver: ContentResolver, request: FileInspectionRequest) : this(
        request = request,
        opener = contentResolver::openInputStream,
    )

    private val openAttempted = AtomicBoolean(false)

    fun openInputStream(): InputStream {
        check(openAttempted.compareAndSet(false, true)) { "File Inspector input stream was already opened" }
        return opener(request.targetUri)
            ?: throw FileNotFoundException("Content provider returned no stream for the inspected file")
    }

    companion object {
        fun resolve(contentResolver: ContentResolver, intent: Intent?): FileInspectorContentSource? =
            FileInspectorIntentPolicy.resolve(intent)?.let { request ->
                FileInspectorContentSource(contentResolver, request)
            }
    }
}
