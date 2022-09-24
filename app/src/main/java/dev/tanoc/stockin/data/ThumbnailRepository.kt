package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Thumbnail
import javax.inject.Inject

class ThumbnailRepository @Inject constructor(
    private val thumbnailDataSource: ThumbnailDataSource,
) {
    suspend fun query(token: String, url: String): Thumbnail {
        return thumbnailDataSource.query(token, url)
    }
}
