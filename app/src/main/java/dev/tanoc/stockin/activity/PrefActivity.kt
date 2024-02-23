package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.Pref.clearPref
import dev.tanoc.stockin.Pref.setPref
import dev.tanoc.stockin.component.PrefForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PrefActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val scope = rememberCoroutineScope()
            StockinTheme {
                PrefScreen(
                    onSubmit = {
                        scope.launch {
                            this@PrefActivity.setPref(it)
                            finish()
                        }
                    },
                    onClear = {
                        scope.launch {
                            this@PrefActivity.clearPref()
                            finish()
                        }
                    },
                    finish = {
                        finish()
                    },
                )
            }
        }
    }
}

@Composable
fun PrefScreen(
    onSubmit: (String) -> Unit,
    onClear: () -> Unit,
    finish: () -> Unit,
) {
    StockinScaffold {
        PrefForm(
            onSubmit = onSubmit,
            onClear = onClear
        )
    }
}
