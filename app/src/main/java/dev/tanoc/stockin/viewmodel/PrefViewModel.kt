package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.tanoc.stockin.data.PrefRepository
import kotlinx.coroutines.launch

class PrefViewModel(
    private val prefRepository: PrefRepository,
) : ViewModel() {
    fun update(token: String) {
        viewModelScope.launch {
            prefRepository.setPref(token)
        }
    }

    fun clear() {
        viewModelScope.launch {
            prefRepository.clearPref()
        }
    }
}
