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

class NewItemViewModel : ViewModel() {
    private lateinit var itemRepository: ItemRepository
    private lateinit var titleRepository: TitleRepository

    private val _isInited = MutableLiveData(false)
    val isInited = _isInited as LiveData<Boolean>

    private val _item = MutableLiveData<Event<Item>>()
    val item = _item as LiveData<Event<Item>>

    private val _title = MutableLiveData<Event<Title>>()
    val title = _title as LiveData<Event<Title>>

    private val _message = MutableLiveData<Event<String>>()
    val message = _message as LiveData<Event<String>>

    fun setRepo(baseUrl: String, token: String) {
        this.itemRepository = ItemRepository(baseUrl, token)
        this.titleRepository = TitleRepository(baseUrl, token)
        _isInited.value = true
    }

    fun queryTitle(url: String) {
        isInited.value?.let {
            viewModelScope.launch {
                try {
                    titleRepository.query(url)?.let {
                        _title.value = Event(it)
                    }
                } catch (e: Exception) {
                    Log.e("Stockin", "NewItemViewModel fetchTitle: $e")
                    _message.value = Event("Failed to fetch title")
                }
            }
        }
    }

    fun submit(title: String, url: String) {
        isInited.value?.let {
            viewModelScope.launch {
                try {
                    itemRepository.create(title, url)?.let {
                        _item.value = Event(it)
                    }
                } catch (e: Exception) {
                    Log.e("Stockin", "NewItemViewModel submit: $e")
                    _message.value = Event("Failed to create data")
                }
            }
        }
    }
}
