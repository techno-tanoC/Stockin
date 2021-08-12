package dev.tanoc.android.stockin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.MockItemRepository

class MainViewModel : ViewModel() {
    private val itemRepository = MockItemRepository()

    private val _items = MutableLiveData<List<Item>>(listOf())
    val items = _items as LiveData<List<Item>>

    fun load() {
        _items.postValue(itemRepository.index())
    }

    fun add(item: Item) {
        itemRepository.prepend(item)
        load()
    }
}
