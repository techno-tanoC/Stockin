package dev.tanoc.stockin.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.tanoc.stockin.BuildConfig
import dev.tanoc.stockin.data.ItemService
import dev.tanoc.stockin.data.QueryService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {
    private const val baseUrl = BuildConfig.ENDPOINT
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()

    @Provides
    fun bindItemService(): ItemService {
        return retrofit.create()
    }

    @Provides
    fun bindQueryService(): QueryService {
        return retrofit.create()
    }
}
