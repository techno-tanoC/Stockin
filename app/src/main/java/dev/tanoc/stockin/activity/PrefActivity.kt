package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import dev.tanoc.stockin.App
import dev.tanoc.stockin.component.PrefForm
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.*

class PrefActivity : ComponentActivity() {
    private val viewModel by lazy {
        val appContainer = (this.application as App).appContainer
        val factory = PrefViewModelFactory(
            appContainer.prefRepository,
        )
        ViewModelProvider(this, factory).get(RealPrefViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StockinTheme {
                PrefScreen(
                    vm = viewModel,
                    finish = { finish() },
                )
            }
        }
    }
}

@Composable
fun PrefScreen(
    vm: PrefViewModel,
    finish: () -> Unit,
) {
    val (_, effect, dispatch) = use(vm)

    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is PrefViewModel.Effect.Finish -> {
                    finish()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Stockin")
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
        ) {
            PrefForm(
                dispatch = dispatch,
            )
        }
    }
}

@Composable
fun PrefForm(
    dispatch: (PrefViewModel.Event) -> Unit,
) {
    val token = remember { mutableStateOf("") }
    val onTokenChanged = { input: String ->
        token.value = input
    }
    val onSubmit = {
        dispatch(PrefViewModel.Event.UpdateToken(token.value))
    }
    val onClear = {
        dispatch(PrefViewModel.Event.ClearToken)
    }

    PrefForm(
        token = token.value,
        onTokenChanged = onTokenChanged,
        onSubmit = onSubmit,
        onClear = onClear
    )
}
