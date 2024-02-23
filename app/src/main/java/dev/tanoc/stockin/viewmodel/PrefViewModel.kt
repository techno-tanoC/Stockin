package dev.tanoc.stockin.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tanoc.stockin.Pref.clearPref
import dev.tanoc.stockin.Pref.setPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PrefViewModel : UnidirectionalViewModel<PrefViewModel.State, PrefViewModel.Effect, PrefViewModel.Event> {
    object State

    sealed class Event {
        class UpdateToken(val token: String) : Event()
        object ClearToken : Event()
    }

    sealed class Effect {
        object Finish : Effect()
    }

    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealPrefViewModel @Inject constructor(
    @ApplicationContext val context: Context,
) : ViewModel(), PrefViewModel {
    private val _state = MutableStateFlow(PrefViewModel.State)
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PrefViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun event(event: PrefViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is PrefViewModel.Event.UpdateToken -> {
                    context.setPref(event.token)
                    _effect.emit(PrefViewModel.Effect.Finish)
                }
                is PrefViewModel.Event.ClearToken -> {
                    context.clearPref()
                    _effect.emit(PrefViewModel.Effect.Finish)
                }
            }
        }
    }
}
