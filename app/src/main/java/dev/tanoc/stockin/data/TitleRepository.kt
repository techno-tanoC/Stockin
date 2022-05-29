package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Title

class TitleRepository(
    private val titleDataSource: TitleDataSource,
) {
    suspend fun query(token: String, url: String): Title {
        return titleDataSource.query(token, url)
    }
}
