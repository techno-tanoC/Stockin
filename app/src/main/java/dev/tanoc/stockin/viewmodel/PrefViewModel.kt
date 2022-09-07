package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

interface PrefViewModel {
    sealed class Event {
        class UpdateToken(val token: String) : Event()
        object ClearToken : Event()
    }

    sealed class Effect {
        object Finish : Effect()
    }

    val effect: Flow<Effect>
    fun event(event: Event)
}

class RealPrefViewModel(
    private val prefRepository: PrefRepository,
) : ViewModel(), PrefViewModel {
    private val _effect = MutableSharedFlow<PrefViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun event(event: PrefViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is PrefViewModel.Event.UpdateToken -> {
                    prefRepository.setPref(event.token)
                    _effect.emit(PrefViewModel.Effect.Finish)
                }
                is PrefViewModel.Event.ClearToken -> {
                    prefRepository.clearPref()
                    _effect.emit(PrefViewModel.Effect.Finish)
                }
            }
        }
    }
}

class PrefViewModelFactory(
    private val prefRepository: PrefRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            RealPrefViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return RealPrefViewModel(
                    prefRepository,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
