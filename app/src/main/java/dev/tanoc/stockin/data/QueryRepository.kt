package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Info
import javax.inject.Inject

class QueryRepository @Inject constructor(
    private val queryDataSource: QueryDataSource,
) {
    suspend fun info(url: String): Info {
        return queryDataSource.info(url)
    }
}
