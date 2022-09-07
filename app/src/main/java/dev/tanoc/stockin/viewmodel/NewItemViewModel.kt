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

interface NewItemViewModel {
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

    val state: StateFlow<State>
    val effect: Flow<Effect>
    fun event(event: Event)
}

class RealNewItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
    initTitle: String,
    initUrl: String,
    initThumbnail: String,
) : ViewModel(), NewItemViewModel {
    private val _state = MutableStateFlow(
        NewItemViewModel.State(
            title = initTitle,
            url = initUrl,
            thumbnail = initThumbnail,
        )
    )
    override val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NewItemViewModel.Effect>()
    override val effect = _effect.asSharedFlow()

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

class NewItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
    private val initTitle: String,
    private val initUrl: String,
    private val initThumbnail: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            RealNewItemViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return RealNewItemViewModel(
                    itemRepository,
                    titleRepository,
                    thumbnailRepository,
                    prefRepository,
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
