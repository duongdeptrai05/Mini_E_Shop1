package com.example.mini_e_shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.presentation.add_edit_product.AddEditProductScreen
import com.example.mini_e_shop.presentation.add_edit_product.AddEditProductViewModel
import com.example.mini_e_shop.presentation.login.LoginScreen
import com.example.mini_e_shop.presentation.login.LoginViewModel
import com.example.mini_e_shop.presentation.main.MainScreen
import com.example.mini_e_shop.presentation.navigation.Screen
import com.example.mini_e_shop.presentation.orders.OrderViewModel
import com.example.mini_e_shop.presentation.orders.OrdersScreen
import com.example.mini_e_shop.presentation.register.RegisterScreen
import com.example.mini_e_shop.presentation.register.RegisterViewModel
import com.example.mini_e_shop.ui.theme.Mini_E_ShopTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.mini_e_shop.presentation.auth.AuthViewModel
import androidx.activity.viewModels
import com.example.mini_e_shop.presentation.main.MainViewModel


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {


            Mini_E_ShopTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = Screen.Login.route) {

                        composable(Screen.Login.route) {
                            val loginViewModel = hiltViewModel<LoginViewModel>()
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { user ->
                                    // 4. CẬP NHẬT MAINVIEWMODEL SAU KHI ĐĂNG NHẬP
                                    mainViewModel.setCurrentUser(user)
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Screen.Register.route)
                                }
                            )
                        }

                        composable(Screen.Register.route) {
                            val registerViewModel = hiltViewModel<RegisterViewModel>()
                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Register.route) { inclusive = true }
                                    }
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Screen.Main.route) {
                            MainScreen(
                                mainViewModel = mainViewModel,
                                onNavigateToOrders = {
                                    navController.navigate(Screen.Orders.route)
                                },
                                onLogout = {
                                    mainViewModel.onLogout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(Screen.Main.route) { inclusive = true }
                                    }
                                },
                                onNavigateToAddEditProduct = { productId ->
                                    navController.navigate("${Screen.AddEditProduct.route}?productId=$productId")
                                }
                            )
                        }
                        composable(Screen.Orders.route) {
                            val orderViewModel = hiltViewModel<OrderViewModel>()
                            OrdersScreen(
                                viewModel = orderViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("${Screen.AddEditProduct.route}?productId={productId}") {
                            val addEditProductViewModel = hiltViewModel<AddEditProductViewModel>()
                            AddEditProductScreen(
                                viewModel = addEditProductViewModel,
                                onSave = { navController.popBackStack() },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
