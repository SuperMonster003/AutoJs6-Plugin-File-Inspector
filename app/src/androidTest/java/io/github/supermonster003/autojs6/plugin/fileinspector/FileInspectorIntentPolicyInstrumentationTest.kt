@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import androidx.test.runner.AndroidJUnit4
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException

@RunWith(AndroidJUnit4::class)
class FileInspectorIntentPolicyInstrumentationTest {

    private val parentUri = Uri.parse("content://org.autojs.test.fileprovider/root/files")
    private val targetUri = Uri.parse("content://org.autojs.test.fileprovider/root/files/sample.bin")

    @Test
    fun completeReadOnlyExplorerContractIsAccepted() {
        val resolved = FileInspectorIntentPolicy.resolve(validIntent())

        assertNotNull(resolved)
        assertEquals(targetUri, resolved?.targetUri)
        assertEquals(parentUri, resolved?.parentUri)
        assertEquals("sample.bin", resolved?.displayName)
        assertEquals(4096L, resolved?.declaredSize)
        assertEquals("application/octet-stream", resolved?.mimeType)
    }

    @Test
    fun identityFieldsAreMandatoryAndExact() {
        assertNull(FileInspectorIntentPolicy.resolve(null))
        assertNull(FileInspectorIntentPolicy.resolve(Intent(validIntent()).setAction(Intent.ACTION_VIEW)))
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply { removeExtra(ExplorerActionIntentExtras.ACTION_ID) },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.ACTION_ID, "inspect-directory"),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MAX_VALUE),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, "1"),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply { removeExtra(ExplorerActionIntentExtras.SOURCE_SURFACE) },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.SOURCE_SURFACE, "secondary"),
            ),
        )
    }

    @Test
    fun grantsAreReadOnlyAndNotPersistable() {
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply { flags = Intent.FLAG_GRANT_PREFIX_URI_PERMISSION },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION),
            ),
        )
    }

    @Test
    fun targetAndParentMustBePlainContentUris() {
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).setData(Uri.parse("file:///sdcard/sample.bin")),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).setData(
                    Uri.parse("content://org.autojs.test.fileprovider/root/files/sample.bin?revision=1"),
                ),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).setData(
                    Uri.parse("content://org.autojs.test.fileprovider/root/files/sample.bin#fragment"),
                ),
            ),
        )

        val queriedParent = Uri.parse("content://org.autojs.test.fileprovider/root/files?revision=1")
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent())
                    .putExtra(ExplorerActionIntentExtras.PARENT_URI, queriedParent)
                    .apply { clipData = clipData(targetUri, queriedParent) },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PARENT_URI, "not-a-uri"),
            ),
        )
        val traversingTarget = Uri.parse(
            "content://org.autojs.test.fileprovider/root/files/%2E%2E/sample.bin",
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent())
                    .setDataAndType(traversingTarget, "application/octet-stream")
                    .apply { clipData = clipData(traversingTarget, parentUri) },
            ),
        )
    }

    @Test
    fun parentMustBeAStrictAncestorWithTheSameAuthority() {
        val siblingParent = Uri.parse("content://org.autojs.test.fileprovider/root/other")
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent())
                    .putExtra(ExplorerActionIntentExtras.PARENT_URI, siblingParent)
                    .apply { clipData = clipData(targetUri, siblingParent) },
            ),
        )

        val otherAuthority = Uri.parse("content://org.example.fileprovider/root/files")
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent())
                    .putExtra(ExplorerActionIntentExtras.PARENT_URI, otherAuthority)
                    .apply { clipData = clipData(targetUri, otherAuthority) },
            ),
        )

        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent())
                    .putExtra(ExplorerActionIntentExtras.PARENT_URI, targetUri)
                    .apply { clipData = clipData(targetUri, targetUri) },
            ),
        )
    }

    @Test
    fun clipDataHasExactlyTwoUriOnlyItemsMatchingTheRequest() {
        assertNull(
            FileInspectorIntentPolicy.resolve(Intent(validIntent()).apply { clipData = null }),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = ClipData.newRawUri("File target", targetUri)
                },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = clipData(targetUri, parentUri).apply {
                        addItem(ClipData.Item(Uri.parse("content://org.autojs.test.fileprovider/root/extra")))
                    }
                },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = clipData(
                        Uri.parse("content://org.autojs.test.fileprovider/root/files/other.bin"),
                        parentUri,
                    )
                },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = ClipData(
                        ClipDescription("File target", arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST)),
                        ClipData.Item("spoof", null, null, targetUri),
                    ).apply { addItem(ClipData.Item(parentUri)) }
                },
            ),
        )
    }

    @Test
    fun displayNameMustBeASafeExactTargetLeaf() {
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply { removeExtra(ExplorerActionIntentExtras.DISPLAY_NAME) },
            ),
        )
        listOf(
            "",
            ".",
            "..",
            "folder/sample.bin",
            "folder\\sample.bin",
            "sample\u0000.bin",
            "sample\u202E.bin",
            "other.bin",
            "x".repeat(FileInspectorIntentPolicy.MAX_DISPLAY_NAME_LENGTH + 1),
        ).forEach { invalidName ->
            assertNull(
                FileInspectorIntentPolicy.resolve(
                    Intent(validIntent()).putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, invalidName),
                ),
            )
        }
    }

    @Test
    fun declaredSizeIsRequiredAndBoundedToEightTebibytes() {
        assertNotNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.SIZE, 0L),
            ),
        )
        assertNotNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(
                    ExplorerActionIntentExtras.SIZE,
                    FileInspectorIntentPolicy.MAX_DECLARED_SIZE,
                ),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).apply { removeExtra(ExplorerActionIntentExtras.SIZE) },
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.SIZE, -1L),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(
                    ExplorerActionIntentExtras.SIZE,
                    FileInspectorIntentPolicy.MAX_DECLARED_SIZE + 1L,
                ),
            ),
        )
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.SIZE, "4096"),
            ),
        )
    }

    @Test
    fun mimeTypeMustBeCanonicalConcreteOrFullWildcard() {
        assertEquals("*/*", FileInspectorIntentPolicy.normalizeMimeType("*/*"))
        assertEquals(
            "application/vnd.example+json",
            FileInspectorIntentPolicy.normalizeMimeType("application/vnd.example+json"),
        )
        listOf(
            null,
            "",
            " Application/JSON",
            "Application/JSON",
            "application/json; charset=utf-8",
            "application/json ",
            "application",
            "*/json",
            "text/*",
            "application/j@son",
        ).forEach { invalidMimeType ->
            assertNull(FileInspectorIntentPolicy.normalizeMimeType(invalidMimeType))
        }

        assertNotNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).setType("*/*"),
            ),
        )
        assertNull(FileInspectorIntentPolicy.resolve(Intent(validIntent()).setType(null)))
        assertNull(
            FileInspectorIntentPolicy.resolve(
                Intent(validIntent()).setType("Application/JSON"),
            ),
        )
    }

    @Test
    fun contentSourceOpensTheValidatedTargetOnlyOnce() {
        val request = requireNotNull(FileInspectorIntentPolicy.resolve(validIntent()))
        var openCount = 0
        var openedUri: Uri? = null
        val source = FileInspectorContentSource(request) { uri ->
            openCount += 1
            openedUri = uri
            ByteArrayInputStream(byteArrayOf(1, 2, 3))
        }

        source.openInputStream().use { input ->
            assertEquals(1, input.read())
        }
        assertEquals(targetUri, openedUri)
        assertThrows(IllegalStateException::class.java) { source.openInputStream() }
        assertEquals(1, openCount)
    }

    @Test
    fun failedContentOpenStillConsumesTheSingleAttempt() {
        val request = requireNotNull(FileInspectorIntentPolicy.resolve(validIntent()))
        var openCount = 0
        val source = FileInspectorContentSource(request) {
            openCount += 1
            null
        }

        assertThrows(FileNotFoundException::class.java) { source.openInputStream() }
        assertThrows(IllegalStateException::class.java) { source.openInputStream() }
        assertEquals(1, openCount)
    }

    private fun validIntent(): Intent =
        Intent(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(targetUri, "application/octet-stream")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, FileInspectorPlugin.ACTION_ID)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, ExplorerActionProtocol.VERSION)
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, "sample.bin")
            .putExtra(ExplorerActionIntentExtras.SIZE, 4096L)
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            .apply { clipData = clipData(targetUri, parentUri) }

    private fun clipData(target: Uri, parent: Uri): ClipData =
        ClipData(
            ClipDescription("File target", arrayOf(ClipDescription.MIMETYPE_TEXT_URILIST)),
            ClipData.Item(target),
        ).apply {
            addItem(ClipData.Item(parent))
        }
}
