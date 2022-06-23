package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Item
import retrofit2.Response
import retrofit2.http.*

data class ItemParams(
    val title: String,
    val url: String,
)

interface ItemService {
    @GET("/items")
    suspend fun index(@Header("Authorization") token: String, @Query("before") before: Long): Response<Data<List<Item>>>

    @POST("/items")
    suspend fun create(@Header("Authorization") token: String, @Body params: ItemParams): Response<Data<Item>>

    @PUT("/items/{id}")
    suspend fun update(@Header("Authorization") token: String, @Path("id") id: Long, @Body params: ItemParams): Response<Data<Item>>

    @DELETE("/items/{id}")
    suspend fun delete(@Header("Authorization") token: String, @Path("id") id: Long)
}

class ItemDataSource(
    private val itemService: ItemService,
) {
    suspend fun index(token: String, before: Long): List<Item> {
        val bearer = "Bearer $token"
        return itemService.index(bearer, before).body()?.data!!
    }

    suspend fun create(token: String, title: String, url: String): Item {
        val bearer = "Bearer $token"
        val params = ItemParams(title, url)
        return itemService.create(bearer, params).body()?.data!!
    }

    suspend fun update(token: String, id: Long, title: String, url: String): Item {
        val bearer = "Bearer $token"
        val params = ItemParams(title, url)
        return itemService.update(bearer, id, params).body()?.data!!
    }

    suspend fun delete(token: String, id: Long) {
        val bearer = "Bearer $token"
        return itemService.delete(bearer, id)
    }
}
