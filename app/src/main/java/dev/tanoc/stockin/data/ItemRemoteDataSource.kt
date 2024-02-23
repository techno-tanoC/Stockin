package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Item
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
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
    suspend fun index(@Query("before") before: String): Response<Data<List<Item>>>

    @POST("/items")
    suspend fun create(@Body params: ItemParams): Response<Data<Item>>

    @PUT("/items/{id}")
    suspend fun update(@Path("id") id: String, @Body params: ItemParams): Response<Data<Item>>

    @DELETE("/items/{id}")
    suspend fun delete(@Path("id") id: String): Response<Unit>
}

class ItemRemoteDataSource @Inject constructor(
    private val itemService: ItemService,
) {
    suspend fun index(before: String): List<Item> {
        return itemService.index(before).body()?.data!!
    }

    suspend fun create(title: String, url: String, thumbnail: String): Item {
        val params = ItemParams(title, url, thumbnail)
        return itemService.create(params).body()?.data!!
    }

    suspend fun update(id: String, title: String, url: String, thumbnail: String): Item {
        val params = ItemParams(title, url, thumbnail)
        return itemService.update(id, params).body()?.data!!
    }

    suspend fun delete(id: String) {
        itemService.delete(id).body()
    }
}
