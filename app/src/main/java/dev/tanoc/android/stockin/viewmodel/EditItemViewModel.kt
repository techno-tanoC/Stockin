package dev.tanoc.android.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Event
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.model.Title
import dev.tanoc.android.stockin.repository.ItemRepository
import dev.tanoc.android.stockin.repository.TitleRepository
import kotlinx.coroutines.launch

class EditItemViewModel: ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")
    private val titleRepository = TitleRepository("http://10.0.2.2:3000/", "debug")

    private val _item = MutableLiveData<Event<Item>>()
    val item = _item as LiveData<Event<Item>>

    private val _title = MutableLiveData<Event<Title>>()
    val title = _title as LiveData<Event<Title>>

    private val _message = MutableLiveData<Event<String>>()
    val message = _message as LiveData<Event<String>>

    fun queryTitle(url: String) {
        viewModelScope.launch {
            try {
                val title = titleRepository.query(url)?.let {
                    _title.value = Event(it)
                }
            } catch (e: Exception) {
                Log.e("Stockin", "EditItemViewModel fetchTitle: $e")
                _message.value = Event("Failed to fetch title")
            }
        }
    }

    fun submit(id: Long, title: String, url: String) {
        viewModelScope.launch {
            try {
                itemRepository.update(id, title, url)?.let {
                    _item.value = Event(it)
                }
            } catch (e: Exception) {
                Log.e("Stockin", "EditItemViewModel update: $e")
                _message.value = Event("Failed to update data")
            }
        }
    }
}
