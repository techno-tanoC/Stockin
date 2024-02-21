package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Info
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject

@Serializable
data class InfoParams(
    val url: String,
)

interface QueryService {
    @POST("/query/info")
    suspend fun info(@Header("Authorization") token: String, @Body params: InfoParams): Response<Data<Info>>
}

class QueryDataSource @Inject constructor(
    private val queryService: QueryService,
) {
    suspend fun info(token: String, url: String): Info {
        val bearer = "Bearer $token"
        val params = InfoParams(url)
        return queryService.info(bearer, params).body()?.data!!
    }
}
