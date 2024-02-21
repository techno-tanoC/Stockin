package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Item
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

@Serializable
data class ItemParams(
    val title: String,
    val url: String,
    val thumbnail: String,
)

interface ItemService {
    @GET("/items")
    suspend fun index(@Header("Authorization") token: String, @Query("before") before: String): Response<Data<List<Item>>>

    @POST("/items")
    suspend fun create(@Header("Authorization") token: String, @Body params: ItemParams): Response<Data<Item>>

    @PUT("/items/{id}")
    suspend fun update(@Header("Authorization") token: String, @Path("id") id: String, @Body params: ItemParams): Response<Data<Item>>

    @DELETE("/items/{id}")
    suspend fun delete(@Header("Authorization") token: String, @Path("id") id: String): Response<Unit>
}

class ItemRemoteDataSource @Inject constructor(
    private val itemService: ItemService,
) {
    suspend fun index(token: String, before: String): List<Item> {
        val bearer = "Bearer $token"
        return itemService.index(bearer, before).body()?.data!!
    }

    suspend fun create(token: String, title: String, url: String, thumbnail: String): Item {
        val bearer = "Bearer $token"
        val params = ItemParams(title, url, thumbnail)
        return itemService.create(bearer, params).body()?.data!!
    }

    suspend fun update(token: String, id: String, title: String, url: String, thumbnail: String): Item {
        val bearer = "Bearer $token"
        val params = ItemParams(title, url, thumbnail)
        return itemService.update(bearer, id, params).body()?.data!!
    }

    suspend fun delete(token: String, id: String) {
        val bearer = "Bearer $token"
        itemService.delete(bearer, id).body()
    }
}
