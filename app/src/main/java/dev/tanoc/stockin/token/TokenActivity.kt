package dev.tanoc.stockin.token

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.TokenStore.clearToken
import dev.tanoc.stockin.TokenStore.setToken
import dev.tanoc.stockin.ui.theme.StockinTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TokenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val scope = rememberCoroutineScope()
            StockinTheme {
                TokenScreen(
                    onSubmit = {
                        scope.launch {
                            this@TokenActivity.setToken(it)
                            finish()
                        }
                    },
                    onClear = {
                        scope.launch {
                            this@TokenActivity.clearToken()
                            finish()
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun TokenScreen(
    onSubmit: (String) -> Unit,
    onClear: () -> Unit,
) {
    StockinScaffold {
        TokenForm(
            onSubmit = onSubmit,
            onClear = onClear
        )
    }
}

@Composable
fun TokenForm(
    onSubmit: (String) -> Unit,
    onClear: () -> Unit,
) {
    var token by remember { mutableStateOf("") }
    val onTokenChanged = { input: String ->
        token = input
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChanged,
            label = { Text("Token") },
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
        )
        Button(
            onClick = { onSubmit(token) },
            modifier = Modifier
                .padding(4.dp, 8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Submit",
                modifier = Modifier
                    .padding(12.dp)
            )
        }
        Button(
            onClick = onClear,
            modifier = Modifier
                .padding(4.dp, 8.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Clear",
                modifier = Modifier
                    .padding(12.dp)
            )
        }
    }
}
