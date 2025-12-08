package com.example.mini_e_shop.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// LỚP WRAPPER ĐỂ QUẢN LÝ TRẠNG THÁI 'isSelected'
data class SelectableCartItem(
    val details: CartItemDetails,
    val isSelected: Boolean = true // Mặc định là được chọn khi vào giỏ hàng
)

// CẬP NHẬT LẠI UI STATE
sealed class CartUiState {
    object Loading : CartUiState()
    object Empty : CartUiState()
    data class Success(
        val selectableItems: List<SelectableCartItem> = emptyList(),
        val checkoutPrice: Double = 0.0,
        val isAllSelected: Boolean = true
    ) : CartUiState()
}
// --- THÊM SEALED CLASS CHO CÁC SỰ KIỆN VIEW ---
sealed class CartViewEvent {
    data class NavigateToCheckout(val cartItemIds: String) : CartViewEvent()
    data class ShowSnackbar(val message: String) : CartViewEvent()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _cartItems = MutableStateFlow<List<SelectableCartItem>>(emptyList())
    private val _eventChannel = Channel<CartViewEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    val uiState: StateFlow<CartUiState> = _cartItems
        .map { items ->
            if (items.isEmpty()) {
                CartUiState.Empty
            } else {
                val checkoutPrice = items.filter { it.isSelected }
                    .sumOf { it.details.product.price * it.details.cartItem.quantity }

                val isAllSelected = items.isNotEmpty() && items.all { it.isSelected }

                CartUiState.Success(
                    selectableItems = items,
                    checkoutPrice = checkoutPrice,
                    isAllSelected = isAllSelected
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    init {
        observeCartItems()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            userRepository.authPreferencesFlow.flatMapLatest { prefs ->
                if (prefs.isLoggedIn) {
                    cartRepository.getCartItems(prefs.loggedInUserId)
                } else {
                    flowOf(emptyList())
                }
            }.collect { cartItemDetails ->
                val currentSelection = _cartItems.value.associateBy { it.details.cartItem.id }
                _cartItems.value = cartItemDetails.map { detail ->
                    SelectableCartItem(
                        details = detail,
                        isSelected = currentSelection[detail.cartItem.id]?.isSelected ?: true
                    )
                }
            }
        }
    }

    fun onItemCheckedChanged(cartItemId: Int, isChecked: Boolean) {
        _cartItems.value = _cartItems.value.map {
            if (it.details.cartItem.id == cartItemId) {
                it.copy(isSelected = isChecked)
            } else {
                it
            }
        }
    }

    fun onSelectAllChecked(isChecked: Boolean) {
        _cartItems.value = _cartItems.value.map { it.copy(isSelected = isChecked) }
    }

    fun onQuantityChange(cartItemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            val item = _cartItems.value.find { it.details.cartItem.id == cartItemId }
            if (item != null) {
                val product = item.details.product
                if (newQuantity > product.stock) {
                    _eventChannel.send(CartViewEvent.ShowSnackbar("Số lượng vượt quá số hàng còn lại trong kho"))
                    return@launch
                }
            }

            if (newQuantity > 0) {
                cartRepository.updateQuantity(cartItemId, newQuantity)
            } else {
                cartRepository.removeItem(cartItemId)
            }
        }
    }

    fun onCheckoutClick() {
        viewModelScope.launch {
            val selectedIds = _cartItems.value
                .filter { it.isSelected }
                .map { it.details.cartItem.id }
                .joinToString(separator = ",")

            if (selectedIds.isNotEmpty()) {
                _eventChannel.send(CartViewEvent.NavigateToCheckout(selectedIds))
            } else {
                _eventChannel.send(CartViewEvent.ShowSnackbar("Vui lòng chọn ít nhất một sản phẩm"))
            }
        }
    }
}
