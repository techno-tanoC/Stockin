package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainViewModel : UnidirectionalViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event> {
    data class State(
        val items: List<Item>,
        val isLoading: Boolean,
    )

    sealed class Effect {
    }

    sealed class Event {
        object LoadMore : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealMainViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
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
            }
        }
    }

    private suspend fun loadMore() {
        if (_isLoading.value) {
            return
        }

        try {
            _isLoading.value = true
            itemRepository.loadMore("debug")
        } finally {
            _isLoading.value = false
        }
    }
}
