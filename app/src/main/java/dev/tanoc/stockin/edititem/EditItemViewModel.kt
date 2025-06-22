package dev.tanoc.stockin.edititem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.EmptyTokenException
import dev.tanoc.stockin.UnidirectionalViewModel
import dev.tanoc.stockin.repo.ItemRepo
import dev.tanoc.stockin.repo.QueryRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface EditItemViewModel : UnidirectionalViewModel<EditItemViewModel.State, EditItemViewModel.Effect, EditItemViewModel.Event> {
    data class State(
        val id: String,
        val title: String,
        val url: String,
        val thumbnail: String,
        val isLoading: Boolean,
    )

    sealed class Effect {
        data object Finish : Effect()
        data class ShowToast(val message: String) : Effect()
    }

    sealed class Event {
        class ChangeTitle(val title: String) : Event()
        class ChangeUrl(val url: String) : Event()
        class ChangeThumbnail(val thumbnail: String) : Event()
        data object QueryInfo : Event()
        data object Submit : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealEditItemViewModel @Inject constructor(
    private val itemRepo: ItemRepo,
    private val queryRepo: QueryRepo,
) : ViewModel(), EditItemViewModel {
    private val _state = MutableStateFlow(
        EditItemViewModel.State(
            id = "",
            title = "",
            url = "",
            thumbnail = "",
            isLoading = false,
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
        if (_state.value.isLoading) {
            return
        }

        try {
            _state.value = _state.value.copy(isLoading = true)
            val response = queryRepo.info(url)
            _state.value = _state.value.copy(title = response.title, thumbnail = response.thumbnail)
        } catch (_: EmptyTokenException) {
            _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to query the info"))
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun submit(id: String, title: String, url: String, thumbnail: String) {
        if (_state.value.isLoading) {
            return
        }

        try {
            _state.value = _state.value.copy(isLoading = true)
            itemRepo.update(id, title, url, thumbnail)
            _effect.emit(EditItemViewModel.Effect.ShowToast("Updated the item"))
            _effect.emit(EditItemViewModel.Effect.Finish)
        } catch (_: EmptyTokenException) {
            _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to edit the item"))
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }
}
