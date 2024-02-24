package dev.tanoc.stockin

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltAndroidApp
class App : Application() {
    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val URL = "url"
        const val THUMBNAIL = "thumbnail"
    }
}
