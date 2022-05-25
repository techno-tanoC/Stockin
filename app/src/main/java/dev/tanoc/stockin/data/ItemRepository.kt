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
}
