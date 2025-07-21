package io.github.samolego.kelnar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.repository.DataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ProductsViewModel(private val repository: DataRepository) : ViewModel() {

    val products = repository.products

    private val _currentProduct = MutableStateFlow<Product?>(null)
    val currentProduct: StateFlow<Product?> = _currentProduct.asStateFlow()

    private val _productName = MutableStateFlow("")
    val productName: StateFlow<String> = _productName.asStateFlow()

    private val _productPrice = MutableStateFlow("")
    val productPrice: StateFlow<String> = _productPrice.asStateFlow()

    private val _productDescription = MutableStateFlow("")
    val productDescription: StateFlow<String> = _productDescription.asStateFlow()

    private val _showAddProductDialog = MutableStateFlow(false)
    val showAddProductDialog: StateFlow<Boolean> = _showAddProductDialog.asStateFlow()

    private val _showEditProductDialog = MutableStateFlow(false)
    val showEditProductDialog: StateFlow<Boolean> = _showEditProductDialog.asStateFlow()

    fun setProductName(name: String) {
        _productName.value = name
    }

    fun setProductPrice(price: String) {
        _productPrice.value = price
    }

    fun setProductDescription(description: String) {
        _productDescription.value = description
    }

    fun showAddProductDialog() {
        clearProductForm()
        _showAddProductDialog.value = true
    }

    fun hideAddProductDialog() {
        _showAddProductDialog.value = false
        clearProductForm()
    }

    fun showEditProductDialog(product: Product) {
        _currentProduct.value = product
        _productName.value = product.name
        _productPrice.value = product.price.toString()
        _productDescription.value = product.description
        _showEditProductDialog.value = true
    }

    fun hideEditProductDialog() {
        _showEditProductDialog.value = false
        _currentProduct.value = null
        clearProductForm()
    }

    fun saveProduct() {
        val name = _productName.value.trim()
        val priceText = _productPrice.value.trim()
        val description = _productDescription.value.trim()

        if (name.isBlank() || priceText.isBlank()) {
            return
        }

        val price = priceText.toDoubleOrNull() ?: return

        viewModelScope.launch {
            val product = Product(
                id = _currentProduct.value?.id ?: generateId(),
                name = name,
                price = price,
                description = description
            )

            repository.addProduct(product)

            if (_showAddProductDialog.value) {
                hideAddProductDialog()
            } else if (_showEditProductDialog.value) {
                hideEditProductDialog()
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.removeProduct(productId)
        }
    }

    private fun clearProductForm() {
        _productName.value = ""
        _productPrice.value = ""
        _productDescription.value = ""
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }

    fun isFormValid(): Boolean {
        val name = _productName.value.trim()
        val priceText = _productPrice.value.trim()

        if (name.isBlank() || priceText.isBlank()) {
            return false
        }

        val price = priceText.toDoubleOrNull()
        return price != null && price > 0
    }
}
