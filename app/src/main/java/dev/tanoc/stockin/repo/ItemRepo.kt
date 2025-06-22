package dev.tanoc.stockin.repo

import javax.inject.Inject

class ItemRepo @Inject constructor(
    private val localDataSource: ItemLocalDataSource,
) {
    val itemsFlow = localDataSource.itemsFlow

    suspend fun reload() {
    }

    suspend fun loadMore() {
    }

    suspend fun create() {
    }

    suspend fun update() {
    }

    suspend fun delete() {
    }
}
