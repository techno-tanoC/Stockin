package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Item
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class Resp<T>(
    val data: T,
    val message: String
)

interface IItemRepository {
    suspend fun index(): List<Item>
    suspend fun create(title: String, url: String)
}

class ItemRepository(val baseUrl: String, val token: String) : IItemRepository {
    data class NewItem(
        val title: String,
        val url: String,
    )

    interface Api {
        @GET("/items")
        suspend fun index(@Header("Authorization") token: String): Response<Resp<List<Item>>>

        @POST("/items")
        suspend fun create(@Header("Authorization") token: String, @Body newItem: NewItem)
    }

    private val service by lazy {
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    override suspend fun index(): List<Item> {
        return service.index(this.token).body()?.data ?: listOf()
    }

    override suspend fun create(title: String, url: String) {
        service.create(this.token, NewItem(title, url))
    }
}
