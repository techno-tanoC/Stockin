package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    val items = itemRepository.itemsFlow

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _event = MutableSharedFlow<String>()
    val event = _event.asSharedFlow()

    fun reload() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.reload(pref.token)
                } else {
                    _event.emit("Empty token")
                }
            } catch (e: Exception) {
                Log.e("Stockin MainVM: ", e.stackTraceToString())
                _event.emit("Failed to reload items")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.loadMore(pref.token)
                } else {
                    _event.emit("Token is empty")
                }
            } catch (e: Exception) {
                Log.e("Stockin MainVM: ", e.stackTraceToString())
                _event.emit("Failed to load items")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.delete(pref.token, id)
                    _event.emit("Deleted the item")
                } else {
                    _event.emit("Token is empty")
                }
            } catch (e: Exception) {
                Log.e("Stockin MainVM: ", e.stackTraceToString())
                _event.emit("Failed to delete the item")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
