package io.github.samolego.kelnar

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
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
import io.github.samolego.kelnar.ui.navigation.OrderTab
import io.github.samolego.kelnar.ui.navigation.Orders
import io.github.samolego.kelnar.ui.navigation.OrdersActive
import io.github.samolego.kelnar.ui.navigation.OrdersCompleted
import io.github.samolego.kelnar.ui.navigation.Menu
import io.github.samolego.kelnar.ui.navigation.ProductsImport
import io.github.samolego.kelnar.ui.navigation.ProductsShare
import io.github.samolego.kelnar.ui.screens.EditOrderScreen
import io.github.samolego.kelnar.ui.screens.NewOrderScreen
import io.github.samolego.kelnar.ui.screens.OrderDetailsScreen
import io.github.samolego.kelnar.ui.screens.OrdersScreen
import io.github.samolego.kelnar.ui.screens.ProductsScreen
import io.github.samolego.kelnar.ui.screens.ProductsShareScreen
import io.github.samolego.kelnar.ui.theme.ApplyStatusBarColor
import io.github.samolego.kelnar.ui.theme.KelnarTheme
import io.github.samolego.kelnar.ui.viewmodel.OrdersViewModel
import io.github.samolego.kelnar.ui.viewmodel.ProductsViewModel
import io.github.samolego.kelnar.utils.AppConfig
import kelnar.composeapp.generated.resources.Res
import kelnar.composeapp.generated.resources.orders
import kelnar.composeapp.generated.resources.menu
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun App(
        modifier: Modifier = Modifier,
        onNavHostReady: suspend (NavController) -> Unit = {},
) {
    KelnarTheme {
        ApplyStatusBarColor()

        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
                                    label = { Text(stringResource(Res.string.orders)) },
                                    selected = currentRoute?.contains("orders") == true,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(OrdersActive)
                                        }
                                    }
                            )
                            NavigationDrawerItem(
                                    label = { Text(stringResource(Res.string.menu)) },
                                    selected = currentRoute?.contains("menu") == true,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(Menu) {
                                                popUpTo(OrdersActive)
                                            }
                                        }
                                    }
                            )
                        }
                    }
            ) {
                AppNavigation(
                        modifier = Modifier.fillMaxSize(),
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
            startDestination = OrdersActive,
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
    ) {
        // Redirect base orders route to active orders
        composable<Orders>(
                deepLinks = listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/orders" }),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            LaunchedEffect(Unit) {
                navController.navigate(OrdersActive) { popUpTo(Orders) { inclusive = true } }
            }
        }

        composable<OrdersActive>(
                deepLinks =
                        listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/orders/active" }),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            OrdersScreen(
                    viewModel = ordersViewModel,
                    initialTab = OrderTab.ACTIVE,
                    onNavigateToNewOrder = { navController.navigate(NewOrder) },
                    onNavigateToOrderDetails = { orderId ->
                        navController.navigate(OrderDetails(orderId))
                    },
                    onTabChanged = { newTab: OrderTab ->
                        when (newTab) {
                            OrderTab.ACTIVE ->
                                    navController.navigate(OrdersActive) {
                                        popUpTo(OrdersActive) { inclusive = true }
                                    }
                            OrderTab.COMPLETED ->
                                    navController.navigate(OrdersCompleted) {
                                        popUpTo(OrdersActive) { inclusive = true }
                                    }
                        }
                    },
                    onOpenDrawer = onOpenDrawer
            )
        }

        composable<OrdersCompleted>(
                deepLinks =
                        listOf(
                                navDeepLink {
                                    uriPattern = "${AppConfig.BASE_URL}/orders/completed"
                                }
                        ),
                enterTransition = { fadeIn(animationSpec = tween(150)) },
                exitTransition = { fadeOut(animationSpec = tween(150)) }
        ) {
            OrdersScreen(
                    viewModel = ordersViewModel,
                    initialTab = OrderTab.COMPLETED,
                    onNavigateToNewOrder = { navController.navigate(NewOrder) },
                    onNavigateToOrderDetails = { orderId ->
                        navController.navigate(OrderDetails(orderId))
                    },
                    onTabChanged = { newTab: OrderTab ->
                        when (newTab) {
                            OrderTab.ACTIVE ->
                                    navController.navigate(OrdersActive) {
                                        popUpTo(OrdersCompleted) { inclusive = true }
                                    }
                            OrderTab.COMPLETED ->
                                    navController.navigate(OrdersCompleted) {
                                        popUpTo(OrdersCompleted) { inclusive = true }
                                    }
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

        composable<Menu>(
                deepLinks = listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/menu" }),
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
                                    uriPattern = "${AppConfig.BASE_URL}/menu/import?data={data}"
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
                        listOf(navDeepLink { uriPattern = "${AppConfig.BASE_URL}/menu/share" }),
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
