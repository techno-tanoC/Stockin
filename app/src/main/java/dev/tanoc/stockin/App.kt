package dev.tanoc.stockin

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltAndroidApp
class App : Application() {
    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val URL = "url"
        const val THUMBNAIL = "thumbnail"
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object TokenStore {
    private val tokenKey = stringPreferencesKey("token_key")

    fun Context.tokenFlow(): Flow<String> {
        return this.dataStore.data.map {
            it[tokenKey] ?: ""
        }
    }

    suspend fun Context.setToken(token: String) {
        this.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun Context.clearToken() {
        this.dataStore.edit {
            it.clear()
        }
    }
}
