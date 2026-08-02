package io.github.supermonster003.autojs6.plugin.fileinspector.core

import java.io.InputStream

interface InspectorSource {

    val declaredSize: Long?

    fun openStream(): InputStream
}
