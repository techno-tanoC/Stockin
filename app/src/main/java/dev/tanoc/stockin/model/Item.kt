package dev.tanoc.stockin.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: String,
    val title: String,
    val url: String,
    val thumbnail: String,
)
