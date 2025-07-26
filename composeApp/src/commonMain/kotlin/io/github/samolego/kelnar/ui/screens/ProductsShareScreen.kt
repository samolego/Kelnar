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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import io.github.samolego.kelnar.utils.QRCodeImage
import io.github.samolego.kelnar.utils.copyToClipboard
import io.github.samolego.kelnar.utils.formatAsPrice
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.back
import kelnar.composeapp.generated.resources.copy_to_clipboard
import kelnar.composeapp.generated.resources.link_copied_to_clipboard
import kelnar.composeapp.generated.resources.menu_to_share_format
import kelnar.composeapp.generated.resources.no_menu_to_share
import kelnar.composeapp.generated.resources.share_link_description
import kelnar.composeapp.generated.resources.share_menu
import kelnar.composeapp.generated.resources.shareable_link
import org.jetbrains.compose.resources.stringResource

// Platform-specific URL encoding - expect/actual pattern
expect fun encodeURIComponent(str: String): String

// Platform-specific base URL generation
expect fun getCurrentBaseUrl(): String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsShareScreen(viewModel: ProductsViewModel, onNavigateBack: () -> Unit) {
    val menu by viewModel.menu.collectAsState()
    var showCopiedSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Generate share data and URL
    val shareData: String = remember(menu) { viewModel.generateShareData() }
    val baseUrl: String = remember { getCurrentBaseUrl() + "#menu/import?data=" }
    val shareUrl: String = remember(shareData) { baseUrl + encodeURIComponent(shareData) }
    var textFieldValue by remember(shareUrl) { mutableStateOf(TextFieldValue(shareUrl)) }
    val copied = stringResource(Res.string.link_copied_to_clipboard)

    LaunchedEffect(showCopiedSnackbar) {
        if (showCopiedSnackbar) {
            snackbarHostState.showSnackbar(
                    message = copied,
                    duration = SnackbarDuration.Short
            )
            showCopiedSnackbar = false
        }
    }

    Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                KelnarAppBar(
                        title = { Text(stringResource(Res.string.share_menu)) },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(Res.string.back),
                                )
                            }
                        },
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
                                text = stringResource(Res.string.shareable_link),
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
                                value = textFieldValue,
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
                                    textFieldValue =
                                            textFieldValue.copy(
                                                    selection = TextRange(0, shareUrl.length)
                                            )
                                    copyToClipboard(shareUrl)
                                    showCopiedSnackbar = true
                                }
                        ) {
                            Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription =
                                            stringResource(Res.string.copy_to_clipboard),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Text(
                            text = stringResource(Res.string.share_link_description, menu.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // QR Code
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        QRCodeImage(
                                data = shareUrl,
                                size = 250,
                                backgroundColor = Color.White,
                                foregroundColor = Color.Black,
                                modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Menu preview section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                            text = stringResource(Res.string.menu_to_share_format, menu.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (menu.isEmpty()) {
                        Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                        ) {
                            Text(
                                    text = stringResource(Res.string.no_menu_to_share),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                                modifier = Modifier.heightIn(max = 400.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(menu) { product ->
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
