package io.github.samolego.kelnar.repository

import io.github.samolego.kelnar.data.Order
import io.github.samolego.kelnar.data.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataRepository(private val localStorage: LocalStorage) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    companion object {
        private const val PRODUCTS_KEY = "products"
        private const val ORDERS_KEY = "orders"
    }

    suspend fun loadData() {
        loadProducts()
        loadOrders()
    }

    private suspend fun loadProducts() {
        val productsJson = localStorage.getString(PRODUCTS_KEY)
        if (productsJson != null) {
            try {
                val productsList = json.decodeFromString<List<Product>>(productsJson)
                _products.value = productsList
            } catch (e: Exception) {
                _products.value = getDefaultProducts()
                saveProducts()
            }
        } else {
            _products.value = getDefaultProducts()
            saveProducts()
        }
    }

    private suspend fun loadOrders() {
        val ordersJson = localStorage.getString(ORDERS_KEY)
        if (ordersJson != null) {
            try {
                val ordersList = json.decodeFromString<List<Order>>(ordersJson)
                _orders.value = ordersList
            } catch (e: Exception) {
                _orders.value = emptyList()
            }
        }
    }

    suspend fun addProduct(product: Product) {
        val currentProducts = _products.value.toMutableList()
        val existingIndex = currentProducts.indexOfFirst { it.id == product.id }

        if (existingIndex >= 0) {
            currentProducts[existingIndex] = product
        } else {
            currentProducts.add(product)
        }

        _products.value = currentProducts
        saveProducts()
    }

    suspend fun removeProduct(productId: String) {
        val currentProducts = _products.value.toMutableList()
        currentProducts.removeAll { it.id == productId }
        _products.value = currentProducts
        saveProducts()
    }

    suspend fun addOrder(order: Order) {
        val currentOrders = _orders.value.toMutableList()
        val existingIndex = currentOrders.indexOfFirst { it.id == order.id }

        if (existingIndex >= 0) {
            currentOrders[existingIndex] = order
        } else {
            currentOrders.add(0, order) // Add new orders at the beginning
        }

        _orders.value = currentOrders
        saveOrders()
    }

    suspend fun updateOrder(order: Order) {
        val currentOrders = _orders.value.toMutableList()
        val existingIndex = currentOrders.indexOfFirst { it.id == order.id }

        if (existingIndex >= 0) {
            currentOrders[existingIndex] = order
            _orders.value = currentOrders
            saveOrders()
        }
    }

    suspend fun removeOrder(orderId: String) {
        val currentOrders = _orders.value.toMutableList()
        currentOrders.removeAll { it.id == orderId }
        _orders.value = currentOrders
        saveOrders()
    }

    fun getProductById(id: String): Product? {
        return _products.value.find { it.id == id }
    }

    fun getOrderById(id: String): Order? {
        return _orders.value.find { it.id == id }
    }

    private suspend fun saveProducts() {
        val productsJson = json.encodeToString(_products.value)
        localStorage.putString(PRODUCTS_KEY, productsJson)
    }

    private suspend fun saveOrders() {
        val ordersJson = json.encodeToString(_orders.value)
        localStorage.putString(ORDERS_KEY, ordersJson)
    }

    private fun getDefaultProducts(): List<Product> {
        return listOf(
                Product(
                        id = "1",
                        name = "Burger",
                        price = 12.99,
                        description = "Classic beef burger with lettuce, tomato, and onion"
                ),
                Product(
                        id = "2",
                        name = "Hot Dog",
                        price = 8.50,
                        description = "Grilled hot dog with ketchup and mustard"
                ),
                Product(
                        id = "3",
                        name = "French Fries",
                        price = 4.99,
                        description = "Crispy golden french fries"
                ),
                Product(
                        id = "4",
                        name = "Coca Cola",
                        price = 2.50,
                        description = "Cold refreshing cola drink"
                ),
                Product(
                        id = "5",
                        name = "Pizza Margherita",
                        price = 15.99,
                        description = "Classic pizza with tomato sauce, mozzarella, and basil"
                )
        )
    }
}
