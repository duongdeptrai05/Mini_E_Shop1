package com.example.mini_e_shop.presentation.main

import androidx.lifecycle.ViewModel
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    // State để lưu trữ thông tin đầy đủ của người dùng hiện tại.
    // Dùng `private` để chỉ có ViewModel này mới có thể thay đổi nó.
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    // State để lưu trữ trạng thái admin (true/false).
    // Giao diện sẽ lắng nghe State này để quyết định hiển thị các nút quản trị.
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    /**
     * Hàm này được gọi một lần duy nhất từ MainActivity sau khi người dùng đăng nhập thành công.
     * Nó nhận vào đối tượng UserEntity và cập nhật tất cả các state liên quan.
     */
    fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
        // Kiểm tra vai trò của người dùng và cập nhật state _isAdmin
        _isAdmin.value = user.role == UserRole.ADMIN
    }

    /**
     * Hàm này được gọi khi người dùng đăng xuất.
     * Nó sẽ reset tất cả các state về giá trị ban đầu.
     */
    fun onLogout() {
        _currentUser.value = null
        _isAdmin.value = false
    }
}