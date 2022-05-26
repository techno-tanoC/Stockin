package dev.tanoc.stockin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.tanoc.stockin.model.Pref
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PrefRepository(private val context: Context) {
    private val tokenKey = stringPreferencesKey("token_key")
    val prefFlow =
        context.dataStore.data.map { preferences ->
            preferences[tokenKey]?.let { token ->
                Pref(token)
            }
        }

    suspend fun setPref(token: String) {
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun clearPref() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
