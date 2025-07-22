package io.github.samolego.kelnar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.samolego.kelnar.data.Order
import io.github.samolego.kelnar.data.OrderItem
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class OrdersViewModel(private val repository: DataRepository) : ViewModel() {

    val orders = repository.orders
    val products = repository.products

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()

    private val _isEditingOrder = MutableStateFlow(false)
    val isEditingOrder: StateFlow<Boolean> = _isEditingOrder.asStateFlow()

    private val _newOrderItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val newOrderItems: StateFlow<List<OrderItem>> = _newOrderItems.asStateFlow()

    private val _tableNumber = MutableStateFlow("")
    val tableNumber: StateFlow<String> = _tableNumber.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    init {
        viewModelScope.launch {
            products.collect { productsList ->
                updateFilteredProducts(productsList, _searchQuery.value)
            }
        }

        viewModelScope.launch {
            searchQuery.collect { query -> updateFilteredProducts(products.value, query) }
        }
    }

    fun setTableNumber(number: String) {
        _tableNumber.value = number
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun updateFilteredProducts(productsList: List<Product>, query: String) {
        _filteredProducts.value =
                if (query.isBlank()) {
                    productsList
                } else {
                    productsList.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true)
                    }
                }
    }

    fun addProductToOrder(
            product: Product,
            quantity: Int = 1,
            customizations: List<String> = emptyList()
    ) {
        val currentItems = _newOrderItems.value.toMutableList()
        val existingItemIndex =
                currentItems.indexOfFirst {
                    it.product.id == product.id && it.customizations == customizations
                }

        if (existingItemIndex >= 0) {
            val existingItem = currentItems[existingItemIndex]
            currentItems[existingItemIndex] =
                    existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            currentItems.add(
                    OrderItem(
                            id = generateId(),
                            product = product,
                            quantity = quantity,
                            customizations = customizations
                    )
            )
        }

        _newOrderItems.value = currentItems
    }

    fun removeItemFromOrder(itemId: String) {
        val currentItems = _newOrderItems.value.toMutableList()
        currentItems.removeAll { it.id == itemId }
        _newOrderItems.value = currentItems
    }

    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItemFromOrder(itemId)
            return
        }

        val currentItems = _newOrderItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == itemId }

        if (itemIndex >= 0) {
            val item = currentItems[itemIndex]
            currentItems[itemIndex] = item.copy(quantity = newQuantity)
            _newOrderItems.value = currentItems
        }
    }

    fun updateItemCustomizations(itemId: String, customizations: List<String>) {
        val currentItems = _newOrderItems.value.toMutableList()
        val itemIndex = currentItems.indexOfFirst { it.id == itemId }

        if (itemIndex >= 0) {
            val item = currentItems[itemIndex]
            currentItems[itemIndex] = item.copy(customizations = customizations)
            _newOrderItems.value = currentItems
        }
    }

    fun calculateTotal(): Double {
        return _newOrderItems.value.sumOf { it.subtotal }
    }

    fun saveOrder() {
        if (_tableNumber.value.isBlank() || _newOrderItems.value.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val order =
                    Order(
                            id = generateId(),
                            tableNumber = _tableNumber.value,
                            items = _newOrderItems.value,
                            createdAt =
                                    Clock.System.now()
                                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    )

            repository.saveOrder(order)
            clearNewOrder()
        }
    }

    fun clearNewOrder() {
        _newOrderItems.value = emptyList()
        _tableNumber.value = ""
        _searchQuery.value = ""
        _currentOrder.value = null
        _isEditingOrder.value = false
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch { repository.removeOrder(orderId) }
    }

    fun markOrderCompleted(orderId: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                val updatedOrder = order.copy(isCompleted = true)
                repository.saveOrder(updatedOrder)
            }
        }
    }

    fun loadOrderForEditing(orderId: String) {
        viewModelScope.launch {
            val order = repository.getOrderById(orderId)
            if (order != null) {
                // Clear form data but not the editing state
                _newOrderItems.value = emptyList()
                _tableNumber.value = ""
                _searchQuery.value = ""
                _currentOrder.value = null

                // Now load the order data
                _currentOrder.value = order
                _tableNumber.value = order.tableNumber
                _newOrderItems.value = order.items
                _isEditingOrder.value = true
            }
        }
    }

    fun updateExistingOrder() {
        val order = _currentOrder.value
        if (order != null && _tableNumber.value.isNotBlank() && _newOrderItems.value.isNotEmpty()) {
            viewModelScope.launch {
                val updatedOrder =
                        order.copy(tableNumber = _tableNumber.value, items = _newOrderItems.value)
                repository.saveOrder(updatedOrder)
                clearNewOrder()
            }
        }
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
}
