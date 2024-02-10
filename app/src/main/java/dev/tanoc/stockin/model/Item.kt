package dev.tanoc.stockin.model

data class Item(
    val id: String,
    val title: String,
    val url: String,
    val thumbnail: String,
    val archived: Boolean,
    val liked: Boolean,
)
