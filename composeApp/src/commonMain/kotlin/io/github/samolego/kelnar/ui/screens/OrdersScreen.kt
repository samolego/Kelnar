package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.Order
import io.github.samolego.kelnar.ui.components.KelnarAppBar
import io.github.samolego.kelnar.ui.components.SwipeableOrderCard
import io.github.samolego.kelnar.ui.navigation.OrderTab
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice
import io.github.samolego.kelnar.utils.formatAsTime
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.active
import kelnar.composeapp.generated.resources.and_more_items_format
import kelnar.composeapp.generated.resources.complete
import kelnar.composeapp.generated.resources.complete_order
import kelnar.composeapp.generated.resources.complete_orders_to_see_them_here
import kelnar.composeapp.generated.resources.completed
import kelnar.composeapp.generated.resources.delete_order
import kelnar.composeapp.generated.resources.items_format
import kelnar.composeapp.generated.resources.mark_as_active
import kelnar.composeapp.generated.resources.menu
import kelnar.composeapp.generated.resources.new_order
import kelnar.composeapp.generated.resources.no_active_orders
import kelnar.composeapp.generated.resources.no_completed_orders
import kelnar.composeapp.generated.resources.orders
import kelnar.composeapp.generated.resources.quantity_product_format
import kelnar.composeapp.generated.resources.swipe_right_to_complete_order
import kelnar.composeapp.generated.resources.table_format
import kelnar.composeapp.generated.resources.tap_plus_to_create_first_order
import kelnar.composeapp.generated.resources.total_format
import kelnar.composeapp.generated.resources.uncomplete
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
        viewModel: OrdersViewModel,
        initialTab: OrderTab = OrderTab.ACTIVE,
        onNavigateToNewOrder: () -> Unit,
        onNavigateToOrderDetails: (String) -> Unit,
        onTabChanged: (OrderTab) -> Unit,
        onOpenDrawer: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    val activeOrders = orders.filter { !it.isCompleted }
    val completedOrders = orders.filter { it.isCompleted }

    Scaffold(
            topBar = {
                KelnarAppBar(
                        title = { Text(stringResource(Res.string.orders)) },
                        navigationIcon = {
                            IconButton(onClick = onOpenDrawer) {
                                Icon(
                                        Icons.Default.Menu,
                                        contentDescription = stringResource(Res.string.menu),
                                )
                            }
                        },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                        onClick = onNavigateToNewOrder,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                            Icons.Default.Add,
                            contentDescription = stringResource(Res.string.new_order)
                    )
                }
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Tab Row
            TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Tab(
                        selected = selectedTab == OrderTab.ACTIVE,
                        onClick = {
                            selectedTab = OrderTab.ACTIVE
                            onTabChanged(OrderTab.ACTIVE)
                        },
                        text = {
                            Text(
                                    stringResource(Res.string.active),
                                    fontWeight =
                                            if (selectedTab == OrderTab.ACTIVE) FontWeight.Bold
                                            else FontWeight.Normal
                            )
                        }
                )
                Tab(
                        selected = selectedTab == OrderTab.COMPLETED,
                        onClick = {
                            selectedTab = OrderTab.COMPLETED
                            onTabChanged(OrderTab.COMPLETED)
                        },
                        text = {
                            Text(
                                    stringResource(Res.string.completed),
                                    fontWeight =
                                            if (selectedTab == OrderTab.COMPLETED) FontWeight.Bold
                                            else FontWeight.Normal
                            )
                        }
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                OrderTab.ACTIVE -> {
                    Column {
                        // Show swipe hint if there are active orders but no completed orders
                        if (activeOrders.isNotEmpty() && completedOrders.isEmpty()) {
                            SwipeHint()
                        }
                        OrdersList(
                                orders = activeOrders,
                                onOrderClick = { orderId -> onNavigateToOrderDetails(orderId) },
                                onDeleteOrder = { viewModel.deleteOrder(it) },
                                onMarkCompleted = { viewModel.markOrderCompleted(it) },
                                emptyMessage = stringResource(Res.string.no_active_orders),
                                emptySubMessage =
                                        stringResource(Res.string.tap_plus_to_create_first_order),
                                showSwipeToComplete = true
                        )
                    }
                }
                OrderTab.COMPLETED ->
                        OrdersList(
                                orders = completedOrders,
                                onOrderClick = { orderId -> onNavigateToOrderDetails(orderId) },
                                onDeleteOrder = { viewModel.deleteOrder(it) },
                                onMarkCompleted = { viewModel.markOrderCompleted(it) },
                                emptyMessage = stringResource(Res.string.no_completed_orders),
                                emptySubMessage =
                                        stringResource(Res.string.complete_orders_to_see_them_here),
                                showSwipeToComplete = false,
                                showSwipeToUncomplete = true
                        )
            }
        }
    }
}

@Composable
fun SwipeHint() {
    Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                    text = stringResource(Res.string.swipe_right_to_complete_order),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun OrdersList(
        orders: List<Order>,
        onOrderClick: (String) -> Unit,
        onDeleteOrder: (String) -> Unit,
        onMarkCompleted: (String) -> Unit,
        emptyMessage: String,
        emptySubMessage: String,
        showSwipeToComplete: Boolean = false,
        showSwipeToUncomplete: Boolean = false
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = emptySubMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                Box {
                    when {
                        showSwipeToComplete && !order.isCompleted -> {
                            SwipeToCompleteOrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) },
                                    onDeleteOrder = { onDeleteOrder(order.id) },
                                    onMarkCompleted = { onMarkCompleted(order.id) }
                            )
                        }
                        showSwipeToUncomplete && order.isCompleted -> {
                            SwipeToUncompleteOrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) },
                                    onDeleteOrder = { onDeleteOrder(order.id) },
                                    onMarkCompleted = { onMarkCompleted(order.id) }
                            )
                        }
                        else -> {
                            OrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) },
                                    onDeleteOrder = { onDeleteOrder(order.id) },
                                    onMarkCompleted = { onMarkCompleted(order.id) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToCompleteOrderCard(
        order: Order,
        onClick: () -> Unit,
        onDeleteOrder: () -> Unit,
        onMarkCompleted: () -> Unit
) {
    SwipeableOrderCard(
            order = order,
            onClick = onClick,
            onDeleteOrder = onDeleteOrder,
            onSwipeAction = onMarkCompleted,
            swipeActionIcon = Icons.Default.CheckCircle,
            swipeActionText = stringResource(Res.string.complete_order),
            swipeActionColor = MaterialTheme.colorScheme.primary,
            onSwipeActionColor = MaterialTheme.colorScheme.onPrimary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToUncompleteOrderCard(
        order: Order,
        onClick: () -> Unit,
        onDeleteOrder: () -> Unit,
        onMarkCompleted: () -> Unit
) {
    SwipeableOrderCard(
            order = order,
            onClick = onClick,
            onDeleteOrder = onDeleteOrder,
            onSwipeAction = onMarkCompleted,
            swipeActionIcon = Icons.AutoMirrored.Filled.Undo,
            swipeActionText = stringResource(Res.string.mark_as_active),
            swipeActionColor = MaterialTheme.colorScheme.tertiary,
            onSwipeActionColor = MaterialTheme.colorScheme.onTertiary
    )
}

@Composable
fun OrderCard(
        order: Order,
        onClick: () -> Unit,
        onDeleteOrder: () -> Unit,
        onMarkCompleted: () -> Unit,
        hideCompleteButton: Boolean = false
) {
    Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor =
                                    if (order.isCompleted) MaterialTheme.colorScheme.surfaceVariant
                                    else MaterialTheme.colorScheme.surface
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = stringResource(Res.string.table_format, order.tableNumber),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!order.isCompleted && !hideCompleteButton) {
                        Button(
                                onClick = onMarkCompleted,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                modifier = Modifier.padding(end = 8.dp)
                        ) { Text(stringResource(Res.string.complete)) }
                    }
                    IconButton(onClick = onDeleteOrder) {
                        Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.delete_order),
                                tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order summary
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        text = stringResource(Res.string.items_format, order.items.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                        text = order.createdAt.formatAsTime(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items preview
            order.items.take(3).forEach { item ->
                Text(
                        text =
                                stringResource(
                                        Res.string.quantity_product_format,
                                        item.quantity,
                                        item.product.name
                                ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (order.items.size > 3) {
                Text(
                        text =
                                stringResource(
                                        Res.string.and_more_items_format,
                                        order.items.size - 3
                                ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Total price
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                ) {
                    Text(
                            text =
                                    stringResource(
                                            Res.string.total_format,
                                            order.total.formatAsPrice()
                                    ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
