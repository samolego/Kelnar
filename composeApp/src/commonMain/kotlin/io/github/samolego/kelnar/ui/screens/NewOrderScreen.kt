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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.OrderItem
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.add_customization
import kelnar.composeapp.generated.resources.add_product
import kelnar.composeapp.generated.resources.add_special_instructions
import kelnar.composeapp.generated.resources.back
import kelnar.composeapp.generated.resources.cancel
import kelnar.composeapp.generated.resources.clear_search
import kelnar.composeapp.generated.resources.current_customizations
import kelnar.composeapp.generated.resources.customization_example
import kelnar.composeapp.generated.resources.customizations_format
import kelnar.composeapp.generated.resources.customize
import kelnar.composeapp.generated.resources.customize_product_format
import kelnar.composeapp.generated.resources.decrease_quantity
import kelnar.composeapp.generated.resources.edit_order
import kelnar.composeapp.generated.resources.increase_quantity
import kelnar.composeapp.generated.resources.new_order
import kelnar.composeapp.generated.resources.no_items_added_yet
import kelnar.composeapp.generated.resources.order_items
import kelnar.composeapp.generated.resources.price_each_format
import kelnar.composeapp.generated.resources.remove_customization
import kelnar.composeapp.generated.resources.remove_item
import kelnar.composeapp.generated.resources.save
import kelnar.composeapp.generated.resources.search_products
import kelnar.composeapp.generated.resources.select_product
import kelnar.composeapp.generated.resources.table_number
import kelnar.composeapp.generated.resources.tap_add_product_to_get_started
import kelnar.composeapp.generated.resources.total
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewOrderScreen(
        viewModel: OrdersViewModel,
        onNavigateBack: () -> Unit,
        onOrderSaved: () -> Unit
) {
    // Clear any existing order state when starting a new order
    LaunchedEffect(Unit) { viewModel.clearNewOrder() }

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
    val focusManager = LocalFocusManager.current

    var showProductSearch by remember { mutableStateOf(false) }
    var showCustomizationDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<OrderItem?>(null) }

    Scaffold(
            topBar = {
                KelnarAppBar(
                        title = {
                            Text(
                                    if (isEditingOrder) stringResource(Res.string.edit_order)
                                    else stringResource(Res.string.new_order)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.back),
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
                                        contentDescription = stringResource(Res.string.save),
                                )
                            }
                        },
                )
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            // Table Number Input
            OutlinedTextField(
                    value = tableNumber,
                    onValueChange = { viewModel.setTableNumber(it) },
                    label = { Text(stringResource(Res.string.table_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add Product Button
            OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        showProductSearch = true
                    },
                    modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(Res.string.add_product))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Items
            if (orderItems.isNotEmpty()) {
                Text(
                        text = stringResource(Res.string.order_items),
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

                // Total Section
                TotalCard(total = total, modifier = Modifier.fillMaxWidth())
            } else {
                // Empty State
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                                text = stringResource(Res.string.no_items_added_yet),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = stringResource(Res.string.tap_add_product_to_get_started),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
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
                            text =
                                    stringResource(
                                            Res.string.price_each_format,
                                            item.product.price.formatAsPrice()
                                    ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.remove_item),
                            tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (item.customizations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text =
                                stringResource(
                                        Res.string.customizations_format,
                                        item.customizations.joinToString(", ")
                                ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                            onClick = { onQuantityChange(item.quantity - 1) },
                            enabled = item.quantity > 1
                    ) {
                        Icon(
                                Icons.Default.Remove,
                                contentDescription = stringResource(Res.string.decrease_quantity)
                        )
                    }
                    Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
                        Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.increase_quantity)
                        )
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
                        Text(stringResource(Res.string.customize))
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

@Composable
fun TotalCard(total: Double, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier,
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = stringResource(Res.string.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
            )
            Surface(color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.small) {
                Text(
                        text = total.formatAsPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
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
            title = { Text(stringResource(Res.string.select_product)) },
            text = {
                Column {
                    OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            label = { Text(stringResource(Res.string.search_products)) },
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchQueryChange("") }) {
                                        Icon(
                                                Icons.Default.Clear,
                                                contentDescription =
                                                        stringResource(Res.string.clear_search)
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
            confirmButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            }
    )
}

@Composable
fun CustomizationDialog(
        item: OrderItem,
        onCustomizationsChanged: (List<String>) -> Unit,
        onDismiss: () -> Unit
) {
    var customizationText by remember { mutableStateOf("") }
    var customizations by remember { mutableStateOf(item.customizations.toList()) }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(stringResource(Res.string.customize_product_format, item.product.name))
            },
            text = {
                Column {
                    Text(
                            text = stringResource(Res.string.add_special_instructions),
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
                                label = { Text(stringResource(Res.string.customization_example)) },
                                modifier = Modifier.weight(1f)
                        )
                        IconButton(
                                onClick = {
                                    if (customizationText.isNotBlank()) {
                                        customizations = customizations + customizationText.trim()
                                        customizationText = ""
                                    }
                                }
                        ) {
                            Icon(
                                    Icons.Default.Add,
                                    contentDescription =
                                            stringResource(Res.string.add_customization)
                            )
                        }
                    }

                    if (customizations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = stringResource(Res.string.current_customizations),
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
                                IconButton(
                                        onClick = {
                                            customizations =
                                                    customizations.filterIndexed { i, _ ->
                                                        i != index
                                                    }
                                        }
                                ) {
                                    Icon(
                                            Icons.Default.Delete,
                                            contentDescription =
                                                    stringResource(Res.string.remove_customization),
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
                            val finalCustomizations =
                                    if (customizationText.trim().isNotEmpty()) {
                                        customizations + customizationText.trim()
                                    } else {
                                        customizations
                                    }
                            onCustomizationsChanged(finalCustomizations)
                            onDismiss()
                        }
                ) { Text(stringResource(Res.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            }
    )
}
