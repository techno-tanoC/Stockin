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

class NewItemViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")

    private val _item = MutableLiveData<Event<Item>>()
    val item = _item as LiveData<Event<Item>>

    private val _message = MutableLiveData<Event<String>>()
    val message = _message as LiveData<Event<String>>

    fun submit(title: String, url: String) {
        viewModelScope.launch {
            try {
                itemRepository.create(title, url)?.let {
                    _item.value = Event(it)
                }
            } catch (err: Exception) {
                Log.e("Stockin", "NewItemViewModel submit: $err")
                _message.value = Event("Failed to create data")
            }
        }
    }
}
