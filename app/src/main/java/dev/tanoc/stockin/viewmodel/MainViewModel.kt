package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val itemRepository: ItemRepository,
) : ViewModel() {
    val items = itemRepository.itemsFlow

    fun load() {
        viewModelScope.launch {
            itemRepository.reload("debug")
        }
    }
}
