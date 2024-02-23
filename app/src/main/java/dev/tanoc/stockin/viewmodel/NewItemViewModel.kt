package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.QueryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NewItemViewModel : UnidirectionalViewModel<NewItemViewModel.State, NewItemViewModel.Effect, NewItemViewModel.Event> {
    data class State(
        val title: String,
        val url: String,
        val thumbnail: String,
        val isLoading: Boolean,
    )

    sealed class Effect {
        object Finish : Effect()
        data class ShowToast(val message: String) : Effect()
    }

    sealed class Event {
        class ChangeTitle(val title: String) : Event()
        class ChangeUrl(val url: String) : Event()
        class ChangeThumbnail(val thumbnail: String) : Event()
        object QueryInfo : Event()
        object Submit : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealNewItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val queryRepository: QueryRepository,
) : ViewModel(), NewItemViewModel {
    private val _state = MutableStateFlow(
        NewItemViewModel.State(
            title = "",
            url = "",
            thumbnail = "",
            isLoading = false,
        )
    )
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewItemViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    fun init(url: String) {
        _state.value = _state.value.copy(url = url)
    }

    override fun event(event: NewItemViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is NewItemViewModel.Event.ChangeTitle -> {
                    _state.value = _state.value.copy(title = event.title)
                }
                is NewItemViewModel.Event.ChangeUrl -> {
                    _state.value = _state.value.copy(url = event.url)
                }
                is NewItemViewModel.Event.ChangeThumbnail -> {
                    _state.value = _state.value.copy(thumbnail = event.thumbnail)
                }
                is NewItemViewModel.Event.QueryInfo -> {
                    queryInfo(_state.value.url)
                }
                is NewItemViewModel.Event.Submit -> {
                    val (title, url, thumbnail) = _state.value
                    submit(title, url, thumbnail)
                }
            }
        }
    }

    private suspend fun queryInfo(url: String) {
        if (_state.value.isLoading) {
            return
        }

        try {
            _state.value = _state.value.copy(isLoading = true)
            val response = queryRepository.info(url)
            _state.value = _state.value.copy(title = response.title, thumbnail = response.thumbnail)
        } catch (e: Exception) {
            Log.e("Stockin NewItemVM", e.stackTraceToString())
            _effect.emit(NewItemViewModel.Effect.ShowToast("Failed to query the info"))
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun submit(title: String, url: String, thumbnail: String) {
        if (_state.value.isLoading) {
            return
        }

        try {
            _state.value = _state.value.copy(isLoading = true)
            itemRepository.create(title, url, thumbnail)
            _effect.emit(NewItemViewModel.Effect.ShowToast("Created the item"))
            _effect.emit(NewItemViewModel.Effect.Finish)
        } catch (e: Exception) {
            Log.e("Stockin NewItemVM: ", e.stackTraceToString())
            _effect.emit(NewItemViewModel.Effect.ShowToast("Failed to create the item"))
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
