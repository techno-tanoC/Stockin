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
        val items = itemDataSource.index(token, "ffffffff-ffff-ffff-ffff-ffffffffffff")
        _itemsFlow.update {
            items
        }
    }

    suspend fun loadMore(token: String) {
        val lastId = itemsFlow.value.lastOrNull()?.id ?: "ffffffff-ffff-ffff-ffff-ffffffffffff"
        val items = itemDataSource.index(token, lastId)
        _itemsFlow.update {
            val list = it.toMutableList()
            list.addAll(items)
            list.toList()
        }
    }

    suspend fun create(token: String, title: String, url: String, thumbnail: String) {
        val item = itemDataSource.create(token, title, url, thumbnail)
        _itemsFlow.update {
            val list = it.toMutableList()
            list.add(0, item)
            list.toList()
        }
    }

    suspend fun update(token: String, id: String, title: String, url: String, thumbnail: String) {
        val item = itemDataSource.update(token, id, title, url, thumbnail)
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

    suspend fun delete(token: String, id: String) {
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
