package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    val items = itemRepository.itemsFlow

    fun load() {
        viewModelScope.launch {
            try {
                prefRepository.prefFlow.first()?.let {
                    itemRepository.reload(it.token)
                }
            } catch (e: Exception) {
                Log.e("Stockin MainViewModel: ", e.stackTraceToString())
            }
        }
    }
}
