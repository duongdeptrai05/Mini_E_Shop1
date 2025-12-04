package com.example.mini_e_shop.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Order
import com.example.mini_e_shop.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    // Giả sử userId là 1 để demo. Trong ứng dụng thực tế, bạn sẽ lấy từ SessionManager.
    private val userId = 1

    val orderState: StateFlow<OrderUiState> = orderRepository.getOrdersForUser(userId)
        .map { orders ->
            if (orders.isEmpty()) {
                OrderUiState.Empty
            } else {
                OrderUiState.Success(orders)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OrderUiState.Loading
        )
}

sealed class OrderUiState {
    object Loading : OrderUiState()
    object Empty : OrderUiState()
    data class Success(val orders: List<Order>) : OrderUiState()
}
