package com.example.mini_e_shop.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(val currentUser: UserEntity?, val isAdmin: Boolean) : MainUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = userRepository.authPreferencesFlow.map {
        if (it.isLoggedIn) AuthState.Authenticated else AuthState.Unauthenticated
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    val mainUiState: StateFlow<MainUiState> = userRepository.authPreferencesFlow.map {
        val user = userRepository.getUserById(it.loggedInUserId)
        if (user != null) {
            MainUiState.Success(user, user.role == com.example.mini_e_shop.data.local.entity.UserRole.ADMIN)
        } else {
            MainUiState.Loading // Or an error state
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainUiState.Loading)

    fun onLoginSuccess(userId: Int) {
        viewModelScope.launch {
            userRepository.saveLoginState(true, userId)
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            userRepository.clearLoginState()
        }
    }
}
