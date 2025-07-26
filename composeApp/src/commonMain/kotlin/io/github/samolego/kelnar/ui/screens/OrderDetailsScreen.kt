package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.OrderItem
import io.github.samolego.kelnar.ui.components.CompletedBadge
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice
import io.github.samolego.kelnar.utils.formatAsTime
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.back
import kelnar.composeapp.generated.resources.complete
import kelnar.composeapp.generated.resources.complete_order
import kelnar.composeapp.generated.resources.created_format
import kelnar.composeapp.generated.resources.customizations
import kelnar.composeapp.generated.resources.edit_order_action
import kelnar.composeapp.generated.resources.items_format
import kelnar.composeapp.generated.resources.order_details
import kelnar.composeapp.generated.resources.order_items
import kelnar.composeapp.generated.resources.price_each_format
import kelnar.composeapp.generated.resources.quantity_product_format
import kelnar.composeapp.generated.resources.table_format
import kelnar.composeapp.generated.resources.total
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
        orderId: String,
        viewModel: OrdersViewModel,
        onNavigateBack: () -> Unit,
        onNavigateToEdit: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId }

    LaunchedEffect(order) {
        if (order == null) {
            onNavigateBack()
        }
    }

    order?.let { currentOrder ->
        Scaffold(
                topBar = {
                    KelnarAppBar(
                            title = { Text(stringResource(Res.string.order_details)) },
                            navigationIcon = {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = stringResource(Res.string.back),
                                    )
                                }
                            },
                            actions = {
                                if (!currentOrder.isCompleted) {
                                    IconButton(onClick = onNavigateToEdit) {
                                        Icon(
                                                Icons.Default.Edit,
                                                contentDescription =
                                                        stringResource(
                                                                Res.string.edit_order_action
                                                        ),
                                        )
                                    }
                                }
                            },
                    )
                },
                floatingActionButton = {
                    if (!currentOrder.isCompleted) {
                        ExtendedFloatingActionButton(
                                onClick = {
                                    viewModel.markOrderCompleted(orderId)
                                    onNavigateBack()
                                },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = stringResource(Res.string.complete_order),
                                    modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.complete))
                        }
                    }
                }
        ) { paddingValues ->
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Order Header
                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    if (currentOrder.isCompleted)
                                                            MaterialTheme.colorScheme.surfaceVariant
                                                    else MaterialTheme.colorScheme.primary
                                    )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val textColor =
                                    if (currentOrder.isCompleted)
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onPrimary
                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                        text =
                                                stringResource(
                                                        Res.string.table_format,
                                                        currentOrder.tableNumber
                                                ),
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = textColor,
                                        fontWeight = FontWeight.Bold
                                )
                                if (currentOrder.isCompleted) {
                                    CompletedBadge()
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                    text =
                                            stringResource(
                                                    Res.string.created_format,
                                                    currentOrder.createdAt.formatAsTime()
                                            ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor,
                            )
                            Text(
                                    text =
                                            stringResource(
                                                    Res.string.items_format,
                                                    currentOrder.items.size
                                            ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = textColor,
                            )
                        }
                        Column {
                            // Total
                            Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors =
                                            CardDefaults.cardColors(
                                                    containerColor =
                                                            MaterialTheme.colorScheme
                                                                    .secondaryContainer
                                            )
                            ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                            text = stringResource(Res.string.total),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                    Text(
                                            text = currentOrder.total.formatAsPrice(),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                            text = stringResource(Res.string.order_items),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                    )
                }

                items(currentOrder.items) { item -> OrderItemDetailCard(item = item) }
            }
        }
    }
}

@Composable
fun OrderItemDetailCard(item: OrderItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text =
                                    stringResource(
                                            Res.string.quantity_product_format,
                                            item.quantity,
                                            item.product.name
                                    ),
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
                    if (item.product.description.isNotEmpty()) {
                        Text(
                                text = item.product.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = item.subtotal.formatAsPrice(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (item.customizations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = stringResource(Res.string.customizations),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                )
                item.customizations.forEach { customization ->
                    Text(
                            text = "• $customization",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
