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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.Product
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.viewmodel.ImportAction
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import io.github.samolego.kelnar.utils.formatAsPrice
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.add_product
import kelnar.composeapp.generated.resources.add_to_current
import kelnar.composeapp.generated.resources.and_more_items_skipped_format
import kelnar.composeapp.generated.resources.cancel
import kelnar.composeapp.generated.resources.delete
import kelnar.composeapp.generated.resources.delete_all
import kelnar.composeapp.generated.resources.delete_all_products
import kelnar.composeapp.generated.resources.delete_all_products_confirmation
import kelnar.composeapp.generated.resources.delete_product
import kelnar.composeapp.generated.resources.description_optional
import kelnar.composeapp.generated.resources.edit_product
import kelnar.composeapp.generated.resources.found_products_to_import_format
import kelnar.composeapp.generated.resources.import_products
import kelnar.composeapp.generated.resources.menu
import kelnar.composeapp.generated.resources.more_options
import kelnar.composeapp.generated.resources.no_products_yet
import kelnar.composeapp.generated.resources.overwrite_all
import kelnar.composeapp.generated.resources.price
import kelnar.composeapp.generated.resources.product_name
import kelnar.composeapp.generated.resources.save
import kelnar.composeapp.generated.resources.share_products
import kelnar.composeapp.generated.resources.skipped_invalid_items_format
import kelnar.composeapp.generated.resources.tap_plus_to_add_first_product
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
        viewModel: ProductsViewModel,
        onOpenDrawer: () -> Unit,
        onNavigateToShare: (String) -> Unit = {},
        importParam: String = ""
) {
    val menu by viewModel.menu.collectAsState()
    val showAddProductDialog by viewModel.showAddProductDialog.collectAsState()
    val showEditProductDialog by viewModel.showEditProductDialog.collectAsState()
    val importState by viewModel.importState.collectAsState()

    var showOptionsMenu by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    // Handle import parameter
    LaunchedEffect(importParam) {
        if (importParam.isNotBlank()) {
            viewModel.parseImportUrl(importParam)
        }
    }

    Scaffold(
            topBar = {
                KelnarAppBar(
                        title = { Text(stringResource(Res.string.menu)) },
                        navigationIcon = {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(
                                        Icons.Default.Menu,
                                        contentDescription = stringResource(Res.string.menu),
                                )
                            }
                        },
                        actions = {
                            Box {
                                IconButton(onClick = { showOptionsMenu = true }) {
                                    Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription =
                                                    stringResource(Res.string.more_options),
                                    )
                                }
                                DropdownMenu(
                                        expanded = showOptionsMenu,
                                        onDismissRequest = { showOptionsMenu = false }
                                ) {
                                    DropdownMenuItem(
                                            text = {
                                                Text(stringResource(Res.string.share_products))
                                            },
                                            onClick = {
                                                showOptionsMenu = false
                                                val shareData = viewModel.generateShareData()
                                                onNavigateToShare(shareData)
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Share, contentDescription = null)
                                            }
                                    )
                                    DropdownMenuItem(
                                            text = { Text(stringResource(Res.string.delete_all)) },
                                            onClick = {
                                                showOptionsMenu = false
                                                showDeleteAllDialog = true
                                            },
                                            leadingIcon = {
                                                Icon(
                                                        Icons.Default.Delete,
                                                        contentDescription = null
                                                )
                                            }
                                    )
                                }
                            }
                        },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = { viewModel.showAddProductDialog() },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(Res.string.add_product)
                    )
                }
            }
    ) { paddingValues ->
        if (menu.isEmpty()) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = stringResource(Res.string.no_products_yet),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = stringResource(Res.string.tap_plus_to_add_first_product),
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
                items(menu) { product ->
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
                title = stringResource(Res.string.add_product),
                viewModel = viewModel,
                onDismiss = { viewModel.hideAddProductDialog() },
                onSave = { viewModel.saveProduct() }
        )
    }

    // Edit Product Dialog
    if (showEditProductDialog) {
        ProductDialog(
                title = stringResource(Res.string.edit_product),
                viewModel = viewModel,
                onDismiss = { viewModel.hideEditProductDialog() },
                onSave = { viewModel.saveProduct() }
        )
    }

    // Import Menu Dialog
    if (importState.isVisible) {
        ImportProductsDialog(
                importState = importState,
                onAction = { action -> viewModel.executeImport(action) }
        )
    }

    // Delete All Confirmation Dialog
    if (showDeleteAllDialog) {
        AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text(stringResource(Res.string.delete_all_products)) },
                text = { Text(stringResource(Res.string.delete_all_products_confirmation)) },
                confirmButton = {
                    TextButton(
                            onClick = {
                                showDeleteAllDialog = false
                                viewModel.deleteAllProducts()
                            }
                    ) { Text(stringResource(Res.string.delete)) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
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
                                contentDescription = stringResource(Res.string.edit_product),
                                tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteProduct) {
                        Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.delete_product),
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
            title = { Text(stringResource(Res.string.import_products)) },
            text = {
                Column {
                    Text(
                            text =
                                    stringResource(
                                            Res.string.found_products_to_import_format,
                                            importState.menu.size
                                    ),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(importState.menu) { product ->
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
                                text =
                                        stringResource(
                                                Res.string.skipped_invalid_items_format,
                                                importState.skippedItems.size
                                        ),
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
                                    text =
                                            stringResource(
                                                    Res.string.and_more_items_skipped_format,
                                                    importState.skippedItems.size - 3
                                            ),
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
                        Text(stringResource(Res.string.add_to_current))
                    }
                    Button(onClick = { onAction(ImportAction.OVERWRITE_ALL) }) {
                        Text(stringResource(Res.string.overwrite_all))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(ImportAction.CANCEL) }) {
                    Text(stringResource(Res.string.cancel))
                }
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
                            label = { Text(stringResource(Res.string.product_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                    )

                    OutlinedTextField(
                            value = productPrice,
                            onValueChange = { viewModel.setProductPrice(it) },
                            label = { Text(stringResource(Res.string.price)) },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            suffix = { Text("€") }
                    )

                    OutlinedTextField(
                            value = productDescription,
                            onValueChange = { viewModel.setProductDescription(it) },
                            label = { Text(stringResource(Res.string.description_optional)) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = onSave, enabled = isFormValid) {
                    Text(stringResource(Res.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            }
    )
}
