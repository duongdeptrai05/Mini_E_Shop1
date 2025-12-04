package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.data.local.entity.UserEntity

interface UserRepository {
    suspend fun getUserByEmail(email: String):  UserEntity?
    suspend fun registerUser(user: UserEntity)
}
