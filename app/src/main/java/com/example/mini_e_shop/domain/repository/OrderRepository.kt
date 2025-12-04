package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.data.local.entity.OrderEntity
import com.example.mini_e_shop.data.local.entity.OrderItemEntity
import com.example.mini_e_shop.domain.model.Order
import com.example.mini_e_shop.presentation.cart.CartItemDetails
import kotlinx.coroutines.flow.Flow


interface OrderRepository {
    fun getOrders(userId: Int): Flow<List<OrderEntity>>
    suspend fun createOrder(order: OrderEntity, items: List<OrderItemEntity>): Long
    fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>>
    suspend fun createOrderFromCart(userId: Int, cartItems: List<CartItemDetails>)
    fun getOrdersForUser(userId: Int): Flow<List<Order>>
}

