package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

interface MainViewModel : UnidirectionalViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event> {
    data class State(
        val items: List<Item>,
        val isLoading: Boolean,
    )

    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }

    sealed class Event {
        object Reload : Event()
        object LoadMore : Event()
        class Delete(val id: String) : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealMainViewModel(
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
                is MainViewModel.Event.Reload -> {
                    reload()
                }
                is MainViewModel.Event.LoadMore -> {
                    loadMore()
                }
                is MainViewModel.Event.Delete -> {
                    delete(event.id)
                }
            }
        }
    }

    private suspend fun reload() {
        try {
            _isLoading.value = true
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                itemRepository.reload(pref.token)
            } else {
                _effect.emit(MainViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin MainVM: ", e.stackTraceToString())
            _effect.emit(MainViewModel.Effect.ShowToast("Failed to reload items"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun loadMore() {
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
            _effect.emit(MainViewModel.Effect.ShowToast("Failed to load items"))
        } finally {
            _isLoading.value = false
        }
    }

    private suspend fun delete(id: String) {
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

class MainViewModelFactory(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            RealMainViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return RealMainViewModel(
                    itemRepository,
                    prefRepository,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
