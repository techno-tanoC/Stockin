package dev.tanoc.stockin.repo

import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemLocalDataSource @Inject constructor() {
    private val _itemsFlow: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val itemsFlow = _itemsFlow.asStateFlow()

    fun replaceAll(items: List<Item>) {
        _itemsFlow.value = items
    }

    fun concat(items: List<Item>) {
        _itemsFlow.update {
            it + items
        }
    }

    fun prepend(item: Item) {
        _itemsFlow.update {
            listOf(item) + it
        }
    }

    fun patch(item: Item) {
        _itemsFlow.update {
            it.map { element ->
                if (element.id == item.id) {
                    item
                } else {
                    element
                }
            }
        }
    }

    fun remove(id: String) {
        _itemsFlow.update {
            it.filterNot { element ->
                element.id == id
            }
        }
    }
}
