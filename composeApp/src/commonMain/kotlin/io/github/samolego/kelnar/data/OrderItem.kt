package io.github.samolego.kelnar.data

import kotlinx.serialization.Serializable

@Serializable
data class OrderItem(
    val id: String,
    val product: Product,
    val quantity: Int = 1,
    val customizations: List<String> = emptyList(),
    val subtotal: Double = product.price * quantity
)
