package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.QueryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface EditItemViewModel : UnidirectionalViewModel<EditItemViewModel.State, EditItemViewModel.Effect, EditItemViewModel.Event> {
    data class State(
        val id: String,
        val title: String,
        val url: String,
        val thumbnail: String,
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

class RealEditItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val queryRepository: QueryRepository,
    private val prefRepository: PrefRepository,
) : ViewModel(), EditItemViewModel {
    private val _state = MutableStateFlow(
        EditItemViewModel.State(
            id = "",
            title = "",
            url = "",
            thumbnail = "",
        )
    )
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditItemViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

    fun init(id: String, title: String, url: String, thumbnail: String) {
        _state.value = _state.value.copy(
            id = id,
            title = title,
            url = url,
            thumbnail = thumbnail,
        )
    }

    override fun event(event: EditItemViewModel.Event) {
        viewModelScope.launch {
            when (event) {
                is EditItemViewModel.Event.ChangeTitle -> {
                    _state.value = _state.value.copy(title = event.title)
                }
                is EditItemViewModel.Event.ChangeUrl -> {
                    _state.value = _state.value.copy(url = event.url)
                }
                is EditItemViewModel.Event.ChangeThumbnail -> {
                    _state.value = _state.value.copy(thumbnail = event.thumbnail)
                }
                is EditItemViewModel.Event.QueryInfo -> {
                    queryInfo(_state.value.url)
                }
                is EditItemViewModel.Event.Submit -> {
                    val (id, title, url, thumbnail) = _state.value
                    submit(id, title, url, thumbnail)
                }
            }
        }
    }

    private suspend fun queryInfo(url: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                val response = queryRepository.info(pref.token, url)
                _state.value = _state.value.copy(title = response.title, thumbnail = response.thumbnail)
            } else {
                _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to query the info"))
        }
    }

    private suspend fun submit(id: String, title: String, url: String, thumbnail: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                itemRepository.update(pref.token, id, title, url, thumbnail)
                _effect.emit(EditItemViewModel.Effect.ShowToast("Updated the item"))
            } else {
                _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
            }
            _effect.emit(EditItemViewModel.Effect.Finish)
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to edit the item"))
        }
    }
}
