package com.example.mini_e_shop.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

// --- AUTH PREFERENCES ---
data class AuthPreferences(
    val isLoggedIn: Boolean,
    val loggedInUserId: Int,
    val rememberMeEmail: String?
)

// --- SETTINGS PREFERENCES ---
data class SettingsPreferences(
    val notificationsEnabled: Boolean,
    val darkModeEnabled: Boolean,
    val language: String
)

@Singleton
class UserPreferencesManager @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        // Auth Keys
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val LOGGED_IN_USER_ID = intPreferencesKey("logged_in_user_id")
        val REMEMBER_ME_EMAIL = stringPreferencesKey("remember_me_email")

        // Settings Keys
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        val APP_LANGUAGE = stringPreferencesKey("app_language")
    }

    // --- AUTH FLOW & FUNCTIONS ---
    val authPreferencesFlow: Flow<AuthPreferences> = context.dataStore.data
        .catchIO()
        .map {
            val isLoggedIn = it[PreferencesKeys.IS_LOGGED_IN] ?: false
            val loggedInUserId = it[PreferencesKeys.LOGGED_IN_USER_ID] ?: -1
            val rememberMeEmail = it[PreferencesKeys.REMEMBER_ME_EMAIL]
            AuthPreferences(isLoggedIn, loggedInUserId, rememberMeEmail)
        }

    suspend fun saveLoginState(isLoggedIn: Boolean, userId: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
            it[PreferencesKeys.LOGGED_IN_USER_ID] = userId
        }
    }
    
    suspend fun clearLoginState() {
        context.dataStore.edit {
            it[PreferencesKeys.IS_LOGGED_IN] = false
            it[PreferencesKeys.LOGGED_IN_USER_ID] = -1
        }
    }

    suspend fun saveRememberMeEmail(email: String?) {
        context.dataStore.edit {
            if (email != null) {
                it[PreferencesKeys.REMEMBER_ME_EMAIL] = email
            } else {
                it.remove(PreferencesKeys.REMEMBER_ME_EMAIL)
            }
        }
    }

    // --- SETTINGS FLOW & FUNCTIONS ---
    val settingsPreferencesFlow: Flow<SettingsPreferences> = context.dataStore.data
        .catchIO()
        .map {
            val notifications = it[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true // Default: true
            val darkMode = it[PreferencesKeys.DARK_MODE_ENABLED] ?: false    // Default: false
            val language = it[PreferencesKeys.APP_LANGUAGE] ?: "Tiếng Việt" // Default: Tiếng Việt
            SettingsPreferences(notifications, darkMode, language)
        }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateDarkModeEnabled(enabled: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit {
            it[PreferencesKeys.APP_LANGUAGE] = language
        }
    }

    // Helper to centralize exception handling for Flows
    private fun Flow<Preferences>.catchIO(): Flow<Preferences> {
        return this.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
    }
}
