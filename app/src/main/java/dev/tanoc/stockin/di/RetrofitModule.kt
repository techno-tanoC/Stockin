package dev.tanoc.stockin.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.tanoc.stockin.BuildConfig
import dev.tanoc.stockin.data.ItemService
import dev.tanoc.stockin.data.QueryService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.create

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {
    private const val baseUrl = BuildConfig.ENDPOINT
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private val tokenInterceptor = TokenInterceptor()
    private val client = OkHttpClient
        .Builder()
        .addInterceptor(tokenInterceptor)
        .build()
    private val retrofit = Retrofit
        .Builder()
        .client(client)
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

    fun setTokenFlow(flow: Flow<String>) {
        tokenInterceptor.setTokenFlow(flow)
    }
}

class TokenInterceptor : Interceptor {
    private lateinit var tokenFlow: Flow<String>

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenFlow.first()
        }
        val request = chain
                .request()
                .newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        return chain.proceed(request)
    }

    fun setTokenFlow(tokenFlow: Flow<String>) {
        this.tokenFlow = tokenFlow
    }
}
