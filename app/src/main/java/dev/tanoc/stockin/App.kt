package dev.tanoc.stockin

import android.app.Application
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.dataStore
import dev.tanoc.stockin.di.RetrofitModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        val tokenFlow = (this@App).applicationContext.dataStore.data.map {
            it[PrefRepository.tokenKey] ?: ""
        }
        runBlocking {
            if (tokenFlow.first() == "") {
                Toast.makeText(this@App, "Empty token", Toast.LENGTH_LONG).show()
            }
        }
        RetrofitModule.setTokenFlow(tokenFlow)

        super.onCreate()
    }

    companion object {
        const val ID = "id"
        const val TITLE = "title"
        const val URL = "url"
        const val THUMBNAIL = "thumbnail"
    }
}
