package io.github.supermonster003.autojs6.plugin.fileinspector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol

class FileInspectorActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isSupportedRequest(intent)) {
            finish()
            return
        }
        setContentView(
            TextView(this).apply {
                gravity = Gravity.CENTER
                setPadding(48, 48, 48, 48)
                setText(R.string.placeholder_message)
            },
        )
    }

    private fun isSupportedRequest(request: Intent?): Boolean {
        request ?: return false
        if (request.action != ExplorerActionPluginActions.EXECUTE) return false
        if (request.getStringExtra(ExplorerActionIntentExtras.ACTION_ID) != FileInspectorPlugin.ACTION_ID) {
            return false
        }
        if (
            request.getIntExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MIN_VALUE) !=
            ExplorerActionProtocol.VERSION
        ) {
            return false
        }
        if (request.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION == 0) return false
        if (request.flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0) return false
        return request.data?.scheme == "content"
    }
}
