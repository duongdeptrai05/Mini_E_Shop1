package com.example.mini_e_shop.presentation.checkout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.AddressEntity
import com.example.mini_e_shop.data.local.dao.CartItemWithProduct
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.AddressRepository
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CheckoutUiState(
    val cartItems: List<CartItemWithProduct> = emptyList(),
    val totalPrice: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isOrderPlaced: Boolean = false,
    val selectedAddress: AddressEntity? = null,
    val addresses: List<AddressEntity> = emptyList()
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository,
    private val userPreferencesManager: UserPreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("cartItemIds")?.let {
            val ids = it.split(",").mapNotNull { idStr -> idStr.toIntOrNull() }
            if (ids.isNotEmpty()) {
                loadCheckoutItems(ids)
            }
        }
    }

    private fun loadCheckoutItems(itemIds: List<Int>) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = userPreferencesManager.authPreferencesFlow.first().loggedInUserId
                if (userId != -1) {
                    launch {
                        addressRepository.getAddressesByUserId(userId).collect { addresses ->
                            val defaultAddress = addresses.find { it.isDefault } ?: addresses.firstOrNull()
                            val currentSelected = _uiState.value.selectedAddress
                            _uiState.value = _uiState.value.copy(
                                addresses = addresses,
                                selectedAddress = currentSelected ?: defaultAddress
                            )
                        }
                    }

                    cartRepository.getCartItems(userId).collect { items ->
                        val filteredItems = items.filter { itemIds.contains(it.cartItem.id) }
                        val total = filteredItems.sumOf { it.product.price * it.cartItem.quantity }
                        _uiState.value = _uiState.value.copy(
                            cartItems = filteredItems.map { it.toCartItemWithProduct() },
                            totalPrice = total,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun selectAddress(address: AddressEntity) {
        _uiState.value = _uiState.value.copy(selectedAddress = address)
    }

    fun placeOrder() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = userPreferencesManager.authPreferencesFlow.first().loggedInUserId
                val address = _uiState.value.selectedAddress
                if (userId != -1 && address != null) {
                    val addressString = "${address.name}, ${address.phone}, ${address.address}"

                    orderRepository.createOrder(
                        userId = userId, 
                        items = _uiState.value.cartItems, 
                        totalAmount = _uiState.value.totalPrice,
                        shippingAddress = addressString
                    )
                    
                    // Clear cart items that were ordered
                    _uiState.value.cartItems.forEach { 
                        cartRepository.removeCartItem(it.cartItem.id)
                    }
                    _uiState.value = _uiState.value.copy(isLoading = false, isOrderPlaced = true)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

// Helper to map back from domain model to DAO model for repo call
private fun com.example.mini_e_shop.presentation.cart.CartItemDetails.toCartItemWithProduct(): CartItemWithProduct {
    return CartItemWithProduct(
        cartItem = com.example.mini_e_shop.data.local.entity.CartItemEntity(
            id = this.cartItem.id,
            userId = this.cartItem.userId,
            productId = this.cartItem.productId,
            quantity = this.cartItem.quantity
        ),
        product = com.example.mini_e_shop.data.local.entity.ProductEntity(
            id = this.product.id,
            name = this.product.name,
            brand = this.product.brand,
            category = this.product.category,
            origin = this.product.origin,
            price = this.product.price,
            stock = this.product.stock,
            imageUrl = this.product.imageUrl,
            description = this.product.description
        )
    )
}
