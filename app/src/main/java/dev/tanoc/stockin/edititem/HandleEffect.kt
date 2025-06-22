package dev.tanoc.stockin.edititem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffect(
    effect: Flow<EditItemViewModel.Effect>,
    finish: () -> Unit,
    showToast: (String) -> Unit,
) {
    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is EditItemViewModel.Effect.Finish -> {
                    finish()
                }
                is EditItemViewModel.Effect.ShowToast -> {
                    showToast(effect.message)
                }
            }
        }
    }
}
