package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface MainViewModel : UnidirectionalViewModel<MainViewModel.State, MainViewModel.Effect, MainViewModel.Event> {
    data class State(
        val items: List<Item>,
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
    override val state = itemRepository.itemsFlow.map {
        MainViewModel.State(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainViewModel.State(emptyList())
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
        itemRepository.loadMore("debug")
    }
}
