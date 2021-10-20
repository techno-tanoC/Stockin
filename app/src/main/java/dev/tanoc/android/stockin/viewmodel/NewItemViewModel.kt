package dev.tanoc.android.stockin.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.android.stockin.model.Item
import dev.tanoc.android.stockin.repository.ItemRepository
import kotlinx.coroutines.launch

class NewItemViewModel : ViewModel() {
    private val itemRepository = ItemRepository("http://10.0.2.2:3000/", "debug")

    private val _item = MutableLiveData<Item>()
    val item = _item as LiveData<Item>

    private val _title = MutableLiveData("")
    val title = _title as LiveData<String>

    private val _url = MutableLiveData("")
    val url = _url as LiveData<String>

    fun create() {
        viewModelScope.launch {
            val item = itemRepository.create(title.value!!, url.value!!)
            if (item != null) {
                _item.postValue(item)
            }
        }
    }

    fun updateTitle(title: String) {
        _title.value = title
        _title.postValue(_title.value)
    }

    fun updateUrl(url: String) {
        _url.value = url
        _url.postValue(_url.value)
    }
}
