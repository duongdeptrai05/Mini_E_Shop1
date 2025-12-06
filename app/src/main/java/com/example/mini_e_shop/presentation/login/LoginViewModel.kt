package com.example.mini_e_shop.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _usernameOrEmail = MutableStateFlow("")
    val usernameOrEmail = _usernameOrEmail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe = _rememberMe.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    init {
        loadRememberMeCredentials()
    }

    private fun loadRememberMeCredentials() {
        viewModelScope.launch {
            val prefs = userPreferencesManager.authPreferencesFlow.first()
            _usernameOrEmail.value = prefs.rememberMeEmail // Assuming this stores the last login identifier
            if (prefs.rememberMeEmail.isNotEmpty()) {
                _rememberMe.value = true
            }
        }
    }

    fun onUsernameOrEmailChange(newValue: String) {
        _usernameOrEmail.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun onRememberMeChange(newValue: Boolean) {
        _rememberMe.value = newValue
    }

    fun loginUser() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val loginIdentifier = _usernameOrEmail.value
                var user = userRepository.getUserByEmail(loginIdentifier)
                if (user == null) {
                    user = userRepository.getUserByName(loginIdentifier)
                }

                if (user != null && BCrypt.checkpw(_password.value, user.passwordHash)) {
                    if (_rememberMe.value) {
                        // Save the identifier that was used to log in
                        userPreferencesManager.saveLoginState(user.id, loginIdentifier)
                    } else {
                        // If remember me is not checked, we should still save the logged in user's ID but clear the remember me identifier
                         userPreferencesManager.saveLoginState(user.id, "")
                    }
                    
                    _loginState.value = LoginState.Success(user)

                } else {
                    _loginState.value = LoginState.Error("Tên đăng nhập/email hoặc mật khẩu không đúng")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Đã có lỗi xảy ra: ${e.message}")
            }
        }
    }

    sealed interface LoginState {
        object Idle : LoginState
        object Loading : LoginState
        data class Success(val user: UserEntity) : LoginState
        data class Error(val message: String) : LoginState
    }
}
