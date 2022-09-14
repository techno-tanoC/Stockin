package dev.tanoc.stockin.data

class ItemRepository(
    private val localItemDataSource: ItemLocalDataSource,
    private val remoteItemDataSource: ItemRemoteDataSource,
) {
    val itemsFlow = localItemDataSource.itemsFlow

    suspend fun reload(token: String) {
        val items = remoteItemDataSource.index(token, "ffffffff-ffff-ffff-ffff-ffffffffffff")
        localItemDataSource.replaceAll(items)
    }

    suspend fun loadMore(token: String) {
        val lastId = itemsFlow.value.lastOrNull()?.id ?: "ffffffff-ffff-ffff-ffff-ffffffffffff"
        val items = remoteItemDataSource.index(token, lastId)
        localItemDataSource.concat(items)
    }

    suspend fun create(token: String, title: String, url: String, thumbnail: String) {
        val item = remoteItemDataSource.create(token, title, url, thumbnail)
        localItemDataSource.prepend(item)
    }

    suspend fun update(token: String, id: String, title: String, url: String, thumbnail: String) {
        val item = remoteItemDataSource.update(token, id, title, url, thumbnail)
        localItemDataSource.patch(item)
    }

    suspend fun delete(token: String, id: String) {
        remoteItemDataSource.delete(token, id)
        localItemDataSource.remove(id)
    }
}
