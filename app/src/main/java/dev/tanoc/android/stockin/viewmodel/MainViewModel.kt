package dev.tanoc.android.stockin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/")

    private val _items = MutableLiveData<List<Item>>(listOf())
    val items = _items as LiveData<List<Item>>

    fun load() {
        viewModelScope.launch {
            reload()
        }
    }

    fun create(title: String, url: String) {
        viewModelScope.launch {
            itemRepository.create(title, url)
            reload()
        }
    }

    private suspend fun reload() {
        val res = itemRepository.index()
        _items.postValue(res)
    }
}
