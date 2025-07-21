package io.github.samolego.kelnar.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String = ""
)
