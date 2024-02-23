package dev.tanoc.stockin.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PrefRepository @Inject constructor(
    @ApplicationContext val context: Context,
) {
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

    companion object {
        val tokenKey = stringPreferencesKey("token_key")
    }
}
