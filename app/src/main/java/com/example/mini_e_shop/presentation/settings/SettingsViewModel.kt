package com.example.mini_e_shop.presentation.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SettingsState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: String = "Tiếng Việt",
    val name: String = "manh",
    val email: String = "manh@gmail.com"
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _state.update { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleDarkMode(enabled: Boolean) {
        _state.update { it.copy(darkModeEnabled = enabled) }
    }

    fun changeLanguage(newLanguage: String) {
        _state.update { it.copy(language = newLanguage) }
    }

    fun updateUserInfo(name: String, email: String) {
        _state.update { it.copy(name = name, email = email) }
    }
}
