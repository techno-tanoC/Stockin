package dev.tanoc.android.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")

    private val _items = MutableLiveData<MutableList<Item>>(mutableListOf())
    val items = _items as LiveData<List<Item>>

    fun load() {
        viewModelScope.launch {
            reload()
        }
    }

    fun prepend(id: Int, title: String, url: String) {
        val item = Item(id, title, url)
        val list = mutableListOf(item)
        list.addAll(_items.value ?: mutableListOf())
        _items.value = list
    }

    private suspend fun reload() {
        val res = itemRepository.index() ?: listOf()
        _items.postValue(res.toMutableList())
    }
}
