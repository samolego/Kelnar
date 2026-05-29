package io.github.samolego.kelnar.data

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String = ""
)
