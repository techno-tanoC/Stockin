package dev.tanoc.android.stockin

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Pref(val baseUrl: String, val token: String) {
    fun isValid(): Boolean {
        return baseUrl != "" && token != ""
    }
}

class StockinApplication : Application() {
    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private val baseUrlKey = stringPreferencesKey("stockin_base_url_key")
    private val tokenKey = stringPreferencesKey("stockin_token_key")

    fun flowPref(): Flow<Pref> {
        return dataStore.data.map { settings ->
            val b = settings[baseUrlKey] ?: ""
            val t = settings[tokenKey] ?: ""
            Pref(b, t)
        }
    }

    suspend fun savePref(baseUrl: String, token: String) {
        dataStore.edit { settings ->
            settings[baseUrlKey] = baseUrl
            settings[tokenKey] = token
        }
    }
}
