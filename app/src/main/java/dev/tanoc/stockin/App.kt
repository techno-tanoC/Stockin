package dev.tanoc.stockin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val URL = "url"
        const val THUMBNAIL = "thumbnail"
    }
}
