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
}

class ItemDataSource(
    private val itemService: ItemService,
) {
    suspend fun index(token: String): List<Item> {
        return itemService.index(token, Long.MAX_VALUE).body()?.data ?: emptyList()
    }

    suspend fun create(token: String, title: String, url: String): Item {
        val params = ItemParams(title, url)
        return itemService.create(token, params).body()?.data!!
    }

    suspend fun update(token: String, id: Long, title: String, url: String): Item {
        val params = ItemParams(title, url)
        return itemService.update(token, id, params).body()?.data!!
    }
}
