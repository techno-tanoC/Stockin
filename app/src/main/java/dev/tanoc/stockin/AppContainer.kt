package dev.tanoc.stockin

import android.content.Context
import dev.tanoc.stockin.data.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppContainer(private val context: Context) {
    private val baseUrl = BuildConfig.ENDPOINT
    private val retrofit = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val itemService = retrofit.create(ItemService::class.java)
    private val itemDataSource = ItemDataSource(itemService)
    val itemRepository = ItemRepository(itemDataSource)

    private val titleService = retrofit.create(TitleService::class.java)
    private val titleDataSource = TitleDataSource(titleService)
    val titleRepository = TitleRepository(titleDataSource)

    val prefRepository = PrefRepository(context)
}
