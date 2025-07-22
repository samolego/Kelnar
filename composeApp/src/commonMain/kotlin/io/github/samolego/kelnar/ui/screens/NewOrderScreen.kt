package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.OrderItem
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
        viewModel: OrdersViewModel,
        onNavigateBack: () -> Unit,
        onOrderSaved: () -> Unit
) {
    OrderFormScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onOrderSaved = onOrderSaved
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(
        viewModel: OrdersViewModel,
        onNavigateBack: () -> Unit,
        onOrderSaved: () -> Unit
) {
    val tableNumber by viewModel.tableNumber.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val orderItems by viewModel.newOrderItems.collectAsState()
    val isEditingOrder by viewModel.isEditingOrder.collectAsState()
    val total by remember { derivedStateOf { orderItems.sumOf { it.subtotal } } }

    var showProductSearch by remember { mutableStateOf(false) }
    var showCustomizationDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<OrderItem?>(null) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text(if (isEditingOrder) "Edit Order" else "New Order") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                    onClick = {
                                        if (isEditingOrder) {
                                            viewModel.updateExistingOrder()
                                        } else {
                                            viewModel.saveOrder()
                                        }
                                        onOrderSaved()
                                    },
                                    enabled = tableNumber.isNotBlank() && orderItems.isNotEmpty()
                            ) {
                                Icon(
                                        Icons.Default.Save,
                                        contentDescription = "save",
                                        tint = Color.White,
                                )
                                Text("Save", color = Color.White)
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = Color.White,
                                        navigationIconContentColor = Color.White
                                )
                )
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            // Table Number Input
            OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { viewModel.setTableNumber(it) },
                    label = { Text("Table Number") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Product Button
            OutlinedButton(
                    onClick = { showProductSearch = true },
                    modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Product")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Items
            if (orderItems.isNotEmpty()) {
                Text(
                        text = "Order Items",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orderItems) { item ->
                        OrderItemCard(
                                item = item,
                                onQuantityChange = { newQuantity ->
                                    viewModel.updateItemQuantity(item.id, newQuantity)
                                },
                                onCustomize = {
                                    selectedItem = item
                                    showCustomizationDialog = true
                                },
                                onRemove = { viewModel.removeItemFromOrder(item.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Total
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "Total:",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                        )
                        Text(
                                text = total.formatAsPrice(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                            text = "No items added yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Product Search Dialog
    if (showProductSearch) {
        ProductSearchDialog(
                searchQuery = searchQuery,
                products = filteredProducts,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onProductSelected = { product ->
                    viewModel.addProductToOrder(product)
                    showProductSearch = false
                    viewModel.setSearchQuery("")
                },
                onDismiss = {
                    showProductSearch = false
                    viewModel.setSearchQuery("")
                }
        )
    }

    // Customization Dialog
    if (showCustomizationDialog && selectedItem != null) {
        CustomizationDialog(
                item = selectedItem!!,
                onCustomizationsChanged = { customizations ->
                    viewModel.updateItemCustomizations(selectedItem!!.id, customizations)
                },
                onDismiss = {
                    showCustomizationDialog = false
                    selectedItem = null
                }
        )
    }
}

@Composable
fun OrderItemCard(
        item: OrderItem,
        onQuantityChange: (Int) -> Unit,
        onCustomize: () -> Unit,
        onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = item.product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = "${item.product.price.formatAsPrice()} each",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove item",
                            tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (item.customizations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "Customizations: ${item.customizations.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                            onClick = { onQuantityChange(item.quantity - 1) },
                            enabled = item.quantity > 1
                    ) { Icon(Icons.Default.Remove, contentDescription = "Decrease quantity") }
                    Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Icon(Icons.Default.Add, contentDescription = "Increase quantity")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onCustomize) {
                        Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Customize")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            text = item.subtotal.formatAsPrice(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchDialog(
        searchQuery: String,
        products: List<Product>,
        onSearchQueryChange: (String) -> Unit,
        onProductSelected: (Product) -> Unit,
        onDismiss: () -> Unit
) {
    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Select Product") },
            text = {
                Column {
                    OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            label = { Text("Search products...") },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Clear search"
                                        )
                                    }
                                }
                            }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                            modifier = Modifier.height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(products) { product ->
                            Card(
                                    onClick = { onProductSelected(product) },
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                            text = product.price.formatAsPrice(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                    )
                                    if (product.description.isNotEmpty()) {
                                        Text(
                                                text = product.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun CustomizationDialog(
        item: OrderItem,
        onCustomizationsChanged: (List<String>) -> Unit,
        onDismiss: () -> Unit
) {
    var customizationText by remember { mutableStateOf("") }
    var customizations by remember { mutableStateOf(item.customizations.toMutableList()) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Customize ${item.product.name}") },
            text = {
                Column {
                    Text(
                            text = "Add special instructions or modifications:",
                            style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                                value = customizationText,
                                onValueChange = { customizationText = it },
                                label = { Text("e.g., no ketchup") },
                                modifier = Modifier.weight(1f)
                        )
                        IconButton(
                                onClick = {
                                    if (customizationText.isNotBlank()) {
                                        customizations.add(customizationText.trim())
                                        customizationText = ""
                                    }
                                }
                        ) { Icon(Icons.Default.Add, contentDescription = "Add customization") }
                    }

                    if (customizations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Current customizations:",
                                style = MaterialTheme.typography.labelMedium
                        )
                        customizations.forEachIndexed { index, customization ->
                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                        text = "• $customization",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { customizations.removeAt(index) }) {
                                    Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove customization",
                                            modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                        onClick = {
                            onCustomizationsChanged(customizations)
                            onDismiss()
                        }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
