package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.ThumbnailRepository
import dev.tanoc.stockin.data.TitleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

interface NewItemViewModel : UnidirectionalViewModel<NewItemViewModel.State, NewItemViewModel.Effect, NewItemViewModel.Event> {
    data class State(
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
        object QueryUrl : Event()
        object QueryThumbnail : Event()
        object Submit : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealNewItemViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
) : ViewModel(), NewItemViewModel {
    private val _state = MutableStateFlow(
        NewItemViewModel.State(
            title = "",
            url = "",
            thumbnail = "",
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
                is NewItemViewModel.Event.QueryUrl -> {
                    queryTitle(_state.value.url)
                }
                is NewItemViewModel.Event.QueryThumbnail -> {
                    queryThumbnail(_state.value.url)
                }
                is NewItemViewModel.Event.Submit -> {
                    val (title, url, thumbnail) = _state.value
                    submit(title, url, thumbnail)
                }
            }
        }
    }

    private suspend fun queryTitle(url: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                val response = titleRepository.query(pref.token, url)
                _state.value = _state.value.copy(title = response.title)
            } else {
                _effect.emit(NewItemViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin NewItemVM", e.stackTraceToString())
            _effect.emit(NewItemViewModel.Effect.ShowToast("Failed to query the title"))
        }
    }

    private suspend fun queryThumbnail(url: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                val response = thumbnailRepository.query(pref.token, url)
                _state.value = _state.value.copy(thumbnail = response.url)
            } else {
                _effect.emit(NewItemViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin NewItemVM", e.stackTraceToString())
            _effect.emit(NewItemViewModel.Effect.ShowToast("Failed to query the thumbnail"))
        }
    }

    private suspend fun submit(title: String, url: String, thumbnail: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                itemRepository.create(pref.token, title, url, thumbnail)
                _effect.emit(NewItemViewModel.Effect.ShowToast("Created the item"))
            } else {
                _effect.emit(NewItemViewModel.Effect.ShowToast("Empty token"))
            }
            _effect.emit(NewItemViewModel.Effect.Finish)
        } catch (e: Exception) {
            Log.e("Stockin NewItemVM: ", e.stackTraceToString())
            _effect.emit(NewItemViewModel.Effect.ShowToast("Failed to create the item"))
        }
    }
}
