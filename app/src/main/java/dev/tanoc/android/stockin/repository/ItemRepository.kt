package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Item
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class Resp<T>(
    val data: T,
    val message: String
)

interface IItemRepository {
    suspend fun index(): List<Item>
    suspend fun create(title: String, url: String)
}

class ItemRepository(val baseUrl: String) : IItemRepository {
    data class NewItem(
        val title: String,
        val url: String,
    )

    interface Api {
        @GET("/items")
        suspend fun index(): Response<Resp<List<Item>>>

        @POST("/items")
        suspend fun create(@Body newItem: NewItem)
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
        return service.index().body()?.data ?: listOf()
    }

    override suspend fun create(title: String, url: String) {
        service.create(NewItem(title, url))
    }
}
