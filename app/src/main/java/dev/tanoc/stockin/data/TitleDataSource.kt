package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Title
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class TitleParams(
    val url: String,
)

interface TitleService {
    @POST("/title/query")
    suspend fun query(@Header("Authorization") token: String, @Body params: TitleParams): Response<Data<Title>>
}

class TitleDataSource(
    private val titleService: TitleService,
) {
    suspend fun query(token: String, url: String): Title {
        val bearer = "Bearer $token"
        val params = TitleParams(url)
        return titleService.query(bearer, params).body()?.data!!
    }
}
