package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainViewModel : UnidirectionalViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event> {
    data class State(
        val items: List<Item>,
        val isLoading: Boolean,
    )

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }

    sealed class Event {
        object LoadMore : Event()
        data class Delete(val id: String) : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealMainViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModel(), MainViewModel {
    private val _isLoading = MutableStateFlow(false)

    override val state = combine(
        itemRepository.itemsFlow,
        _isLoading,
    ) { items, isLoading ->
        MainViewModel.State(
            items = items,
            isLoading = isLoading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainViewModel.State(
            items = emptyList(),
            isLoading = false,
        )
    )

    private val _effect = MutableSharedFlow<MainViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun event(event: MainViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is MainViewModel.Event.LoadMore -> {
                    loadMore()
                }
                is MainViewModel.Event.Delete -> {
                    delete(event.id)
                }
            }
        }
    }

    private suspend fun loadMore() {
        if (_isLoading.value) {
            return
        }

        try {
            _isLoading.value = true
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                itemRepository.loadMore(pref.token)
            } else {
                _effect.emit(MainViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin MainVM: ", e.stackTraceToString())
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun delete(id: String) {
        if (_isLoading.value) {
            return
        }

        try {
            _isLoading.value = true
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                itemRepository.delete(pref.token, id)
                _effect.emit(MainViewModel.Effect.ShowToast("Deleted the item"))
            } else {
                _effect.emit(MainViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin MainVM: ", e.stackTraceToString())
            _effect.emit(MainViewModel.Effect.ShowToast("Failed to delete the item"))
        } finally {
            _isLoading.value = false
        }
    }
}
