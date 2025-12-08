package com.example.mini_e_shop.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val rememberMeEmail: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    init {
        viewModelScope.launch {
            val email = userRepository.authPreferencesFlow.first().rememberMeEmail
            _loginState.value = _loginState.value.copy(rememberMeEmail = email)
        }
    }

    fun onLogin(email: String, password: String, rememberMe: Boolean, onLoginSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            _loginState.value = _loginState.value.copy(isLoading = true, error = null)
            val user = userRepository.loginUser(email, password)
            if (user != null) {
                userRepository.saveLoginState(true, user.id)
                if (rememberMe) {
                    userRepository.saveRememberMeEmail(email)
                } else {
                    userRepository.saveRememberMeEmail(null)
                }
                onLoginSuccess(user.id)
            } else {
                _loginState.value = _loginState.value.copy(isLoading = false, error = "Invalid email or password")
            }
        }
    }
}
