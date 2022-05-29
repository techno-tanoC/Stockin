package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ItemRepository(
    private val itemDataSource: ItemDataSource,
) {
    private val _itemsFlow: MutableStateFlow<List<Item>> = MutableStateFlow(emptyList())
    val itemsFlow = _itemsFlow.asStateFlow()

    suspend fun reload(token: String) {
        val items = itemDataSource.index(token, Long.MAX_VALUE)
        _itemsFlow.update {
            items
        }
    }

    suspend fun loadMore(token: String) {
        val lastId = itemsFlow.value.lastOrNull()?.id ?: Long.MAX_VALUE
        val items = itemDataSource.index(token, lastId)
        _itemsFlow.update {
            val list = it.toMutableList()
            list.addAll(items)
            list.toList()
        }
    }

    suspend fun create(token: String, title: String, url: String) {
        val item = itemDataSource.create(token, title, url)
        _itemsFlow.update {
            val list = it.toMutableList()
            list.add(0, item)
            list.toList()
        }
    }

    suspend fun update(token: String, id: Long, title: String, url: String) {
        val item = itemDataSource.update(token, id, title, url)
        _itemsFlow.update {
            it.map { element ->
                if (element.id == id) {
                    item
                } else {
                    element
                }
            }
        }
    }

    suspend fun delete(token: String, id: Long) {
        itemDataSource.delete(token, id)
        _itemsFlow.update {
            val list = it.toMutableList()
            list.removeAll { element ->
                element.id == id
            }
            list.toList()
        }
    }
}
