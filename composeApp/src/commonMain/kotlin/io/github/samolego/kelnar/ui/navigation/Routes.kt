package io.github.samolego.kelnar.ui.navigation

sealed class Routes(val route: String) {
    object Orders : Routes("orders")
    object NewOrder : Routes("new-order")
    object EditOrder : Routes("edit-order/{orderId}") {
        fun createRoute(orderId: String) = "edit-order/$orderId"
    }
    object Products : Routes("products")
    object AddProduct : Routes("add-product")
    object EditProduct : Routes("edit-product/{productId}") {
        fun createRoute(productId: String) = "edit-product/$productId"
    }
    object Menu : Routes("menu")
}
