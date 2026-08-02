package io.github.supermonster003.autojs6.plugin.fileinspector

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.IExplorerActionPlugin

class ExplorerActionService : Service() {

    private val binder = object : IExplorerActionPlugin.Stub() {
        override fun getInfo() = fileInspectorPluginInfo()

        override fun getActionCatalog() = fileInspectorActionCatalog()
    }

    override fun onBind(intent: Intent?): IBinder? =
        binder.takeIf { intent?.action == ExplorerActionPluginActions.EXPLORER_ACTION }
}
