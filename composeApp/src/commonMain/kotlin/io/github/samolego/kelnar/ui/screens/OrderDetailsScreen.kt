package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.OrderItem
import io.github.samolego.kelnar.ui.components.CompletedBadge
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice
import io.github.samolego.kelnar.utils.formatAsTime
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.complete
import kelnar.composeapp.generated.resources.created_format
import kelnar.composeapp.generated.resources.customer_paid
import kelnar.composeapp.generated.resources.customizations
import kelnar.composeapp.generated.resources.edit_order
import kelnar.composeapp.generated.resources.items_format
import kelnar.composeapp.generated.resources.not_enough_money
import kelnar.composeapp.generated.resources.order_items
import kelnar.composeapp.generated.resources.price_each_format
import kelnar.composeapp.generated.resources.quantity_product_format
import kelnar.composeapp.generated.resources.return_and_complete_format
import kelnar.composeapp.generated.resources.table_format
import kelnar.composeapp.generated.resources.total
import org.jetbrains.compose.resources.stringResource
import kotlin.math.ceil

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

    // Payment bottom sheet state
    var showPaymentBottomSheet by remember { mutableStateOf(false) }
    var customerPaidAmount by remember { mutableStateOf("") }
    val customerPaidDouble = customerPaidAmount.toDoubleOrNull() ?: 0.0
    val returnAmount = customerPaidDouble - (order?.total ?: 0.0)
    val isValidPayment = customerPaidAmount.isEmpty() || customerPaidDouble >= (order?.total ?: 0.0)

    LaunchedEffect(order) {
        if (order == null) {
            onNavigateBack()
        }
    }

    order?.let { currentOrder ->
        Scaffold(
                topBar = {
                    KelnarAppBar(
                            title = { Text("Order Details") },
                            navigationIcon = {
                                IconButton(onClick = onNavigateBack) {
                                    Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                    )
                                }
                            },
                            actions = {
                                if (!currentOrder.isCompleted) {
                                    IconButton(onClick = onNavigateToEdit) {
                                        Icon(
                                                Icons.Default.Edit,
                                                contentDescription =
                                                        stringResource(Res.string.edit_order),
                                        )
                                    }
                                }
                            },
                    )
                },
                floatingActionButton = {
                    if (!currentOrder.isCompleted) {
                        ExtendedFloatingActionButton(
                                onClick = { showPaymentBottomSheet = true },
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Complete Order",
                                    modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.complete))
                        }
                    }
                }
        ) { paddingValues ->
            LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 64.dp,
                    ),
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

        // Payment Bottom Sheet
        if (showPaymentBottomSheet) {
            ModalBottomSheet(
                    onDismissRequest = { showPaymentBottomSheet = false },
                    containerColor = MaterialTheme.colorScheme.primary,
                    dragHandle = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(4.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(2.dp)
                                    ),
                            )
                        }
                       }
            ) {
                PaymentBottomSheetContent(
                        billTotal = currentOrder.total,
                        customerPaidAmount = customerPaidAmount,
                        onCustomerPaidChange = { customerPaidAmount = it },
                        isValidPayment = isValidPayment,
                        returnAmount = returnAmount,
                        onCompleteOrder = {
                            viewModel.markOrderCompleted(orderId)
                            onNavigateBack()
                        }
                )
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

@Composable
fun PaymentBottomSheetContent(
        billTotal: Double,
        customerPaidAmount: String,
        onCustomerPaidChange: (String) -> Unit,
        isValidPayment: Boolean,
        returnAmount: Double,
        onCompleteOrder: () -> Unit
) {
    Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bill total display
        Card(
                modifier = Modifier.fillMaxWidth(),
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                        )
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = stringResource(Res.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = billTotal.formatAsPrice(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                )
            }
        }

        val currentContentColor = if (isValidPayment) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onError
        }

        // Customer payment input
        OutlinedTextField(
                value = customerPaidAmount,
                onValueChange = onCustomerPaidChange,
                label = {
                    Text(
                            stringResource(Res.string.customer_paid),
                            color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                placeholder = {
                    Text(
                            ceil(billTotal).toInt().toString(),
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                                focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                                unfocusedBorderColor =
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                cursorColor = MaterialTheme.colorScheme.onPrimary,
                                selectionColors = TextSelectionColors(
                                        handleColor = currentContentColor,
                                        backgroundColor = currentContentColor.copy(alpha = 0.3f)
                                ),
                                errorTextColor = MaterialTheme.colorScheme.onError,
                                errorBorderColor = MaterialTheme.colorScheme.onError,
                                errorCursorColor = MaterialTheme.colorScheme.onError,
                                errorLabelColor = MaterialTheme.colorScheme.onError,
                        ),
                isError = !isValidPayment
        )

        // Complete button
        Button(
                onClick = onCompleteOrder,
                enabled = isValidPayment,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors =
                        ButtonDefaults.buttonColors(
                                containerColor =
                                        if (isValidPayment) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.error
                                        }
                        )
        ) {
            Icon(
                    if (isValidPayment) {
                        Icons.Default.CheckCircle
                    } else {
                        Icons.Default.ErrorOutline
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isValidPayment) {
                             MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onError
                            }
            )
            Spacer(modifier = Modifier.width(8.dp))

            val buttonText =
                    when {
                        customerPaidAmount.isEmpty() ||
                                customerPaidAmount.toDoubleOrNull() == billTotal -> {
                            stringResource(Res.string.complete)
                        }
                        returnAmount > 0 -> {
                            stringResource(
                                    Res.string.return_and_complete_format,
                                    returnAmount.formatAsPrice()
                            )
                        }
                        else -> {
                            stringResource(
                                    Res.string.not_enough_money,
                                (-returnAmount).formatAsPrice()
                            )
                        }
                    }

            Text(
                    text = buttonText,
                    color =
                            if (isValidPayment) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onError
                            }
            )
        }
    }
}
