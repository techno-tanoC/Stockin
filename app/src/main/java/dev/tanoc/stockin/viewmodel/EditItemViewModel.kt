package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.TitleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val prefRepository: PrefRepository,
    initTitle: String,
    initUrl: String,
) : ViewModel() {
    private val _title = MutableStateFlow(initTitle)
    val title = _title.asStateFlow()
    private val _url = MutableStateFlow(initUrl)
    val url = _url.asStateFlow()

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

    fun query(url: String) {
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
                Log.e("Stockin EditItemVM", e.stackTraceToString())
                _event.emit("Failed to query the title")
            }
        }
    }

    fun submit(id: Long, title: String, url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.update(pref.token, id, title, url)
                    _event.emit("Updated the item")
                } else {
                    _event.emit("Empty token")
                }
                _isFinish.value = true
            } catch (e: Exception) {
                Log.e("Stockin EditItemVM", e.stackTraceToString())
                _event.emit("Failed to edit the item")
            }
        }
    }
}

class EditItemViewModelFactory(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val prefRepository: PrefRepository,
    private val initTitle: String,
    private val initUrl: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            EditItemViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return EditItemViewModel(
                    itemRepository,
                    titleRepository,
                    prefRepository,
                    initTitle,
                    initUrl,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
