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
import kotlinx.serialization.Serializable

@Serializable
data class ImportProduct(val name: String, val price: Double, val description: String = "")

enum class ImportAction {
    CANCEL,
    OVERWRITE_ALL,
    ADD_TO_CURRENT
}

data class ImportState(
        val menu: List<ImportProduct> = emptyList(),
        val isVisible: Boolean = false,
        val skippedItems: List<String> = emptyList()
)

class ProductsViewModel(private val repository: DataRepository) : ViewModel() {

    val menu = repository.menu

    private val _importState = MutableStateFlow(ImportState())
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

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
            val product =
                    Product(
                            id = _currentProduct.value?.id ?: generateId(),
                            name = name,
                            price = price,
                            description = description
                    )

            repository.saveProduct(product)

            if (_showAddProductDialog.value) {
                hideAddProductDialog()
            } else if (_showEditProductDialog.value) {
                hideEditProductDialog()
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch { repository.removeProduct(productId) }
    }

    private fun clearProductForm() {
        _productName.value = ""
        _productPrice.value = ""
        _productDescription.value = ""
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }

    fun parseImportUrl(importParam: String) {
        if (importParam.isBlank()) return

        val cleanParam = importParam.trim().removeSurrounding("[", "]")
        val productStrings = cleanParam.split("|")
        val validProducts = mutableListOf<ImportProduct>()
        val skippedItems = mutableListOf<String>()

        productStrings.forEach { productStr ->
            val parts = productStr.split(";")
            if (parts.size >= 2) {
                val name = parts[0].trim()
                val priceStr = parts[1].trim()
                val description = parts.getOrNull(2)?.trim() ?: ""

                if (name.isNotBlank()) {
                    val price = priceStr.toDoubleOrNull()
                    if (price != null && price > 0) {
                        validProducts.add(ImportProduct(name, price, description))
                    } else {
                        skippedItems.add("$name (invalid price: $priceStr)")
                    }
                } else {
                    skippedItems.add("Product with empty name")
                }
            } else {
                skippedItems.add("Malformed product: $productStr")
            }
        }

        if (validProducts.isNotEmpty()) {
            _importState.value =
                    ImportState(
                            menu = validProducts,
                            isVisible = true,
                            skippedItems = skippedItems
                    )
        }
    }

    fun hideImportDialog() {
        _importState.value = ImportState()
    }

    fun executeImport(action: ImportAction) {
        val currentImportState = _importState.value
        if (!currentImportState.isVisible || currentImportState.menu.isEmpty()) return

        viewModelScope.launch {
            when (action) {
                ImportAction.CANCEL -> {
                    // Do nothing, just close dialog
                }
                ImportAction.OVERWRITE_ALL -> {
                    // Clear all existing menu and add imported ones
                    repository.clearAllProducts()
                    currentImportState.menu.forEach { importProduct ->
                        val product =
                                Product(
                                        id = generateId(),
                                        name = importProduct.name,
                                        price = importProduct.price,
                                        description = importProduct.description
                                )
                        repository.saveProduct(product)
                    }
                }
                ImportAction.ADD_TO_CURRENT -> {
                    // Add imported menu, overwriting duplicates by name
                    currentImportState.menu.forEach { importProduct ->
                        // Check if product with same name exists
                        val existingProduct =
                                repository.menu.value.find {
                                    it.name.equals(importProduct.name, ignoreCase = true)
                                }

                        val product =
                                Product(
                                        id = existingProduct?.id ?: generateId(),
                                        name = importProduct.name,
                                        price = importProduct.price,
                                        description = importProduct.description
                                )
                        repository.saveProduct(product)
                    }
                }
            }
            hideImportDialog()
        }
    }

    fun generateShareData(): String {
        val currentProducts = repository.menu.value
        if (currentProducts.isEmpty()) return "[]"

        val data = currentProducts.joinToString("|") { product ->
            "${product.name};${product.price}" +
            if (product.description.isNotBlank()) ";${product.description}" else ""
        }
        return "[$data]"
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            repository.clearAllProducts()
        }
    }
}
