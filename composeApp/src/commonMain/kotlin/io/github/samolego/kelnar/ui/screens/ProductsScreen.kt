package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.ui.viewmodel.ImportAction
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import io.github.samolego.kelnar.utils.formatAsPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
        viewModel: ProductsViewModel,
        onOpenDrawer: () -> Unit,
        importParam: String = ""
) {
    val products by viewModel.products.collectAsState()
    val showAddProductDialog by viewModel.showAddProductDialog.collectAsState()
    val showEditProductDialog by viewModel.showEditProductDialog.collectAsState()
    val importState by viewModel.importState.collectAsState()

    // Handle import parameter
    LaunchedEffect(importParam) {
        if (importParam.isNotBlank()) {
            viewModel.parseImportUrl(importParam)
        }
    }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Products") },
                        navigationIcon = {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(
                                        Icons.Default.Menu,
                                        contentDescription = "Menu",
                                        tint = Color.White
                                )
                            }
                        },
                        colors =
                                TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = Color.White,
                                        navigationIconContentColor = Color.White
                                )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { viewModel.showAddProductDialog() },
                        containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product", tint = Color.White)
                }
            }
    ) { paddingValues ->
        if (products.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = "No products yet",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Tap + to add your first product",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                            product = product,
                            onEditProduct = { viewModel.showEditProductDialog(product) },
                            onDeleteProduct = { viewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        ProductDialog(
                title = "Add Product",
                viewModel = viewModel,
                onDismiss = { viewModel.hideAddProductDialog() },
                onSave = { viewModel.saveProduct() }
        )
    }

    // Edit Product Dialog
    if (showEditProductDialog) {
        ProductDialog(
                title = "Edit Product",
                viewModel = viewModel,
                onDismiss = { viewModel.hideEditProductDialog() },
                onSave = { viewModel.saveProduct() }
        )
    }

    // Import Products Dialog
    if (importState.isVisible) {
        ImportProductsDialog(
                importState = importState,
                onAction = { action -> viewModel.executeImport(action) }
        )
    }
}

@Composable
fun ProductCard(product: Product, onEditProduct: () -> Unit, onDeleteProduct: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                    )
                    Text(
                            text = product.price.formatAsPrice(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                    )
                    if (product.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = product.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEditProduct) {
                        Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit product",
                                tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteProduct) {
                        Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete product",
                                tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImportProductsDialog(
        importState: io.github.samolego.kelnar.ui.viewmodel.ImportState,
        onAction: (ImportAction) -> Unit
) {
    AlertDialog(
            onDismissRequest = { onAction(ImportAction.CANCEL) },
            title = { Text("Import Products") },
            text = {
                Column {
                    Text(
                            text = "Found ${importState.products.size} products to import:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(importState.products) { product ->
                            Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors =
                                            CardDefaults.cardColors(
                                                    containerColor =
                                                            MaterialTheme.colorScheme.surfaceVariant
                                            )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
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
                                    if (product.description.isNotBlank()) {
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

                    if (importState.skippedItems.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = "Skipped ${importState.skippedItems.size} invalid items:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                        )
                        importState.skippedItems.take(3).forEach { skipped ->
                            Text(
                                    text = "• $skipped",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        if (importState.skippedItems.size > 3) {
                            Text(
                                    text = "• ... and ${importState.skippedItems.size - 3} more",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onAction(ImportAction.ADD_TO_CURRENT) }) {
                        Text("Add to Current")
                    }
                    Button(onClick = { onAction(ImportAction.OVERWRITE_ALL) }) {
                        Text("Overwrite All")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(ImportAction.CANCEL) }) { Text("Cancel") }
            }
    )
}

@Composable
fun ProductDialog(
        title: String,
        viewModel: ProductsViewModel,
        onDismiss: () -> Unit,
        onSave: () -> Unit
) {
    val productName by viewModel.productName.collectAsState()
    val productPrice by viewModel.productPrice.collectAsState()
    val productDescription by viewModel.productDescription.collectAsState()

    val isFormValid =
            remember(productName, productPrice) {
                val name = productName.trim()
                val priceText = productPrice.trim()
                if (name.isBlank() || priceText.isBlank()) {
                    false
                } else {
                    val price = priceText.toDoubleOrNull()
                    price != null && price >= 0
                }
            }

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                            value = productName,
                            onValueChange = { viewModel.setProductName(it) },
                            label = { Text("Product Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                    )

                    OutlinedTextField(
                            value = productPrice,
                            onValueChange = { viewModel.setProductPrice(it) },
                            label = { Text("Price") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            prefix = { Text("$") }
                    )

                    OutlinedTextField(
                            value = productDescription,
                            onValueChange = { viewModel.setProductDescription(it) },
                            label = { Text("Description (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onSave, enabled = isFormValid) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
