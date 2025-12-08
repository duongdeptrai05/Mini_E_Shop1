package com.example.mini_e_shop.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MainThemeState(
    val isDarkMode: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    val themeState: StateFlow<MainThemeState> = userPreferencesManager.settingsPreferencesFlow
        .map { settings -> MainThemeState(isDarkMode = settings.darkModeEnabled) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainThemeState()
        )
}
