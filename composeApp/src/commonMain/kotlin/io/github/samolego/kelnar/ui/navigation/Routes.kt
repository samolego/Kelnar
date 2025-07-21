package io.github.samolego.kelnar.ui.navigation

sealed class Routes(val route: String) {
    object Orders : Routes("orders")
    object NewOrder : Routes("new_order")
    object EditOrder : Routes("edit_order/{orderId}") {
        fun createRoute(orderId: String) = "edit_order/$orderId"
    }
    object Products : Routes("products")
    object AddProduct : Routes("add_product")
    object EditProduct : Routes("edit_product/{productId}") {
        fun createRoute(productId: String) = "edit_product/$productId"
    }
}
