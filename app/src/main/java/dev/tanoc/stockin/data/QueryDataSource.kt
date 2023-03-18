package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Thumbnail
import dev.tanoc.stockin.model.Title
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

data class TitleParams(
    val url: String,
)

data class ThumbnailParams(
    val url: String,
)

interface QueryService {
    @POST("/query/title")
    suspend fun title(@Header("Authorization") token: String, @Body params: TitleParams): Response<Data<Title>>
    @POST("/query/thumbnail")
    suspend fun thumbnail(@Header("Authorization") token: String, @Body params: ThumbnailParams): Response<Data<Thumbnail>>
}

class QueryDataSource @Inject constructor(
    private val queryService: QueryService,
) {
    suspend fun title(token: String, url: String): Title {
        val bearer = "Bearer $token"
        val params = TitleParams(url)
        return queryService.title(bearer, params).body()?.data!!
    }

    suspend fun thumbnail(token: String, url: String): Thumbnail {
        val bearer = "Bearer $token"
        val params = ThumbnailParams(url)
        return queryService.thumbnail(bearer, params).body()?.data!!
    }
}
