package com.example.mini_e_shop.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Gộp tất cả state của màn hình Settings vào một data class
data class SettingsUiState(
    val isLoading: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val language: String = "Tiếng Việt",
    val userName: String = "",
    val userEmail: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            userPreferencesManager.settingsPreferencesFlow.collect { settings ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notificationsEnabled = settings.notificationsEnabled,
                        darkModeEnabled = settings.darkModeEnabled,
                        language = settings.language
                    )
                }
            }
        }
    }

    // Cập nhật thông tin cá nhân (không lưu vào DataStore, chỉ là state tạm thời của UI)
    fun updateUserInfo(name: String, email: String) {
        _uiState.update { it.copy(userName = name, userEmail = email) }
    }

    // Các hàm này giờ sẽ gọi đến UserPreferencesManager để lưu trữ
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.updateNotificationsEnabled(enabled)
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.updateDarkModeEnabled(enabled)
        }
    }

    fun changeLanguage(newLanguage: String) {
        viewModelScope.launch {
            userPreferencesManager.updateLanguage(newLanguage)
        }
    }
}
