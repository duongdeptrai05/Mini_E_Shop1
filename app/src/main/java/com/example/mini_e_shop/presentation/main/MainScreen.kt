package com.example.mini_e_shop.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mini_e_shop.presentation.main.components.BottomNavigationBar
import com.example.mini_e_shop.presentation.navigation.MainNavGraph

/**
 * MainScreen là màn hình chính sau khi đăng nhập, chứa Bottom Navigation và các màn hình con.
 * Nó nhận MainViewModel từ cấp cao hơn (MainActivity) để quản lý trạng thái chung.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") // Chú thích này cần thiết cho Scaffold
@Composable
fun MainScreen(
    // 1. NHẬN MAINVIEWMODEL TỪ MAINACTIVITY
    mainViewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAddEditProduct: (Int?) -> Unit
) {
    // 2. LẤY TRẠNG THÁI isAdmin TỪ MAINVIEWMODEL
    //    `isAdmin` sẽ là một biến kiểu Boolean (true/false) có thể được quan sát.
    //    `collectAsState()` giúp Composable này tự động vẽ lại khi `isAdmin` thay đổi.
    val isAdmin by mainViewModel.isAdmin.collectAsState()

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // Thanh điều hướng dưới cùng
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues: PaddingValues -> // Nhận paddingValues từ Scaffold

        // 3. TRUYỀN `isAdmin` VÀ CÁC THAM SỐ KHÁC XUỐNG MAINNAVGRAPH
        //    Thêm một Modifier.padding để nội dung không bị thanh điều hướng che mất.
        MainNavGraph(
            modifier = Modifier.padding(paddingValues), // Áp dụng padding
            navController = navController,
            mainViewModel = mainViewModel,
            isAdmin = isAdmin, // <-- TRUYỀN QUYỀN ADMIN XUỐNG CÁC MÀN HÌNH CON
            onNavigateToOrders = onNavigateToOrders,
            onLogout = onLogout,
            onNavigateToAddEditProduct = onNavigateToAddEditProduct
        )
    }
}
