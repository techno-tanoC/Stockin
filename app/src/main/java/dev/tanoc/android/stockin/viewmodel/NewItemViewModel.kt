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

    fun create(title: String, url: String) {
        viewModelScope.launch {
            val item = itemRepository.create(title, url)
            if (item != null) {
                _item.postValue(item!!)
            }
        }
    }
}
