package dev.tanoc.stockin.newitem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffect(
    effect: Flow<NewItemViewModel.Effect>,
    finish: () -> Unit,
    showToast: (String) -> Unit,
) {
    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is NewItemViewModel.Effect.Finish -> {
                    finish()
                }
                is NewItemViewModel.Effect.ShowToast -> {
                    showToast(effect.message)
                }
            }
        }
    }
}
