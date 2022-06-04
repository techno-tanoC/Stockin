package dev.tanoc.stockin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

class PrefViewModelFactory(
    private val prefRepository: PrefRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when (modelClass) {
            PrefViewModel::class.java -> {
                @Suppress("UNCHECKED_CAST")
                return PrefViewModel(
                    prefRepository,
                ) as T
            }
            else -> {
                throw IllegalArgumentException("Cannot create an instance of $modelClass")
            }
        }
    }
}
