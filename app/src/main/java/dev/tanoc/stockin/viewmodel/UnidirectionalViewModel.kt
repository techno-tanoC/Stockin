package dev.tanoc.stockin.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class StateEffectDispatch<STATE, EFFECT, EVENT>(
    val state: STATE,
    val effect: Flow<EFFECT>,
    val dispatch: (EVENT) -> Unit,
)

@Composable
inline fun <reified STATE, EFFECT, EVENT> use(
    viewModel: UnidirectionalViewModel<STATE, EFFECT, EVENT>,
) : StateEffectDispatch<STATE, EFFECT, EVENT> {
    val state by viewModel.state.collectAsState()
    val dispatch: (EVENT) -> Unit = { event ->
        viewModel.event(event)
    }
    return StateEffectDispatch(
        state = state,
        effect = viewModel.effect,
        dispatch = dispatch,
    )
}

interface UnidirectionalViewModel<STATE, EFFECT, EVENT> {
    val state: StateFlow<STATE>
    val effect: Flow<EFFECT>
    fun event(event: EVENT)
}
