package dev.tanoc.stockin.data

import dev.tanoc.stockin.model.Data
import dev.tanoc.stockin.model.Item
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ItemService {
    @GET("/items")
    suspend fun index(@Header("Authorization") token: String, @Query("before") before: Long): Response<Data<List<Item>>>
}

class ItemDataSource(
    private val itemService: ItemService,
) {
    suspend fun index(token: String): List<Item> {
        return itemService.index(token, Long.MAX_VALUE).body()?.data ?: emptyList()
    }
}
