package dev.tanoc.stockin

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TokenStore {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
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
