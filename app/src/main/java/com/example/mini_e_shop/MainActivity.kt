package com.example.mini_e_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mini_e_shop.presentation.add_edit_product.AddEditProductScreen
import com.example.mini_e_shop.presentation.address.AddAddressScreen
import com.example.mini_e_shop.presentation.address.AddressListScreen
import com.example.mini_e_shop.presentation.auth.AuthState
import com.example.mini_e_shop.presentation.auth.AuthViewModel
import com.example.mini_e_shop.presentation.auth.MainUiState
import com.example.mini_e_shop.presentation.checkout.CheckoutScreen
import com.example.mini_e_shop.presentation.contact.ContactScreen
import com.example.mini_e_shop.presentation.login.LoginScreen
import com.example.mini_e_shop.presentation.main.MainScreen
import com.example.mini_e_shop.presentation.navigation.Screen
import com.example.mini_e_shop.presentation.orders.OrdersScreen
import com.example.mini_e_shop.presentation.product_detail.ProductDetailScreen
import com.example.mini_e_shop.presentation.register.RegisterScreen
import com.example.mini_e_shop.presentation.settings.SettingsScreen
import com.example.mini_e_shop.presentation.support.SupportScreen
import com.example.mini_e_shop.ui.theme.Mini_E_ShopTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mini_E_ShopTheme {
                val authViewModel = hiltViewModel<AuthViewModel>()
                val authState by authViewModel.authState.collectAsState()
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { padding ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        when (authState) {
                            AuthState.Loading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            AuthState.Authenticated -> {
                                val mainUiState by authViewModel.mainUiState.collectAsState()

                                when (val currentState = mainUiState) {
                                    is MainUiState.Loading -> {
                                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is MainUiState.Success -> {
                                        NavHost(navController = navController, startDestination = Screen.Main.route) {
                                            composable(Screen.Main.route) {
                                                MainScreen(
                                                    mainUiState = currentState,
                                                    currentUser = currentState.currentUser,
                                                    mainNavController = navController,
                                                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                                                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                                                    onNavigateToAddEditProduct = { productId ->
                                                        navController.navigate("${Screen.AddEditProduct.route}?productId=$productId")
                                                    },
                                                    onProductClick = { productId ->
                                                        navController.navigate("${Screen.ProductDetail.route}/$productId")
                                                    },
                                                    onNavigateToSupport = { navController.navigate(Screen.Support.route) },
                                                    onNavigateToCheckout = { cartItemIds ->
                                                        navController.navigate("${Screen.Checkout.route}/$cartItemIds")
                                                    },
                                                    onNavigateToAddresses = { navController.navigate(Screen.AddressList.route) },
                                                    onLogout = { authViewModel.onLogout() }
                                                )
                                            }
                                            composable(Screen.Orders.route) {
                                                OrdersScreen(
                                                    viewModel = hiltViewModel(),
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(
                                                route = "${Screen.AddEditProduct.route}?productId={productId}",
                                                arguments = listOf(
                                                    navArgument("productId") {
                                                        type = NavType.IntType
                                                        defaultValue = -1
                                                    }
                                                )
                                            ) {
                                                AddEditProductScreen(
                                                    viewModel = hiltViewModel(),
                                                    onSave = { navController.popBackStack() },
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(
                                                route = "${Screen.ProductDetail.route}/{productId}",
                                                arguments = listOf(navArgument("productId") { type = NavType.IntType })
                                            ) {
                                                ProductDetailScreen(
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                            composable(Screen.Support.route) {
                                                SupportScreen(
                                                    onBack = { navController.popBackStack() },
                                                    onNavigateToContact = { navController.navigate(Screen.Contact.route) }
                                                )
                                            }
                                            composable(Screen.Contact.route) {
                                                ContactScreen(onBack = { navController.popBackStack() })
                                            }
                                            composable(
                                                route = "${Screen.Checkout.route}/{cartItemIds}",
                                                arguments = listOf(navArgument("cartItemIds") { type = NavType.StringType })
                                            ) {
                                                CheckoutScreen(
                                                    onNavigateBack = { navController.popBackStack() },
                                                    onShowSnackbar = { message ->
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(message)
                                                        }
                                                    }
                                                )
                                            }
                                            composable(Screen.Settings.route) {
                                                SettingsScreen(
                                                    viewModel = hiltViewModel(),
                                                    currentUser = currentState.currentUser,
                                                    onBack = { navController.popBackStack() },
                                                    onNavigateToSupport = { navController.navigate(Screen.Support.route) }
                                                )
                                            }
                                            composable(Screen.AddressList.route) {
                                                AddressListScreen(
                                                    viewModel = hiltViewModel(),
                                                    onBack = { navController.popBackStack() },
                                                    onAddAddress = { navController.navigate(Screen.AddAddress.route) },
                                                    onEditAddress = { addressId -> /* TODO */ }
                                                )
                                            }
                                            composable(Screen.AddAddress.route) {
                                                AddAddressScreen(
                                                    viewModel = hiltViewModel(),
                                                    onBack = { navController.popBackStack() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            AuthState.Unauthenticated -> {
                                NavHost(navController = navController, startDestination = Screen.Login.route) {
                                    composable(Screen.Login.route) {
                                        LoginScreen(
                                            viewModel = hiltViewModel(),
                                            onLoginSuccess = { userId ->
                                                authViewModel.onLoginSuccess(userId)
                                            },
                                            onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                                        )
                                    }
                                    composable(Screen.Register.route) {
                                        RegisterScreen(
                                            viewModel = hiltViewModel(),
                                            onRegisterSuccess = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                                            onBackToLogin = { navController.popBackStack() }
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
