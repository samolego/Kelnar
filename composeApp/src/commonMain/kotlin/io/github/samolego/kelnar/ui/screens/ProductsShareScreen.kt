package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import io.github.samolego.kelnar.utils.QRCodeImage
import io.github.samolego.kelnar.utils.copyToClipboard
import io.github.samolego.kelnar.utils.formatAsPrice

// Platform-specific URL encoding - expect/actual pattern
expect fun encodeURIComponent(str: String): String

// Platform-specific base URL generation
expect fun getCurrentBaseUrl(): String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsShareScreen(viewModel: ProductsViewModel, onNavigateBack: () -> Unit) {
    val products by viewModel.products.collectAsState()
    var showCopiedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Generate share data and URL
    val shareData: String = remember(products) { viewModel.generateShareData() }
    val baseUrl: String = remember { getCurrentBaseUrl() + "#products/import?data=" }
    val shareUrl: String = remember(shareData) { baseUrl + encodeURIComponent(shareData) }

    LaunchedEffect(showCopiedSnackbar) {
        if (showCopiedSnackbar) {
            snackbarHostState.showSnackbar(
                    message = "Link copied to clipboard!",
                    duration = SnackbarDuration.Short
            )
            showCopiedSnackbar = false
        }
    }

    Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                        title = { Text("Share Products") },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
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
            }
    ) { paddingValues ->
        Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Share link section
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
            ) {
                Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "Shareable Link",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                                value = shareUrl,
                                onValueChange = {}, // Read-only
                                enabled = true,
                                readOnly = true,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedTextColor =
                                                        MaterialTheme.colorScheme
                                                                .onPrimaryContainer,
                                                unfocusedTextColor =
                                                        MaterialTheme.colorScheme
                                                                .onPrimaryContainer,
                                                focusedBorderColor =
                                                        MaterialTheme.colorScheme
                                                                .onPrimaryContainer,
                                                unfocusedBorderColor =
                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                .copy(alpha = 0.5f),
                                                focusedContainerColor = Color.Transparent,
                                                unfocusedContainerColor = Color.Transparent
                                        )
                        )

                        IconButton(
                                onClick = {
                                    copyToClipboard(shareUrl)
                                    showCopiedSnackbar = true
                                }
                        ) {
                            Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy to clipboard",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Text(
                            text =
                                    "Share this link to let others import these ${products.size} products",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // QR Code
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        QRCodeImage(
                                data = shareUrl,
                                size = 150,
                                backgroundColor = Color.White,
                                foregroundColor = Color.Black,
                                modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Products preview section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = "Products to Share (${products.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (products.isEmpty()) {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = "No products to share",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                                modifier = Modifier.heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(products) { product ->
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                )
                                ) {
                                    Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                    text = product.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Medium
                                            )
                                            if (product.description.isNotBlank()) {
                                                Text(
                                                        text = product.description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                            }
                                        }
                                        Text(
                                                text = product.price.formatAsPrice(),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
