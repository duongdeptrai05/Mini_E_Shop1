package com.example.mini_e_shop.presentation.auth

import androidx.lifecycle.ViewModel
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

// ViewModel này sẽ được chia sẻ giữa các màn hình để quản lý trạng thái đăng nhập
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    // Kiểm tra xem người dùng hiện tại có phải là Admin không
    val isAdmin: Boolean
        get() = _currentUser.value?.role == UserRole.ADMIN

    // Hàm này sẽ được gọi từ LoginViewModel sau khi đăng nhập thành công
    fun onLoginSuccess(user: UserEntity) {
        _currentUser.value = user
    }

    // Hàm này sẽ được gọi khi người dùng đăng xuất
    fun onLogout() {
        _currentUser.value = null
    }
}