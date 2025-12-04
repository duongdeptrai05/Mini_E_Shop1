package com.example.mini_e_shop.domain.model

data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val name: String,
    val role: String
)
