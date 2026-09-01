package io.github.supermonster003.autojs6.plugin.fileinspector

import android.app.Application
import com.google.android.material.color.DynamicColors

class FileInspectorApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
