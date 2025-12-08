package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.preferences.AuthPreferences
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val authPreferencesFlow: Flow<AuthPreferences>
    suspend fun getUserByEmail(email: String): UserEntity?
    suspend fun getUserById(userId: Int): UserEntity?
    suspend fun registerUser(user: UserEntity)
    suspend fun loginUser(email: String, password: String): UserEntity?
    suspend fun saveLoginState(isLoggedIn: Boolean, userId: Int)
    suspend fun clearLoginState()
    suspend fun saveRememberMeEmail(email: String?)
}
