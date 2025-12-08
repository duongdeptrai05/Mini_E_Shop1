package com.example.mini_e_shop.presentation.product_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Định nghĩa UiState riêng cho màn hình chi tiết
sealed class ProductDetailUiState {
    object Loading : ProductDetailUiState()
    data class Success(val product: Product, val isFavorite: Boolean) : ProductDetailUiState()
    data class Error(val message: String) : ProductDetailUiState()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    init {
        observeProduct()
    }

    private fun observeProduct() {
        viewModelScope.launch {
            val productId = savedStateHandle.get<Int>("productId")
            if (productId == null) {
                _uiState.value = ProductDetailUiState.Error("Product ID not found")
                return@launch
            }

            userRepository.authPreferencesFlow.flatMapLatest { prefs ->
                val favoriteFlow = if (prefs.isLoggedIn) {
                    favoriteRepository.isFavorite(prefs.loggedInUserId, productId)
                } else {
                    flowOf(false)
                }

                // Giả sử getProductById là suspend function, chúng ta gọi nó và tạo flow
                val product = productRepository.getProductById(productId)
                val productFlow = flowOf(product)

                productFlow.combine(favoriteFlow) { prod, isFavorite ->
                    if (prod != null) {
                        ProductDetailUiState.Success(prod, isFavorite)
                    } else {
                        ProductDetailUiState.Error("Product not found")
                    }
                }
            }.catch { e ->
                _uiState.value = ProductDetailUiState.Error(e.message ?: "Unknown error")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onFavoriteToggle() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProductDetailUiState.Success) {
                val product = currentState.product
                val isFavorite = currentState.isFavorite

                userRepository.authPreferencesFlow.firstOrNull()?.let { prefs ->
                    if (prefs.isLoggedIn) {
                        if (isFavorite) {
                            favoriteRepository.removeFavorite(prefs.loggedInUserId, product.id)
                        } else {
                            favoriteRepository.addFavorite(prefs.loggedInUserId, product.id)
                        }
                    }
                }
            }
        }
    }

    // Hàm onAddToCart không cần tham số product vì nó lấy từ state hiện tại
    fun onAddToCart() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is ProductDetailUiState.Success) {
                val product = currentState.product
                userRepository.authPreferencesFlow.firstOrNull()?.let { prefs ->
                    if (prefs.isLoggedIn) {
                        cartRepository.addProductToCart(product, prefs.loggedInUserId)
                    }
                }
            }
        }
    }
}
