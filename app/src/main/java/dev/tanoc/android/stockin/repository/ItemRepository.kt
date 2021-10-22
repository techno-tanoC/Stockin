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
    suspend fun index(before: Long): List<Item>?
    suspend fun create(title: String, url: String): Item?
    suspend fun update(id: Long, title: String, url: String): Item?
    suspend fun delete(id: Long)
}

class ItemRepository(private val baseUrl: String, private val token: String) : IItemRepository {
    private val service by lazy {
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    data class Params(
        val title: String,
        val url: String,
    )

    interface Api {
        @GET("/items")
        suspend fun index(@Header("Authorization") token: String, @Query("before") before: Long): Response<Data<List<Item>>>

        @POST("/items")
        suspend fun create(@Header("Authorization") token: String, @Body params: Params): Response<Data<Item>>

        @PUT("/items/{id}")
        suspend fun update(@Header("Authorization") token: String, @Path("id") id: Long, @Body params: Params): Response<Data<Item>>

        @DELETE("/items/{id}")
        suspend fun delete(@Header("Authorization") token: String, @Path("id") id: Long)
    }

    override suspend fun index(before: Long): List<Item>? {
        return service.index(this.token, before).body()?.data
    }

    override suspend fun create(title: String, url: String): Item? {
        return service.create(this.token, Params(title, url)).body()?.data
    }

    override suspend fun update(id: Long, title: String, url: String): Item? {
        return service.update(this.token, id, Params(title, url)).body()?.data
    }

    override suspend fun delete(id: Long) {
        service.delete(this.token, id)
    }
}
