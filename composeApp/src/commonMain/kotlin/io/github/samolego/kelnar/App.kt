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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import io.github.samolego.kelnar.repository.DataRepository
import io.github.samolego.kelnar.repository.LocalStorage
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
import io.github.samolego.kelnar.utils.AppConfig
import kotlinx.coroutines.launch

@Composable
fun App(onNavHostReady: suspend (NavController) -> Unit = {}) {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val localStorage = remember { getLocalStorage() }
            val repository = remember { DataRepository(localStorage) }

            val navController = rememberNavController()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val ordersViewModel = remember { OrdersViewModel(repository) }
            val productsViewModel = remember { ProductsViewModel(repository) }

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // Wait for repository to load before setting up navigation
            LaunchedEffect(repository) { repository.loadData() }

            ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.surface) {
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
                            modifier = Modifier.padding(paddingValues).fillMaxSize(),
                            navController = navController,
                            ordersViewModel = ordersViewModel,
                            productsViewModel = productsViewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavHostReady = onNavHostReady
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        ordersViewModel: OrdersViewModel,
        productsViewModel: ProductsViewModel,
        onOpenDrawer: () -> Unit,
        onNavHostReady: suspend (NavController) -> Unit = {}
) {
    NavHost(
            navController = navController,
            startDestination = Orders(),
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        composable<Orders>(
                deepLinks =
                        listOf(
                                navDeepLink {
                                    uriPattern = "${AppConfig.BASE_URL}/orders?tab={tab}"
                                }
                        ),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val orders = backStackEntry.toRoute<Orders>()
            OrdersScreen(
                    viewModel = ordersViewModel,
                    initialTab = orders.tab,
                    onNavigateToNewOrder = { navController.navigate(NewOrder) },
                    onNavigateToOrderDetails = { orderId, _currentTab ->
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
                deepLinks = listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/orders/new" }),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            NewOrderScreen(
                    viewModel = ordersViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSaved = { navController.popBackStack() }
            )
        }

        composable<OrderDetails>(
                deepLinks =
                        listOf(
                                navDeepLink {
                                    uriPattern = "${AppConfig.BASE_URL}/orders/{orderId}"
                                }
                        ),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val orderDetails = backStackEntry.toRoute<OrderDetails>()
            OrderDetailsScreen(
                    orderId = orderDetails.orderId,
                    viewModel = ordersViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { navController.navigate(EditOrder(orderDetails.orderId)) }
            )
        }

        composable<EditOrder>(
                deepLinks =
                        listOf(
                                navDeepLink {
                                    uriPattern = "${AppConfig.BASE_URL}/orders/edit/{orderId}"
                                }
                        ),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val editOrder = backStackEntry.toRoute<EditOrder>()
            EditOrderScreen(
                    orderId = editOrder.orderId,
                    viewModel = ordersViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSaved = { navController.popBackStack() }
            )
        }

        composable<Products>(
                deepLinks = listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/products" }),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            ProductsScreen(
                    viewModel = productsViewModel,
                    onOpenDrawer = onOpenDrawer,
                    onNavigateToShare = { _ -> navController.navigate(ProductsShare) }
            )
        }

        composable<ProductsImport>(
                deepLinks =
                        listOf(
                                navDeepLink {
                                    uriPattern = "${AppConfig.BASE_URL}/products/import?data={data}"
                                }
                        ),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) { backStackEntry ->
            val productsImport = backStackEntry.toRoute<ProductsImport>()
            ProductsScreen(
                    viewModel = productsViewModel,
                    onOpenDrawer = onOpenDrawer,
                    onNavigateToShare = { _ -> navController.navigate(ProductsShare) },
                    importParam = productsImport.data
            )
        }

        composable<ProductsShare>(
                deepLinks =
                        listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/products/share" }),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            ProductsShareScreen(
                    viewModel = productsViewModel,
                    onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    // Notify when NavController is ready
    LaunchedEffect(navController) { onNavHostReady(navController) }
}

expect fun getLocalStorage(): LocalStorage
