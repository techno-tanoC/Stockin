package dev.tanoc.android.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import dev.tanoc.android.stockin.StockinApplication
import dev.tanoc.android.stockin.ui.theme.StockinTheme
import kotlinx.coroutines.launch

class PreferencesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            View()
        }
    }

    @Composable
    fun View() {
        StockinTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Preferences") },
                    )
                },
            ) {
                Form()
            }
        }
    }

    @Composable
    fun Form() {
        val baseUrl = remember { mutableStateOf("") }
        val token = remember { mutableStateOf("") }

        val onBaseUrlChanged = { input: String ->
            baseUrl.value = input
        }
        val onTokenChanged = { input: String ->
            token.value = input
        }
        val onSubmit = {
            lifecycleScope.launch {
                (application as StockinApplication).savePref(baseUrl.value, token.value)
            }
            Unit
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(7.dp),
        ) {
            OutlinedTextField(
                value = baseUrl.value,
                onValueChange = onBaseUrlChanged,
                label = { Text("Base URL") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
            )
            OutlinedTextField(
                value = token.value,
                onValueChange = onTokenChanged,
                label = { Text("Bearer Token") },
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
            )
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .padding(3.dp, 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = "Submit",
                    modifier = Modifier.padding(11.dp),
                )
            }
        }
    }
}
