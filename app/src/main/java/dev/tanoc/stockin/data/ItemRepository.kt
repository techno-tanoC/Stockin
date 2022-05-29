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
        val items = itemDataSource.index(token)
        _itemsFlow.update {
            items
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
        _itemsFlow.update { list ->
            list.map {
                if (it.id == id) {
                    item
                } else {
                    it
                }
            }
        }
    }
}
