package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowInsetsControllerCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.MaterialColors
import io.github.supermonster003.autojs6.plugin.fileinspector.databinding.ActivityFileInspectorBinding
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.Locale
import kotlin.math.ceil

@RunWith(AndroidJUnit4::class)
class FileInspectorLayoutTest {

    @Test
    fun completedReportPassesLayoutThemeAndExportChecks() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val testDirectory = File(targetContext.cacheDir, TEST_DIRECTORY).apply {
            require(mkdirs() || isDirectory) { "Could not create test directory: $absolutePath" }
        }
        val testFile = File(testDirectory, LONG_FILE_NAME).apply {
            writeBytes(testPayload())
        }
        val authority = "${targetContext.packageName}.debug.files"
        val targetUri = FileProvider.getUriForFile(targetContext, authority, testFile)
        val parentUri = targetUri.buildUpon()
            .path(targetUri.pathSegments.dropLast(1).joinToString(separator = "/", prefix = "/"))
            .build()
        val intent = inspectionIntent(
            targetContext.packageName,
            targetUri,
            parentUri,
            testFile.length(),
        )
        targetContext.grantUriPermission(
            targetContext.packageName,
            targetUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION,
        )

        val activity = instrumentation.startActivitySync(intent) as FileInspectorActivity
        try {
            waitForInspection(activity)
            val content = activity.findViewById<ViewGroup>(android.R.id.content)
            val binding = ActivityFileInspectorBinding.bind(content.getChildAt(0))
            instrumentation.runOnMainSync {
                assertTheme(activity)
                assertLayout(activity, binding)
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(SCREENSHOT_SETTLE_MILLIS)
            captureScreenshot("top")

            instrumentation.runOnMainSync {
                binding.scrollContainer.fullScroll(View.FOCUS_DOWN)
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(SCROLL_SETTLE_MILLIS)
            instrumentation.runOnMainSync {
                val visibleBounds = Rect()
                assertTrue(
                    "Header copy action must be reachable after scrolling",
                    binding.copyHeader.getGlobalVisibleRect(visibleBounds) && visibleBounds.height() > 0,
                )
            }
            captureScreenshot("bottom")

            instrumentation.runOnMainSync {
                assertExportsMatchScreen(activity, binding)
            }
        } finally {
            instrumentation.runOnMainSync { activity.finish() }
            targetContext.revokeUriPermission(targetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            testFile.delete()
        }
    }

    private fun inspectionIntent(
        targetPackage: String,
        targetUri: android.net.Uri,
        parentUri: android.net.Uri,
        size: Long,
    ): Intent = Intent(ExplorerActionPluginActions.EXECUTE).apply {
        setClassName(targetPackage, FileInspectorActivity::class.java.name)
        setDataAndType(targetUri, TEST_MIME_TYPE)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        clipData = ClipData(
            "inspection",
            arrayOf(TEST_MIME_TYPE),
            ClipData.Item(targetUri),
        ).apply {
            addItem(ClipData.Item(parentUri))
        }
        putExtra(ExplorerActionIntentExtras.ACTION_ID, TEST_ACTION_ID)
        putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, ExplorerActionProtocol.VERSION)
        putExtra(
            ExplorerActionIntentExtras.SOURCE_SURFACE,
            ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
        )
        putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
        putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, LONG_FILE_NAME)
        putExtra(ExplorerActionIntentExtras.SIZE, size)
    }

    private fun waitForInspection(activity: FileInspectorActivity) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val expected = activity.getString(R.string.inspect_complete)
        val deadline = SystemClock.elapsedRealtime() + INSPECTION_TIMEOUT_MILLIS
        while (SystemClock.elapsedRealtime() < deadline) {
            var completed = false
            instrumentation.runOnMainSync {
                completed = activity.findViewById<TextView>(R.id.status).text.toString() == expected
            }
            if (completed) return
            SystemClock.sleep(POLL_INTERVAL_MILLIS)
        }
        throw AssertionError("Inspection did not complete within ${INSPECTION_TIMEOUT_MILLIS} ms")
    }

    private fun assertTheme(activity: FileInspectorActivity) {
        val configuration = activity.resources.configuration
        val isNight = configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        val surface = MaterialColors.getColor(activity, com.google.android.material.R.attr.colorSurface, 0)
        val primary = MaterialColors.getColor(activity, androidx.appcompat.R.attr.colorPrimary, 0)
        val surfaceLuminance = ColorUtils.calculateLuminance(surface)
        if (isNight) {
            assertTrue("Night surface must be dark", surfaceLuminance < MAXIMUM_DARK_SURFACE_LUMINANCE)
        } else {
            assertTrue("Light surface must be light", surfaceLuminance > MINIMUM_LIGHT_SURFACE_LUMINANCE)
        }

        val systemBarController = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        assertEquals(MaterialColors.isColorLight(primary), systemBarController.isAppearanceLightStatusBars)
        assertEquals(MaterialColors.isColorLight(surface), systemBarController.isAppearanceLightNavigationBars)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && DynamicColors.isDynamicColorAvailable()) {
            val fallbackPrimary = activity.getColor(R.color.color_primary)
            assertNotEquals("Dynamic color should replace the fallback primary", fallbackPrimary, primary)
        }
    }

    private fun assertLayout(
        activity: FileInspectorActivity,
        binding: ActivityFileInspectorBinding,
    ) {
        assertEquals(View.VISIBLE, binding.resultsContainer.visibility)
        assertEquals(LONG_FILE_NAME, binding.fileName.text.toString())
        assertFalse("Long file name must not be ellipsized", binding.fileName.hasEllipsis())

        val expectedDirection = TextUtils.getLayoutDirectionFromLocale(
            activity.resources.configuration.locales[0] ?: Locale.getDefault(),
        )
        assertEquals(expectedDirection, binding.root.layoutDirection)
        assertEquals(View.LAYOUT_DIRECTION_LTR, binding.headerHex.layoutDirection)

        val allViews = binding.root.descendantsAndSelf().toList()
        val minimumTouchPixels = ceil(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                MINIMUM_TOUCH_TARGET_DP,
                activity.resources.displayMetrics,
            ).toDouble(),
        ).toInt()
        val undersized = allViews.filter { view ->
            view.visibility == View.VISIBLE && view.isClickable &&
                (view.width < minimumTouchPixels || view.height < minimumTouchPixels)
        }
        assertTrue(
            "Clickable views below ${MINIMUM_TOUCH_TARGET_DP.toInt()} dp: " +
                undersized.joinToString { view -> view.debugName() },
            undersized.isEmpty(),
        )

        val clippedText = allViews.filterIsInstance<TextView>().filter { textView ->
            textView.visibility == View.VISIBLE && textView.text.isNotEmpty() && textView.hasEllipsis()
        }
        assertTrue(
            "Ellipsized visible text: ${clippedText.joinToString { view -> view.debugName() }}",
            clippedText.isEmpty(),
        )

        val configuration = activity.resources.configuration
        if (configuration.screenWidthDp <= NARROW_SCREEN_DP || configuration.fontScale >= LARGE_FONT_SCALE) {
            assertTrue("Long file name should wrap on narrow/large-font layouts", binding.fileName.lineCount > 1)
            val longestDigest = allViews.filterIsInstance<TextView>()
                .filter { view -> view.id == R.id.digest }
                .maxByOrNull { view -> view.text.length }
            assertTrue(
                "The longest checksum should wrap on narrow/large-font layouts",
                requireNotNull(longestDigest).lineCount > 1,
            )
        }
    }

    private fun assertExportsMatchScreen(
        activity: FileInspectorActivity,
        binding: ActivityFileInspectorBinding,
    ) {
        val visibleReportValues = buildList {
            add(binding.fileName.text.toString())
            add(binding.mimeType.text.toString())
            add(binding.extension.text.toString())
            add(binding.declaredSize.text.toString())
            add(binding.actualSize.text.toString())
            add(binding.detectedFormat.text.toString())
            if (binding.containerHint.visibility == View.VISIBLE) {
                add(binding.containerHint.text.toString())
            }
            add(binding.contentAnalysis.text.toString())
            add(binding.textEncoding.text.toString())
            add(binding.headerHex.text.toString())
        }
        val checksumValues = binding.root.descendantsAndSelf()
            .filterIsInstance<TextView>()
            .filter { view -> view.id == R.id.digest }
            .map { view -> view.text.toString() }
            .toList()

        binding.reportFormatMarkdown.performClick()
        binding.copyReport.performClick()
        val markdown = clipboardText(activity)
        (visibleReportValues + checksumValues).forEach { value ->
            assertTrue("Markdown report is missing on-screen value: $value", markdown.contains(value))
        }

        binding.reportFormatJson.performClick()
        binding.copyReport.performClick()
        val json = clipboardText(activity)
        (visibleReportValues.dropLast(1) + checksumValues).forEach { value ->
            assertTrue("JSON report is missing on-screen value: $value", json.contains(value))
        }
        binding.headerHex.text.toString().lineSequence().forEach { headerLine ->
            assertTrue("JSON report is missing header line: $headerLine", json.contains(headerLine))
        }
        assertTrue(json.contains("\"schemaVersion\": 1"))
    }

    private fun clipboardText(activity: FileInspectorActivity): String {
        val clipboard = activity.getSystemService(ClipboardManager::class.java)
        return clipboard.primaryClip?.getItemAt(0)?.coerceToText(activity)?.toString().orEmpty()
    }

    private fun captureScreenshot(position: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val requestedName = InstrumentationRegistry.getArguments()
            .getString(SCREENSHOT_ARGUMENT)
            .orEmpty()
            .ifBlank { "file-inspector" }
            .replace(UNSAFE_FILE_NAME_CHARACTERS, "-")
        val directory = File(
            instrumentation.targetContext.getExternalFilesDir(null),
            SCREENSHOT_DIRECTORY,
        ).apply { mkdirs() }
        val output = File(directory, "$requestedName-$position.png")
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        output.outputStream().use { stream ->
            assertTrue("Screenshot compression failed", bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream))
        }
        bitmap.recycle()
        println("SCREENSHOT:${output.absolutePath}")
    }

    private fun testPayload(): ByteArray = ByteArray(TEST_PAYLOAD_BYTES) { index ->
        (index * 31 + 17).toByte()
    }.apply {
        this[0] = 0x50.toByte()
        this[1] = 0x4B.toByte()
        this[2] = 0x03.toByte()
        this[3] = 0x04.toByte()
    }

    private fun View.descendantsAndSelf(): Sequence<View> = sequence {
        yield(this@descendantsAndSelf)
        if (this@descendantsAndSelf is ViewGroup) {
            repeat(childCount) { index ->
                yieldAll(getChildAt(index).descendantsAndSelf())
            }
        }
    }

    private fun TextView.hasEllipsis(): Boolean {
        val textLayout = layout ?: return false
        return (0 until textLayout.lineCount).any { line -> textLayout.getEllipsisCount(line) > 0 }
    }

    private fun View.debugName(): String = if (id == View.NO_ID) {
        javaClass.simpleName
    } else {
        runCatching { resources.getResourceEntryName(id) }.getOrDefault(javaClass.simpleName)
    }

    private companion object {
        const val TEST_ACTION_ID = "inspect-file"
        const val TEST_DIRECTORY = "inspection"
        const val TEST_MIME_TYPE = "application/vnd.android.package-archive"
        const val TEST_PAYLOAD_BYTES = 64 * 1024
        const val INSPECTION_TIMEOUT_MILLIS = 15_000L
        const val POLL_INTERVAL_MILLIS = 50L
        const val SCROLL_SETTLE_MILLIS = 300L
        const val SCREENSHOT_SETTLE_MILLIS = 500L
        const val MINIMUM_TOUCH_TARGET_DP = 48f
        const val MAXIMUM_DARK_SURFACE_LUMINANCE = 0.25
        const val MINIMUM_LIGHT_SURFACE_LUMINANCE = 0.75
        const val NARROW_SCREEN_DP = 320
        const val LARGE_FONT_SCALE = 1.5f
        const val SCREENSHOT_ARGUMENT = "screenshotName"
        const val SCREENSHOT_DIRECTORY = "screenshots"
        val UNSAFE_FILE_NAME_CHARACTERS = Regex("[^A-Za-z0-9._-]")
        val LONG_FILE_NAME = "file-inspector-" + "long-segment_".repeat(8) + "نموذج.apk"
    }
}
