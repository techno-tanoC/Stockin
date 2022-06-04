package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.PrefForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.PrefViewModel

class PrefActivity : ComponentActivity() {
    private lateinit var prefViewModel: PrefViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (this.application as App).appContainer
        prefViewModel = PrefViewModel(appContainer.prefRepository)

        setContent {
            StockinTheme {
                View()
            }
        }
    }

    @Composable
    fun View() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Stockin")
                    },
                )
            }
        ) {
            Form()
        }
    }

    @Composable
    fun Form() {
        val token = remember { mutableStateOf("") }
        val onTokenChanged = { input: String ->
            token.value = input
        }
        val onSubmit = {
            prefViewModel.update(token.value)
            finish()
        }
        val onClear = {
            prefViewModel.clear()
            finish()
        }

        PrefForm(
            token = token.value,
            onTokenChanged = onTokenChanged,
            onSubmit = onSubmit,
            onClear = onClear
        )
    }
}
