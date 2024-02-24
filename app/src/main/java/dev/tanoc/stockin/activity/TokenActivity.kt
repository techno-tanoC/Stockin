package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.TokenStore.clearToken
import dev.tanoc.stockin.TokenStore.setToken
import dev.tanoc.stockin.component.TokenForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TokenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
