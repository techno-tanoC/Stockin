package dev.tanoc.android.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Event
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.ItemRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")

    private val _items = MutableLiveData<List<Item>>(listOf())
    val items = _items as LiveData<List<Item>>

    private val _message = MutableLiveData<Event<String>>()
    val message = _message as LiveData<Event<String>>

    fun load() {
        viewModelScope.launch {
            reload()
        }
    }

    fun prepend(id: Long, title: String, url: String) {
        val item = Item(id, title, url)
        val list = _items.value!!.toMutableList()
        list.add(0, item)
        _items.value = list.toList()
    }

    fun patch(id: Long, title: String, url: String) {
        val list = _items.value!!.toMutableList()
        list.forEachIndexed { index, item ->
            if (item.id == id) {
                list[index] = Item(id, title, url)
            }
        }
        _items.value = list.toList()
    }

    fun remove(id: Long) {
        viewModelScope.launch {
            try {
                itemRepository.delete(id)

                val list = _items.value!!.toMutableList()
                list.removeIf { it.id == id }
                _items.value = list.toList()
            } catch (e: Exception) {
                Log.e("Stockin", "MainViewModel remove: $e")
                _message.value = Event("Failed to delete data")
            }
        }
    }

    private suspend fun reload() {
        try {
            val res = itemRepository.index()
            if (res != null) {
                    _items.postValue(res)
            }
        } catch (e: Exception) {
            Log.e("Stockin", "MainViewModel reload: $e")
            _message.value = Event("Failed to fetch data")
        }
    }
}
