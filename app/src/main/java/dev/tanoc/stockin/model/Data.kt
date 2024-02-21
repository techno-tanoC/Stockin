package dev.tanoc.stockin.model

import kotlinx.serialization.Serializable

@Serializable
data class Data<T>(val data: T)
