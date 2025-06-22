package dev.tanoc.stockin.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun HandleEffect(
    effect: Flow<MainViewModel.Effect>,
    showToast: (String) -> Unit,
) {
    LaunchedEffect(effect) {
        effect.collect { effect ->
            when (effect) {
                is MainViewModel.Effect.ShowToast -> {
                    showToast(effect.message)
                }
            }
        }
    }
}
