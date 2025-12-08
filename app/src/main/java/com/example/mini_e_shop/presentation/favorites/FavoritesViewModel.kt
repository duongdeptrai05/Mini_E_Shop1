package com.example.mini_e_shop.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FavoritesUiState {
    object Loading : FavoritesUiState()
    data class Success(val favoriteProducts: List<Product>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val userRepository: UserRepository,
    private val cartRepository: CartRepository // Thêm CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    // Channel để gửi sự kiện (vd: hiển thị Toast)
    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            userRepository.authPreferencesFlow.flatMapLatest { prefs ->
                if (prefs.isLoggedIn) {
                    favoriteRepository.getFavoriteProducts(prefs.loggedInUserId)
                } else {
                    flowOf(emptyList())
                }
            }.catch { e ->
                _uiState.value = FavoritesUiState.Error(e.message ?: "An unknown error occurred")
            }.collect { products ->
                _uiState.value = FavoritesUiState.Success(products)
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            userRepository.authPreferencesFlow.firstOrNull()?.let { prefs ->
                if (prefs.isLoggedIn) {
                    favoriteRepository.removeFavorite(prefs.loggedInUserId, product.id)
                }
            }
        }
    }

    // Thêm hàm addToCart
    fun addToCart(product: Product) {
        viewModelScope.launch {
            userRepository.authPreferencesFlow.firstOrNull()?.let { prefs ->
                if (prefs.isLoggedIn) {
                    cartRepository.addProductToCart(product, prefs.loggedInUserId)
                    _eventChannel.send("Đã thêm '${product.name}' vào giỏ hàng")
                } else {
                    _eventChannel.send("Vui lòng đăng nhập để thêm sản phẩm")
                }
            }
        }
    }
}
