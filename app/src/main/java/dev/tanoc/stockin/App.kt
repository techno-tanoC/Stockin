package dev.tanoc.stockin

import android.app.Application

class App : Application() {
    val appContainer by lazy {
        AppContainer(this)
    }
}
