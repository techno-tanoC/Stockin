package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()

    fun create(title: String, url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.create(pref.token, title, url)
                }
                _isFinish.value = true
            } catch (e: Exception) {
            }
        }
    }
}