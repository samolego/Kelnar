package io.github.samolego.kelnar

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import io.github.samolego.kelnar.ui.navigation.EditOrder
import io.github.samolego.kelnar.ui.navigation.NewOrder
import io.github.samolego.kelnar.ui.navigation.OrderDetails
import io.github.samolego.kelnar.ui.navigation.Orders
import io.github.samolego.kelnar.ui.navigation.Products
import io.github.samolego.kelnar.ui.navigation.ProductsImport
import io.github.samolego.kelnar.ui.navigation.ProductsShare
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi

external fun decodeURIComponent(encodedURI: String): String

external fun encodeURIComponent(str: String): String

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
                    // The new bindToBrowserNavigation API uses @SerialName values directly
                    val initRoute = window.location.pathname.trimStart('/')
                    val queryParams = window.location.search

                    when {
                        initRoute == "orders" -> navController.navigate(Orders())
                        initRoute.startsWith("orders/") -> {
                            val tab = initRoute.substringAfter("orders/").toIntOrNull() ?: 0
                            navController.navigate(Orders(tab))
                        }
                        initRoute == "products" -> {
                            navController.navigate(Products)
                        }
                        initRoute == "products/import" -> {
                            // Check for data parameter
                            val importData =
                                    if (queryParams.contains("data=")) {
                                        queryParams
                                                .substringAfter("data=")
                                                .substringBefore("&")
                                                .let { decodeURIComponent(it) }
                                    } else ""
                            navController.navigate(ProductsImport(importData))
                        }
                        initRoute == "new-order" -> navController.navigate(NewOrder)
                        initRoute.startsWith("order-details/") -> {
                            val orderId = initRoute.substringAfter("order-details/")
                            if (orderId.isNotEmpty()) {
                                navController.navigate(OrderDetails(orderId))
                            }
                        }
                        initRoute.startsWith("edit-order/") -> {
                            val orderId = initRoute.substringAfter("edit-order/")
                            if (orderId.isNotEmpty()) {
                                navController.navigate(EditOrder(orderId))
                            }
                        }
                    }

                    navController.bindToBrowserNavigation()
                }
        )
    }
}
