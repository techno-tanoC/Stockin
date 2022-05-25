package dev.tanoc.stockin

import dev.tanoc.stockin.data.ItemDataSource
import dev.tanoc.stockin.data.ItemRepository
import dev.tanoc.stockin.data.ItemService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class AppContainer {
    private val baseUrl = "http://10.0.2.2:3000"
    private val itemService = Retrofit
        .Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(ItemService::class.java)
    private val itemDataSource = ItemDataSource(itemService)
    val itemRepository = ItemRepository(itemDataSource)
}
