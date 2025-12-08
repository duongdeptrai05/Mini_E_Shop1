package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.OrderDao
import com.example.mini_e_shop.data.local.entity.OrderEntity
import com.example.mini_e_shop.data.local.entity.OrderItemEntity
import com.example.mini_e_shop.domain.model.Order
import com.example.mini_e_shop.domain.repository.OrderRepository
import com.example.mini_e_shop.presentation.cart.CartItemDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao
) : OrderRepository {

    override fun getOrders(userId: Int): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByUser(userId)
    }

    override suspend fun createOrder(userId: Int, items: List<com.example.mini_e_shop.data.local.dao.CartItemWithProduct>, totalAmount: Double, shippingAddress: String): Long {
        val orderEntity = OrderEntity(
            userId = userId,
            totalAmount = totalAmount,
            createdAt = Date(),
            shippingAddress = shippingAddress
        )
        val orderId = orderDao.insertOrder(orderEntity)
        
        val orderItems = items.map {
            OrderItemEntity(
                orderId = orderId.toInt(),
                productId = it.product.id,
                quantity = it.cartItem.quantity,
                price = it.product.price
            )
        }
        orderDao.insertOrderItems(orderItems)
        return orderId
    }

    override fun getOrderItems(orderId: Int): Flow<List<OrderItemEntity>> {
        return orderDao.getItemsForOrder(orderId)
    }

    override fun getOrdersForUser(userId: Int): Flow<List<Order>> {
        return orderDao.getOrdersByUser(userId).map { entities ->
            entities.map { entity ->
                Order(
                    id = entity.id,
                    userId = entity.userId,
                    totalAmount = entity.totalAmount,
                    // Chuyển đổi Date sang String để khớp với model 'Order'
                    createdAt = entity.createdAt.toString()
                )
            }
        }
    }

    override suspend fun createOrderFromCart(userId: Int, cartItems: List<CartItemDetails>) {
        if (cartItems.isEmpty()) return

        val totalAmount = cartItems.sumOf { it.product.price * it.cartItem.quantity }

        // Mặc định địa chỉ trống nếu dùng method cũ này
        val orderEntity = OrderEntity(
            userId = userId,
            totalAmount = totalAmount,
            createdAt = Date(),
            shippingAddress = ""
        )

        val orderId = orderDao.insertOrder(orderEntity)

        val orderItemEntities = cartItems.map {
            OrderItemEntity(
                orderId = orderId.toInt(),
                productId = it.product.id,
                quantity = it.cartItem.quantity,
                price = it.product.price
            )
        }

        orderDao.insertOrderItems(orderItemEntities)
    }
}
