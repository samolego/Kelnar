package io.github.samolego.kelnar

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.toRoute
import io.github.samolego.kelnar.ui.navigation.EditOrder
import io.github.samolego.kelnar.ui.navigation.NewOrder
import io.github.samolego.kelnar.ui.navigation.OrderDetails
import io.github.samolego.kelnar.ui.navigation.Orders
import io.github.samolego.kelnar.ui.navigation.Products
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(
        ExperimentalComposeUiApi::class,
        ExperimentalSerializationApi::class,
        ExperimentalBrowserHistoryApi::class
)
fun main() {
    ComposeViewport(document.body!!) {
        App(
                onNavHostReady = { navController ->
                    // Parse initial URL and navigate if needed
                    val initRoute = window.location.hash.substringAfter('#', "").trimStart('/')
                    when {
                        initRoute.startsWith("orders") -> navController.navigate(Orders)
                        initRoute.startsWith("products") -> navController.navigate(Products)
                        initRoute.startsWith("new-order") -> navController.navigate(NewOrder)
                        initRoute.startsWith("order-details") -> {
                            val orderId = initRoute.substringAfter("order-details/")
                            if (orderId.isNotEmpty()) {
                                navController.navigate(OrderDetails(orderId))
                            }
                        }
                        initRoute.startsWith("edit-order") -> {
                            val orderId = initRoute.substringAfter("edit-order/")
                            if (orderId.isNotEmpty()) {
                                navController.navigate(EditOrder(orderId))
                            }
                        }
                    }

                    // Bind navigation to browser history with custom URL transformation
                    window.bindToNavigation(navController) { entry ->
                        val route = entry.destination.route.orEmpty()
                        when {
                            route.startsWith(Orders.serializer().descriptor.serialName) ->
                                    "#/orders"
                            route.startsWith(Products.serializer().descriptor.serialName) ->
                                    "#/products"
                            route.startsWith(NewOrder.serializer().descriptor.serialName) ->
                                    "#/new-order"
                            route.startsWith(OrderDetails.serializer().descriptor.serialName) -> {
                                val args = entry.toRoute<OrderDetails>()
                                "#/order-details/${args.orderId}"
                            }
                            route.startsWith(EditOrder.serializer().descriptor.serialName) -> {
                                val args = entry.toRoute<EditOrder>()
                                "#/edit-order/${args.orderId}"
                            }
                            else -> ""
                        }
                    }
                }
        )
    }
}
