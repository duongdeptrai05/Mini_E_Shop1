package com.example.mini_e_shop.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.local.entity.UserRole
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState = _registerState.asStateFlow()

    fun onNameChange(newValue: String) {
        _name.value = newValue
    }

    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }

    fun registerUser() {
        if (name.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
            _registerState.value = RegisterState.Error("Vui lòng điền đầy đủ thông tin.")
            return
        }
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                if (userRepository.getUserByEmail(email.value) != null) {
                    _registerState.value = RegisterState.Error("Email này đã được đăng ký.")
                    return@launch
                }
                val hashedPassword = BCrypt.hashpw(password.value, BCrypt.gensalt())
                val newUser = UserEntity(
                    name = name.value,
                    email = email.value,
                    passwordHash = hashedPassword,
                    role = UserRole.USER
                )
                userRepository.registerUser(newUser)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Đã có lỗi xảy ra: ${e.message}")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }
}

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}
