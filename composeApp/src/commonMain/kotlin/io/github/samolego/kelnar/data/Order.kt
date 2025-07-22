package io.github.samolego.kelnar.data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Order(
        val id: String,
        val tableNumber: String,
        val items: List<OrderItem> = emptyList(),
        val createdAt: LocalDateTime,
        val isCompleted: Boolean = false
) {
    val total: Double
        get() = items.sumOf { it.subtotal }
}
