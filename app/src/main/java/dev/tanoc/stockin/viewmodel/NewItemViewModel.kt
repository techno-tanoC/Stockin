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

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val thumbnailRepository: ThumbnailRepository,
    private val prefRepository: PrefRepository,
    initTitle: String,
    initUrl: String,
    initThumbnail: String,
) : ViewModel() {
    private val _title = MutableStateFlow(initTitle)
    val title = _title.asStateFlow()
    private val _url = MutableStateFlow(initUrl)
    val url = _url.asStateFlow()
    private val _thumbnail = MutableStateFlow(initThumbnail)
    val thumbnail = _thumbnail.asStateFlow()

    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun updateTitle(title: String) {
        _title.value = title
    }

    fun updateUrl(url: String) {
        _url.value = url
    }

    fun updateThumbnail(thumbnail: String) {
        _thumbnail.value = thumbnail
    }

    fun queryTitle(url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    val response = titleRepository.query(pref.token, url)
                    _title.value = response.title
                } else {
                    _event.emit("Empty token")
                }
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM", e.stackTraceToString())
                _event.emit("Failed to query the title")
            }
        }
    }

    fun queryThumbnail(url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    val response = thumbnailRepository.query(pref.token, url)
                    _thumbnail.value = response.url
                } else {
                    _event.emit("Empty token")
                }
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM", e.stackTraceToString())
                _event.emit("Failed to query the thumbnail")
            }
        }
    }

    fun submit(title: String, url: String, thumbnail: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.create(pref.token, title, url, thumbnail)
                    _event.emit("Created the item")
                } else {
                    _event.emit("Empty token")
                }
                _isFinish.value = true
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM: ", e.stackTraceToString())
                _event.emit("Failed to create the item")
            }
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
            NewItemViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(
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
