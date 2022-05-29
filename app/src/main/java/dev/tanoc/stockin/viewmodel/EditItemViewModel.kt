package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.TitleRepository
import dev.tanoc.stockin.model.Title
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditItemViewModel(
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
                Log.e("Stockin EditItemVM", e.stackTraceToString())
                _event.emit("Failed to query the title")
            }
        }
    }

    fun update(id: Long, title: String, url: String) {
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
