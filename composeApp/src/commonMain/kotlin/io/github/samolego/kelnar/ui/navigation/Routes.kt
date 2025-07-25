package io.github.samolego.kelnar.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable @SerialName("orders") data class Orders(val tab: Int = 0)

@Serializable @SerialName("products") data object Products

@Serializable @SerialName("products/import") data class ProductsImport(val data: String = "")

@Serializable @SerialName("products/share") data object ProductsShare

@Serializable @SerialName("orders/new") data object NewOrder

@Serializable @SerialName("orders") data class OrderDetails(val orderId: String)

@Serializable @SerialName("orders/edit") data class EditOrder(val orderId: String)
