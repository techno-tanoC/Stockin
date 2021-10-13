package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Item
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

data class Data<T>(
    val data: T,
)

interface IItemRepository {
    suspend fun index(): List<Item>?
    suspend fun create(title: String, url: String): Item?
    suspend fun delete(id: Int)
}

class ItemRepository(val baseUrl: String, val token: String) : IItemRepository {
    data class NewItem(
        val title: String,
        val url: String,
    )

    interface Api {
        @GET("/items")
        suspend fun index(@Header("Authorization") token: String): Response<Data<List<Item>>>

        @POST("/items")
        suspend fun create(@Header("Authorization") token: String, @Body newItem: NewItem): Response<Data<Item>>

        @DELETE("/items/{id}")
        suspend fun delete(@Header("Authorization") token: String, @Path("id") id: Int)
    }

    private val service by lazy {
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    override suspend fun index(): List<Item>? {
        return service.index(this.token).body()?.data
    }

    override suspend fun create(title: String, url: String): Item? {
        return service.create(this.token, NewItem(title, url)).body()?.data
    }

    override suspend fun delete(id: Int) {
        service.delete(this.token, id)
    }
}
