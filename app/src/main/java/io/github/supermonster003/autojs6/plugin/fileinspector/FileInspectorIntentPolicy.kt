package io.github.supermonster003.autojs6.plugin.fileinspector

import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import java.util.Locale

internal data class FileInspectionRequest(
    val targetUri: Uri,
    val parentUri: Uri,
    val displayName: String,
    val declaredSize: Long,
    val mimeType: String,
)

/** Validates the complete read-only Explorer Action contract before content is opened. */
internal object FileInspectorIntentPolicy {

    const val MAX_DECLARED_SIZE = 8L * 1024L * 1024L * 1024L * 1024L
    const val MAX_DISPLAY_NAME_LENGTH = 255

    private val mimeTokenPattern = Regex("[a-z0-9][a-z0-9!#$&^_.+-]*")

    fun resolve(intent: Intent?): FileInspectionRequest? = try {
        resolveUnchecked(intent)
    } catch (_: RuntimeException) {
        null
    }

    private fun resolveUnchecked(intent: Intent?): FileInspectionRequest? {
        intent ?: return null
        if (intent.action != ExplorerActionPluginActions.EXECUTE) return null
        if (intent.getStringExtra(ExplorerActionIntentExtras.ACTION_ID) != FileInspectorPlugin.ACTION_ID) {
            return null
        }
        if (
            intent.getIntExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MIN_VALUE) !=
            ExplorerActionProtocol.VERSION
        ) {
            return null
        }
        if (
            intent.getStringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE) !=
            ExplorerActionIntentValues.SOURCE_SURFACE_MAIN
        ) {
            return null
        }

        if (intent.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0) return null
        val forbiddenGrantFlags =
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        if (intent.flags and forbiddenGrantFlags != 0) return null

        val targetUri = intent.data?.takeIf(::isPlainContentUri) ?: return null
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI)
            ?.takeIf(::isPlainContentUri)
            ?: return null
        if (!isStrictDescendant(parentUri, targetUri)) return null

        val clipData = intent.clipData ?: return null
        if (clipData.itemCount != REQUIRED_CLIP_ITEM_COUNT) return null
        if (!clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_TARGET_INDEX).isExactUri(targetUri)) {
            return null
        }
        if (!clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_PARENT_INDEX).isExactUri(parentUri)) {
            return null
        }

        val displayName = validateDisplayName(
            intent.getStringExtra(ExplorerActionIntentExtras.DISPLAY_NAME),
            targetUri,
        ) ?: return null
        if (!intent.hasExtra(ExplorerActionIntentExtras.SIZE)) return null
        val declaredSize = runCatching {
            intent.getLongExtra(ExplorerActionIntentExtras.SIZE, INVALID_DECLARED_SIZE)
        }.getOrNull()?.takeIf(::isDeclaredSizeAccepted) ?: return null
        val mimeType = normalizeMimeType(intent.type) ?: return null

        return FileInspectionRequest(
            targetUri = targetUri,
            parentUri = parentUri,
            displayName = displayName,
            declaredSize = declaredSize,
            mimeType = mimeType,
        )
    }

    fun isDeclaredSizeAccepted(size: Long): Boolean = size in 0L..MAX_DECLARED_SIZE

    fun normalizeMimeType(value: String?): String? {
        val raw = value ?: return null
        if (raw.isEmpty() || raw != raw.trim()) return null
        val normalized = raw.lowercase(Locale.ROOT)
        if (raw != normalized) return null
        if (normalized == WILDCARD_MIME_TYPE) return normalized

        val parts = normalized.split('/')
        if (parts.size != 2) return null
        val type = parts[0]
        val subtype = parts[1]
        if (type == "*" || subtype == "*") return null
        if (!mimeTokenPattern.matches(type) || !mimeTokenPattern.matches(subtype)) return null
        return normalized
    }

    private fun validateDisplayName(value: String?, targetUri: Uri): String? {
        val name = value ?: return null
        if (name.length !in 1..MAX_DISPLAY_NAME_LENGTH || name.isBlank()) return null
        if (name == "." || name == "..") return null
        if (name.any(::isUnsafeNameCharacter)) return null
        if (targetUri.pathSegments.lastOrNull() != name) return null
        return name
    }

    private fun isPlainContentUri(uri: Uri): Boolean {
        if (!uri.isHierarchical || uri.scheme != ContentResolver.SCHEME_CONTENT) return false
        if (uri.authority.isNullOrBlank() || uri.host.isNullOrBlank()) return false
        if (uri.userInfo != null || uri.port != -1) return false
        if (uri.query != null || uri.fragment != null) return false

        val encodedPath = uri.encodedPath ?: return false
        if (!encodedPath.startsWith('/') || encodedPath.length <= 1) return false
        if (encodedPath.split('/').drop(1).any(String::isEmpty)) return false
        val segments = uri.pathSegments
        return segments.isNotEmpty() && segments.none { segment ->
            segment.isEmpty() || segment == "." || segment == ".." || segment.any(::isUnsafeUriCharacter)
        }
    }

    private fun isStrictDescendant(parentUri: Uri, targetUri: Uri): Boolean {
        if (parentUri.scheme != targetUri.scheme || parentUri.authority != targetUri.authority) return false
        val parentSegments = parentUri.pathSegments
        val targetSegments = targetUri.pathSegments
        return targetSegments.size > parentSegments.size &&
            targetSegments.take(parentSegments.size) == parentSegments
    }

    private fun ClipData.Item.isExactUri(expected: Uri): Boolean =
        uri == expected && text == null && htmlText == null && intent == null

    private fun isUnsafeNameCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUriCharacter(character: Char): Boolean =
        character == '/' || character == '\\' || isUnsafeUnicodeCharacter(character)

    private fun isUnsafeUnicodeCharacter(character: Char): Boolean =
        character.isISOControl() || Character.getType(character) == Character.FORMAT.toInt()

    @Suppress("DEPRECATION")
    private fun Intent.parcelableUriExtra(name: String): Uri? = getParcelableExtra(name)

    private const val REQUIRED_CLIP_ITEM_COUNT = 2
    private const val INVALID_DECLARED_SIZE = -1L
    private const val WILDCARD_MIME_TYPE = "*/*"
}
