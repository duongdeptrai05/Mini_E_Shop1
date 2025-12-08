package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.UserDao
import com.example.mini_e_shop.data.local.entity.UserEntity
import com.example.mini_e_shop.data.preferences.AuthPreferences
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val userPreferencesManager: UserPreferencesManager
) : UserRepository {

    override val authPreferencesFlow: Flow<AuthPreferences>
        get() = userPreferencesManager.authPreferencesFlow

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun getUserById(userId: Int): UserEntity? {
        return userDao.getUserById(userId)
    }

    override suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    override suspend fun loginUser(email: String, password: String): UserEntity? {
        val user = userDao.getUserByEmail(email)
        return if (user != null && BCrypt.checkpw(password, user.passwordHash)) {
            user
        } else {
            null
        }
    }

    override suspend fun saveLoginState(isLoggedIn: Boolean, userId: Int) {
        userPreferencesManager.saveLoginState(isLoggedIn, userId)
    }

    override suspend fun clearLoginState() {
        userPreferencesManager.clearLoginState()
    }

    override suspend fun saveRememberMeEmail(email: String?) {
        userPreferencesManager.saveRememberMeEmail(email)
    }
}
