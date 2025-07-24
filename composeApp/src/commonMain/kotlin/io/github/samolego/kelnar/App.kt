package io.github.samolego.kelnar

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import io.github.samolego.kelnar.repository.DataRepository
import io.github.samolego.kelnar.ui.navigation.EditOrder
import io.github.samolego.kelnar.ui.navigation.NewOrder
import io.github.samolego.kelnar.ui.navigation.OrderDetails
import io.github.samolego.kelnar.ui.navigation.Orders
import io.github.samolego.kelnar.ui.navigation.Products
import io.github.samolego.kelnar.ui.navigation.ProductsImport
import io.github.samolego.kelnar.ui.navigation.ProductsShare
import io.github.samolego.kelnar.ui.screens.EditOrderScreen
import io.github.samolego.kelnar.ui.screens.NewOrderScreen
import io.github.samolego.kelnar.ui.screens.OrderDetailsScreen
import io.github.samolego.kelnar.ui.screens.OrdersScreen
import io.github.samolego.kelnar.ui.screens.ProductsScreen
import io.github.samolego.kelnar.ui.screens.ProductsShareScreen
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import kotlinx.coroutines.launch

@Composable
fun App(onNavHostReady: suspend (NavController) -> Unit = {}) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val localStorage = remember { getLocalStorage() }
            val repository = remember { DataRepository(localStorage) }

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Wait for repository to load before setting up navigation
            LaunchedEffect(repository) { repository.loadData() }

            // Notify when NavController is ready
            LaunchedEffect(navController) { onNavHostReady(navController) }

            ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationDrawerItem(
                                    label = { Text("Orders") },
                                    selected = currentRoute?.contains("orders") == true,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(Orders())
                                        }
                                    }
                            )
                            NavigationDrawerItem(
                                    label = { Text("Products") },
                                    selected = currentRoute?.contains("products") == true,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(Products) { popUpTo(Orders()) }
                                        }
                                    }
                            )
                        }
                    }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { paddingValues ->
                    AppNavigation(
                            navController = navController,
                            repository = repository,
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize(),
                            onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
        navController: NavHostController,
        repository: DataRepository,
        modifier: Modifier = Modifier,
        onOpenDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Orders(),
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        enterTransition = { fadeIn(animationSpec = tween(150)) },
        exitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        composable<Orders>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val orders = backStackEntry.toRoute<Orders>()
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            OrdersScreen(
                    viewModel = viewModel,
                    initialTab = orders.tab,
                    onNavigateToNewOrder = { navController.navigate(NewOrder) },
                    onNavigateToOrderDetails = { orderId, currentTab ->
                        // Save current tab state when navigating to details
                        navController.navigate(OrderDetails(orderId))
                    },
                    onTabChanged = { newTab ->
                        // Update route with new tab when tab changes
                        navController.navigate(Orders(newTab)) {
                            popUpTo(Orders()) { inclusive = true }
                        }
                    },
                    onOpenDrawer = onOpenDrawer
            )
        }

        composable<NewOrder>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            NewOrderScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSaved = { navController.popBackStack() }
            )
        }

        composable<OrderDetails>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val orderDetails = backStackEntry.toRoute<OrderDetails>()
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            OrderDetailsScreen(
                    orderId = orderDetails.orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(EditOrder(orderDetails.orderId)) }
            )
        }

        composable<EditOrder>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val editOrder = backStackEntry.toRoute<EditOrder>()
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            EditOrderScreen(
                    orderId = editOrder.orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSaved = { navController.popBackStack() }
            )
        }

        composable<Products>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            val viewModel: ProductsViewModel = viewModel { ProductsViewModel(repository) }
            ProductsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = onOpenDrawer,
                    onNavigateToShare = { _ ->
                        navController.navigate(ProductsShare)
                    }
            )
        }

        composable<ProductsImport>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val productsImport = backStackEntry.toRoute<ProductsImport>()
            val viewModel: ProductsViewModel = viewModel { ProductsViewModel(repository) }
            ProductsScreen(
                    viewModel = viewModel,
                    onOpenDrawer = onOpenDrawer,
                    onNavigateToShare = { _ ->
                        navController.navigate(ProductsShare)
                    },
                    importParam = productsImport.data
            )
        }

        composable<ProductsShare>(
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            val viewModel: ProductsViewModel = viewModel { ProductsViewModel(repository) }
            ProductsShareScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

expect fun getLocalStorage(): io.github.samolego.kelnar.repository.LocalStorage
