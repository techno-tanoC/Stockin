package dev.tanoc.stockin.model

import kotlinx.serialization.Serializable

@Serializable
data class Info(
    val title: String,
    val thumbnail: String,
)
