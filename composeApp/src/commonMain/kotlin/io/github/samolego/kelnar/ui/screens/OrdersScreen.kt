package io.github.samolego.kelnar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.samolego.kelnar.data.Order
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.utils.formatAsPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
        viewModel: OrdersViewModel,
        initialTab: Int = 0,
        onNavigateToNewOrder: () -> Unit,
        onNavigateToOrderDetails: (String, Int) -> Unit,
        onTabChanged: (Int) -> Unit,
        onOpenDrawer: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    var selectedTab by remember(initialTab) { mutableStateOf(initialTab) }

    val activeOrders = orders.filter { !it.isCompleted }
    val completedOrders = orders.filter { it.isCompleted }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Orders") },
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
                        onClick = onNavigateToNewOrder,
                        containerColor = MaterialTheme.colorScheme.primary
                ) { Icon(Icons.Default.Add, contentDescription = "New Order", tint = Color.White) }
            }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Tab Row
            TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            onTabChanged(0)
                        },
                        text = {
                            Text(
                                    "Active",
                                    fontWeight =
                                            if (selectedTab == 0) FontWeight.Bold
                                            else FontWeight.Normal
                            )
                        }
                )
                Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            onTabChanged(1)
                        },
                        text = {
                            Text(
                                    "Completed",
                                    fontWeight =
                                            if (selectedTab == 1) FontWeight.Bold
                                            else FontWeight.Normal
                            )
                        }
                )
            }

            // Content based on selected tab
            when (selectedTab) {
                0 ->
                        OrdersList(
                                orders = activeOrders,
                                onOrderClick = { orderId ->
                                    onNavigateToOrderDetails(orderId, selectedTab)
                                },
                                onDeleteOrder = { viewModel.deleteOrder(it) },
                                onMarkCompleted = { viewModel.markOrderCompleted(it) },
                                emptyMessage = "No active orders",
                                emptySubMessage = "Tap + to create your first order"
                        )
                1 ->
                        OrdersList(
                                orders = completedOrders,
                                onOrderClick = { orderId ->
                                    onNavigateToOrderDetails(orderId, selectedTab)
                                },
                                onDeleteOrder = { viewModel.deleteOrder(it) },
                                onMarkCompleted = { viewModel.markOrderCompleted(it) },
                                emptyMessage = "No completed orders",
                                emptySubMessage = "Complete some orders to see them here"
                        )
            }
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
        emptySubMessage: String
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
            items(orders) { order ->
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

@Composable
fun OrderCard(
        order: Order,
        onClick: () -> Unit,
        onDeleteOrder: () -> Unit,
        onMarkCompleted: () -> Unit
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
                        text = "Table ${order.tableNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!order.isCompleted) {
                        Button(
                                onClick = onMarkCompleted,
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                modifier = Modifier.padding(end = 8.dp)
                        ) { Text("Complete", color = Color.White) }
                    }
                    IconButton(onClick = onDeleteOrder) {
                        Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete order",
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
                        text = "Items: ${order.items.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                        text = order.createdAt.toString().substringBefore('T'),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items preview
            order.items.take(3).forEach { item ->
                Text(
                        text = "• ${item.quantity}x ${item.product.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (order.items.size > 3) {
                Text(
                        text = "• ... and ${order.items.size - 3} more items",
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
                            text = "Total: ${order.total.formatAsPrice()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
