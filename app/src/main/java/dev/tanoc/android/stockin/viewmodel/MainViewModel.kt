package dev.tanoc.android.stockin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")

    private val _items = MutableLiveData<List<Item>>(listOf())
    val items = _items as LiveData<List<Item>>

    fun load() {
        viewModelScope.launch {
            reload()
        }
    }

    fun prepend(id: Int, title: String, url: String) {
        val item = Item(id, title, url)
        val list = _items.value!!.toMutableList()
        list.add(0, item)
        _items.value = list.toList()
    }

    fun patch(id: Int, title: String, url: String) {
        val list = _items.value!!.toMutableList()
        list.forEachIndexed { index, item ->
            if (item.id == id) {
                list[index] = Item(id, title, url)
            }
        }
        _items.value = list.toList()
    }

    fun remove(id: Int) {
        viewModelScope.launch {
            itemRepository.delete(id)

            val list = _items.value!!.toMutableList()
            list.removeIf { it.id == id }
            _items.value = list.toList()
        }
    }

    private suspend fun reload() {
        val res = itemRepository.index()
        if (res != null) {
            _items.postValue(res)
        }
    }
}
