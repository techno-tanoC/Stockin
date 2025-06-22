package dev.tanoc.stockin.repo

import javax.inject.Inject

class ItemRepo @Inject constructor(
    private val localItemDataSource: ItemLocalDataSource,
    private val remoteItemDataSource: ItemRemoteDataSource,
) {
    val itemsFlow = localItemDataSource.itemsFlow

    suspend fun reload() {
        val items = remoteItemDataSource.index("ffffffff-ffff-ffff-ffff-ffffffffffff")
        localItemDataSource.replaceAll(items)
    }

    suspend fun loadMore() {
        val lastId = itemsFlow.value.lastOrNull()?.id ?: "ffffffff-ffff-ffff-ffff-ffffffffffff"
        val items = remoteItemDataSource.index(lastId)
        localItemDataSource.concat(items)
    }

    suspend fun create(title: String, url: String, thumbnail: String) {
        val item = remoteItemDataSource.create(title, url, thumbnail)
        localItemDataSource.prepend(item)
    }

    suspend fun update(id: String, title: String, url: String, thumbnail: String) {
        val item = remoteItemDataSource.update(id, title, url, thumbnail)
        localItemDataSource.patch(item)
    }

    suspend fun delete(id: String) {
        remoteItemDataSource.delete(id)
        localItemDataSource.remove(id)
    }
}
