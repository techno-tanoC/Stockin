package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Data
import dev.tanoc.android.stockin.model.Title
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ITitleRepository {
    suspend fun query(url: String): Title?
}

class TitleRepository(private val baseUrl: String, private val token: String) : ITitleRepository {
    private val service by lazy {
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    data class Params(
        val url: String,
    )

    interface Api {
        @POST("/title/query")
        suspend fun query(@Header("Authorization") token: String, @Body params: Params): Response<Data<Title>>
    }

    override suspend fun query(url: String): Title? {
        return service.query(this.token, Params(url)).body()?.data
    }
}
