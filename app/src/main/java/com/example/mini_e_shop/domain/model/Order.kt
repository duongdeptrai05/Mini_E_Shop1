package com.example.mini_e_shop.domain.model

data class Order(
    val id: Int,
    val userId: Int,
    val totalAmount: Double,
    val createdAt: String
)
