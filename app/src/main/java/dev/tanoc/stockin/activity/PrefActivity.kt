package dev.tanoc.stockin.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import dagger.hilt.android.AndroidEntryPoint
import dev.tanoc.stockin.component.PrefForm
import dev.tanoc.stockin.component.StockinScaffold
import dev.tanoc.stockin.ui.theme.StockinTheme
import dev.tanoc.stockin.viewmodel.PrefViewModel
import dev.tanoc.stockin.viewmodel.RealPrefViewModel
import dev.tanoc.stockin.viewmodel.use
import javax.inject.Inject

@AndroidEntryPoint
class PrefActivity : ComponentActivity() {
    @Inject lateinit var viewModel: RealPrefViewModel

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

    StockinScaffold {
        PrefForm(
            dispatch = dispatch,
        )
    }
}

@Composable
fun PrefForm(
    dispatch: (PrefViewModel.Event) -> Unit,
) {
    var token by remember { mutableStateOf("") }
    val onTokenChanged = { input: String ->
        token = input
    }
    val onSubmit = {
        dispatch(PrefViewModel.Event.UpdateToken(token))
    }
    val onClear = {
        dispatch(PrefViewModel.Event.ClearToken)
    }

    PrefForm(
        token = token,
        onTokenChanged = onTokenChanged,
        onSubmit = onSubmit,
        onClear = onClear
    )
}
