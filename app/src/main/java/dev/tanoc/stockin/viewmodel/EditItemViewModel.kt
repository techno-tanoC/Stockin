package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.ThumbnailRepository
import dev.tanoc.stockin.data.TitleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
        object QueryUrl : Event()
        object QueryThumbnail : Event()
        object Submit : Event()
    }

    override val state: StateFlow<State>
    override val effect: Flow<Effect>
    override fun event(event: Event)
}

class RealEditItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
    initId: String,
    initTitle: String,
    initUrl: String,
    initThumbnail: String,
) : ViewModel(), EditItemViewModel {
    private val _state = MutableStateFlow(
        EditItemViewModel.State(
            id = initId,
            title = initTitle,
            url = initUrl,
            thumbnail = initThumbnail,
        )
    )
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditItemViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

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
                is EditItemViewModel.Event.QueryUrl -> {
                    queryTitle(_state.value.url)
                }
                is EditItemViewModel.Event.QueryThumbnail -> {
                    queryThumbnail(_state.value.url)
                }
                is EditItemViewModel.Event.Submit -> {
                    val (id, title, url, thumbnail) = _state.value
                    submit(id, title, url, thumbnail)
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
                _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to query the title"))
        }
    }

    private suspend fun queryThumbnail(url: String) {
        try {
            val pref = prefRepository.prefFlow.first()
            if (pref != null) {
                val response = thumbnailRepository.query(pref.token, url)
                _state.value = _state.value.copy(thumbnail = response.url)
            } else {
                _effect.emit(EditItemViewModel.Effect.ShowToast("Empty token"))
            }
        } catch (e: Exception) {
            Log.e("Stockin EditItemVM", e.stackTraceToString())
            _effect.emit(EditItemViewModel.Effect.ShowToast("Failed to query the thumbnail"))
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

class EditItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
    private val initId: String,
    private val initTitle: String,
    private val initUrl: String,
    private val initThumbnail: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            RealEditItemViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return RealEditItemViewModel(
                    itemRepository,
                    titleRepository,
                    thumbnailRepository,
                    prefRepository,
                    initId,
                    initTitle,
                    initUrl,
                    initThumbnail,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
