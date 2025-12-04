package com.example.mini_e_shop.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mini_e_shop.presentation.cart.CartScreen
import com.example.mini_e_shop.presentation.cart.CartViewModel
import com.example.mini_e_shop.presentation.main.MainViewModel
import com.example.mini_e_shop.presentation.products_list.ProductListScreen
import com.example.mini_e_shop.presentation.products_list.ProductListViewModel
import com.example.mini_e_shop.presentation.profile.ProfileScreen
import com.example.mini_e_shop.presentation.profile.UserViewModel

@Composable
fun MainNavGraph(
    // 1. THÊM CÁC THAM SỐ CÒN THIẾU VÀO ĐÂY
    modifier: Modifier = Modifier,
    navController: NavHostController,
    mainViewModel: MainViewModel,
    isAdmin: Boolean, // <-- Tham số quan trọng để nhận quyền admin
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit
) {
    // 2. SỬA LẠI CÚ PHÁP CỦA NavHost (thêm dấu phẩy)
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route, // Dùng BottomBarScreen cho nhất quán
        modifier = modifier // Áp dụng modifier padding từ MainScreen
    ) {
        composable(Screen.Home.route) {
            ProductListScreen(
                viewModel = hiltViewModel(),
                mainViewModel = mainViewModel,
                isAdmin = isAdmin,
                onNavigateToAddEditProduct = onNavigateToAddEditProduct
            )
        }
        composable(Screen.Favorites.route) {
            // Màn hình Yêu thích sẽ được tạo ở đây sau
        }
        composable(Screen.Cart.route) {
            // Giữ nguyên, không cần thay đổi
            val cartViewModel = hiltViewModel<CartViewModel>()
            CartScreen(viewModel = cartViewModel)
        }
        composable(Screen.Profile.route) {
            // Giữ nguyên, không cần thay đổi
            val userViewModel = hiltViewModel<UserViewModel>()
            ProfileScreen(
                viewModel = userViewModel,
                onNavigateToOrders = onNavigateToOrders,
                onLogout = onLogout
            )
        }
    }
}
