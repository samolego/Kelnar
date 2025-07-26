package io.github.samolego.kelnar.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class OrderTab {
    ACTIVE,
    COMPLETED
}

@Serializable @SerialName("orders") data object Orders

@Serializable @SerialName("orders/active") data object OrdersActive

@Serializable @SerialName("orders/completed") data object OrdersCompleted

@Serializable @SerialName("menu") data object Menu

@Serializable @SerialName("menu/import") data class ProductsImport(val data: String = "")

@Serializable @SerialName("menu/share") data object ProductsShare

@Serializable @SerialName("orders/new") data object NewOrder

@Serializable @SerialName("orders/details") data class OrderDetails(val orderId: String)

@Serializable @SerialName("orders/edit") data class EditOrder(val orderId: String)
