package io.github.samolego.kelnar.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel

@Composable
fun EditOrderScreen(
        orderId: String,
        viewModel: OrdersViewModel,
        onNavigateBack: () -> Unit,
        onOrderSaved: () -> Unit
) {
    // Load order data when screen is first composed
    LaunchedEffect(orderId) { viewModel.loadOrderForEditing(orderId) }

    OrderFormScreen(
            viewModel = viewModel,
            onNavigateBack = onNavigateBack,
            onOrderSaved = onOrderSaved
    )
}
