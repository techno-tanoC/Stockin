package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Thumbnail
import dev.tanoc.stockin.model.Title
import javax.inject.Inject

class QueryRepository @Inject constructor(
    private val queryDataSource: QueryDataSource,
) {
    suspend fun title(token: String, url: String): Title {
        return queryDataSource.title(token, url)
    }

    suspend fun thumbnail(token: String, url: String): Thumbnail {
        return queryDataSource.thumbnail(token, url)
    }
}
