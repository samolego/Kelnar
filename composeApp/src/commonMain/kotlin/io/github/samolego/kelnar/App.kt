package io.github.samolego.kelnar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.samolego.kelnar.repository.DataRepository
import io.github.samolego.kelnar.ui.navigation.Routes
import io.github.samolego.kelnar.ui.screens.NewOrderScreen
import io.github.samolego.kelnar.ui.screens.OrdersScreen
import io.github.samolego.kelnar.ui.screens.ProductsScreen
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import kotlinx.coroutines.launch

@Composable
fun App() {
    MaterialTheme {
        val localStorage = remember { getLocalStorage() }
        val repository = remember { DataRepository(localStorage) }

        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Wait for repository to load before setting up navigation
        LaunchedEffect(repository) { repository.loadData() }

        ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        NavigationDrawerItem(
                                label = { Text("Orders") },
                                selected = currentRoute == Routes.Orders.route,
                                onClick = { scope.launch { drawerState.close() } }
                        )
                        NavigationDrawerItem(
                                label = { Text("Products") },
                                selected = currentRoute == Routes.Products.route,
                                onClick = { scope.launch { drawerState.close() } }
                        )
                    }
                }
        ) {
            Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                AppNavigation(
                        navController = navController,
                        repository = repository,
                        modifier = Modifier.padding(paddingValues),
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                )
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
            startDestination = Routes.Orders.route,
            modifier = modifier
    ) {
        composable(Routes.Orders.route) {
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            OrdersScreen(
                    viewModel = viewModel,
                    onNavigateToNewOrder = {},
                    onOpenDrawer = onOpenDrawer
            )
        }

        composable(Routes.NewOrder.route) {
            val viewModel: OrdersViewModel = viewModel { OrdersViewModel(repository) }
            NewOrderScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOrderSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.Products.route) {
            val viewModel: ProductsViewModel = viewModel { ProductsViewModel(repository) }
            ProductsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenDrawer = onOpenDrawer
            )
        }
    }
}

expect fun getLocalStorage(): io.github.samolego.kelnar.repository.LocalStorage
