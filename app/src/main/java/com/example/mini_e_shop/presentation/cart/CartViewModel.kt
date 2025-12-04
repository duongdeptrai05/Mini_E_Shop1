package com.example.mini_e_shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    // Giả sử userId là 1 để demo. Trong ứng dụng thực tế, bạn sẽ lấy từ SessionManager.
    private val userId = 1

    val cartState: StateFlow<CartUiState> = cartRepository.getCartItems(userId)
        .map { items ->
            if (items.isEmpty()) {
                CartUiState.Empty
            } else {
                val totalPrice = items.sumOf { it.product.price * it.cartItem.quantity }
                CartUiState.Success(items, totalPrice)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    fun onQuantityChange(cartItemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            if (newQuantity > 0) {
                cartRepository.updateQuantity(cartItemId, newQuantity)
            } else {
                cartRepository.removeItem(cartItemId)
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val currentState = cartState.value
            if (currentState is CartUiState.Success) {
                orderRepository.createOrderFromCart(userId, currentState.items)
                // Sau khi đặt hàng thành công, xóa tất cả item trong giỏ hàng của user
                cartRepository.clearCart(userId)
            }
        }
    }
}

sealed class CartUiState {
    object Loading : CartUiState()
    object Empty : CartUiState()
    data class Success(
        val items: List<CartItemDetails>,
        val totalPrice: Double
    ) : CartUiState()
}
