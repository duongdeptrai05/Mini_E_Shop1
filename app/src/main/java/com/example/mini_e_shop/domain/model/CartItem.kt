package com.example.mini_e_shop.domain.model

data class CartItem(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val quantity: Int
)
