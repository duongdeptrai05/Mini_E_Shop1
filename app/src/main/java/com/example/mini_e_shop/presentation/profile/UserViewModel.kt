package com.example.mini_e_shop.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserRole
import com.example.mini_e_shop.data.mapper.toUser
import com.example.mini_e_shop.domain.model.User
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin = _isAdmin.asStateFlow()

    init {
        viewModelScope.launch {
            val userEntity = userRepository.getUserByEmail("admin@eshop.com")
            _user.value = userEntity?.toUser()
            _isAdmin.value = userEntity?.role == UserRole.ADMIN
        }
    }

    fun logout() {
        // Logic to clear user session
    }
}
