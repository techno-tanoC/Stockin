package dev.tanoc.stockin.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.UnidirectionalViewModel
import dev.tanoc.stockin.model.Item
import dev.tanoc.stockin.repo.ItemRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
        object Reload : Event()
        data class Delete(val id: String) : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)

    companion object {
        val initial = MainViewModel.State(
            items = emptyList(),
            isLoading = false,
        )
    }
}

class RealMainViewModel @Inject constructor(
    private val itemRepo: ItemRepo,
) : ViewModel(), MainViewModel {
    private val _isLoading = MutableStateFlow(false)

    override val state = combine(
        itemRepo.itemsFlow,
        _isLoading,
    ) { items, isLoading ->
        MainViewModel.State(
            items = items,
            isLoading = isLoading,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MainViewModel.initial,
    )

    private val _effect = MutableSharedFlow<MainViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    override fun event(event: MainViewModel.Event) {
    }
}
