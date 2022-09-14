package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ItemLocalDataSource {
    private val _itemsFlow: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val itemsFlow = _itemsFlow.asStateFlow()

    fun replaceAll(items: List<Item>) {
        _itemsFlow.value = items
    }

    fun concat(items: List<Item>) {
        _itemsFlow.update {
            val list = it.toMutableList()
            list.addAll(items)
            list.toList()
        }
    }

    fun prepend(item: Item) {
        _itemsFlow.update {
            val list = it.toMutableList()
            list.add(0, item)
            list.toList()
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
            val list = it.toMutableList()
            list.removeAll { element ->
                element.id == id
            }
            list.toList()
        }
    }
}
