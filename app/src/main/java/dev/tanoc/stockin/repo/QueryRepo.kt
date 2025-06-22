package dev.tanoc.stockin.repo

import dev.tanoc.stockin.model.Info
import javax.inject.Inject

class QueryRepo @Inject constructor(
    private val queryDataSource: QueryDataSource,
) {
    suspend fun info(url: String): Info {
        return queryDataSource.info(url)
    }
}
