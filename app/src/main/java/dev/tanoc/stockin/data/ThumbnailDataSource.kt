package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Thumbnail
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

data class ThumbnailParams(
    val url: String,
)

interface ThumbnailService {
    @POST("/thumbnail/query")
    suspend fun query(@Header("Authorization") token: String, @Body params: ThumbnailParams): Response<Data<Thumbnail>>
}

class ThumbnailDataSource @Inject constructor(
    private val thumbnailService: ThumbnailService,
) {
    suspend fun query(token: String, url: String): Thumbnail {
        val bearer = "Bearer $token"
        val params = ThumbnailParams(url)
        return thumbnailService.query(bearer, params).body()?.data!!
    }
}
