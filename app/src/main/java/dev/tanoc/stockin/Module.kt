package dev.tanoc.stockin

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.tanoc.stockin.TokenStore.tokenFlow
import dev.tanoc.stockin.repo.ItemService
import dev.tanoc.stockin.repo.QueryService
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
import java.io.IOException

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {
    private const val baseUrl = BuildConfig.ENDPOINT
    private val contentType = "application/json".toMediaType()
    private val json = Json { ignoreUnknownKeys = true }

    private fun buildRetrofit(context: Context): Retrofit {
        val tokenFlow = context.tokenFlow()
        val tokenInterceptor = TokenInterceptor(tokenFlow)
        val client = OkHttpClient
            .Builder()
            .addInterceptor(tokenInterceptor)
            .build()
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    fun bindItemService(@ApplicationContext context: Context): ItemService {
        return buildRetrofit(context).create()
    }

    @Provides
    fun bindQueryService(@ApplicationContext context: Context): QueryService {
        return buildRetrofit(context).create()
    }
}

class TokenInterceptor(
    private val tokenFlow: Flow<String>,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenFlow.first()
        }
        if (token == "") {
            throw EmptyTokenException()
        }
        val request = chain
            .request()
            .newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }
}

class EmptyTokenException : IOException()
