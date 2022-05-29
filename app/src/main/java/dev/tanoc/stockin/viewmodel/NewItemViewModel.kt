package dev.tanoc.stockin.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.PrefRepository
import dev.tanoc.stockin.data.TitleRepository
import dev.tanoc.stockin.model.Title
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewItemViewModel(
    private val itemRepository: ItemRepository,
    private val titleRepository: TitleRepository,
    private val prefRepository: PrefRepository,
) : ViewModel() {
    private val _isFinish = MutableStateFlow(false)
    val isFinish = _isFinish.asStateFlow()

    fun query(url: String, callback: (Title) -> Unit) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    val title = titleRepository.query(pref.token, url)
                    callback(title)
                }
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM", e.stackTraceToString())
            }
        }
    }

    fun create(title: String, url: String) {
        viewModelScope.launch {
            try {
                val pref = prefRepository.prefFlow.first()
                if (pref != null) {
                    itemRepository.create(pref.token, title, url)
                }
                _isFinish.value = true
            } catch (e: Exception) {
                Log.e("Stockin NewItemVM: ", e.stackTraceToString())
            }
        }
    }
}
