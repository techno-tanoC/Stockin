package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    val items = itemRepository.itemsFlow
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun load() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.reload(pref.token)
                }
            } catch (e: Exception) {
                Log.e("Stockin MainViewModel: ", e.stackTraceToString())
            } finally {
                _isLoading.value = false
            }
        }
    }
}
