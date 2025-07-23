package io.github.samolego.kelnar.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable @SerialName("orders") data class Orders(val tab: Int = 0)

@Serializable @SerialName("products") data class Products(val import: String = "")

@Serializable @SerialName("new-order") data object NewOrder

@Serializable @SerialName("add-product") data object AddProduct

@Serializable @SerialName("menu") data object Menu

@Serializable @SerialName("order-details") data class OrderDetails(val orderId: String)

@Serializable @SerialName("edit-order") data class EditOrder(val orderId: String)

@Serializable @SerialName("edit-product") data class EditProduct(val productId: String)
