package dev.tanoc.stockin

import android.content.Context
import dev.tanoc.stockin.data.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppContainer(context: Context) {
    private val baseUrl = BuildConfig.ENDPOINT
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val itemService = retrofit.create(ItemService::class.java)
    private val localItemDataSource = LocalItemDataSource()
    private val remoteItemDataSource = RemoteItemDataSource(itemService)
    val itemRepository = ItemRepository(localItemDataSource, remoteItemDataSource)

    private val titleService = retrofit.create(TitleService::class.java)
    private val titleDataSource = TitleDataSource(titleService)
    val titleRepository = TitleRepository(titleDataSource)

    private val thumbnailService = retrofit.create(ThumbnailService::class.java)
    private val thumbnailDataSource = ThumbnailDataSource(thumbnailService)
    val thumbnailRepository = ThumbnailRepository(thumbnailDataSource)

    val prefRepository = PrefRepository(context)
}
