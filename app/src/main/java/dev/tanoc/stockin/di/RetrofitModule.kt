package dev.tanoc.stockin.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dev.tanoc.stockin.BuildConfig
import dev.tanoc.stockin.data.ItemService
import dev.tanoc.stockin.data.ThumbnailService
import dev.tanoc.stockin.data.TitleService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

@Module
@InstallIn(ActivityComponent::class)
object RetrofitModule {
    private const val baseUrl = BuildConfig.ENDPOINT
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    @Provides
    fun bindItemService(): ItemService {
        return retrofit.create()
    }

    @Provides
    fun bindTitleService(): TitleService {
        return retrofit.create()
    }

    @Provides
    fun providerThumbnailService(): ThumbnailService {
        return retrofit.create()
    }
}
