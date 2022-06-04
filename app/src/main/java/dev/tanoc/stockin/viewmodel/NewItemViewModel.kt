package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.TitleRepository
import dev.tanoc.stockin.model.Title
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun query(url: String, callback: (Title) -> Unit) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    val title = titleRepository.query(pref.token, url)
                    callback(title)
                } else {
                    _event.emit("Empty token")
                }
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM", e.stackTraceToString())
                _event.emit("Failed to query the title")
            }
        }
    }

    fun create(title: String, url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.create(pref.token, title, url)
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
    private val prefRepository: PrefRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            NewItemViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(
                    itemRepository,
                    titleRepository,
                    prefRepository,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
