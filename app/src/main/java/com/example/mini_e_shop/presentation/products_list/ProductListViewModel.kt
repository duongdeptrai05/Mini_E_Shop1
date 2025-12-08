package com.example.mini_e_shop.presentation.products_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Define SortType enum as expected by the UI
enum class SortType {
    NONE,
    PRICE_ASC,
    PRICE_DESC,
    NAME_ASC
}

// Define ProductListUiState sealed class as expected by the UI
sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<String>,
        val favoriteStatusMap: Map<Int, Boolean>
    ) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _selectedSortType = MutableStateFlow(SortType.NONE)
    val selectedSortType: StateFlow<SortType> = _selectedSortType.asStateFlow()

    private val _eventChannel = Channel<String>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        observeProductList()
    }

    private fun observeProductList() {
        viewModelScope.launch {
            // Combine multiple flows for reactive UI
            combine(
                productRepository.getAllProducts(),
                userRepository.authPreferencesFlow.flatMapLatest { prefs ->
                    if (prefs.isLoggedIn) favoriteRepository.getFavoriteProductIds(prefs.loggedInUserId)
                    else flowOf(emptySet())
                },
                _searchQuery,
                _selectedCategory,
                _selectedSortType
            ) { allProducts, favoriteIds, query, category, sortType ->
                // Filtering and Sorting logic
                val categories = listOf("Tất cả") + allProducts.map { it.category }.distinct()

                val filteredProducts = allProducts.filter { product ->
                    val matchesCategory = category == null || category == "Tất cả" || product.category == category
                    val matchesQuery = query.isBlank() || product.name.contains(query, ignoreCase = true)
                    matchesCategory && matchesQuery
                }

                val sortedProducts = when (sortType) {
                    SortType.PRICE_ASC -> filteredProducts.sortedBy { it.price }
                    SortType.PRICE_DESC -> filteredProducts.sortedByDescending { it.price }
                    SortType.NAME_ASC -> filteredProducts.sortedBy { it.name }
                    SortType.NONE -> filteredProducts
                }

                val favoriteMap = sortedProducts.associate { it.id to favoriteIds.contains(it.id) }

                ProductListUiState.Success(
                    products = sortedProducts,
                    categories = categories,
                    favoriteStatusMap = favoriteMap
                )
            }.catch { e ->
                _uiState.value = ProductListUiState.Error(e.message ?: "An unknown error occurred")
            }.collect { successState ->
                _uiState.value = successState
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = if (category == "Tất cả") null else category
    }

    fun onSortTypeSelected(sortType: SortType) {
        _selectedSortType.value = sortType
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
            _eventChannel.send("Sản phẩm đã được xóa")
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val userId = userRepository.authPreferencesFlow.first().loggedInUserId
            if (userId != -1) {
                cartRepository.addProductToCart(product, userId)
                _eventChannel.send("${product.name} đã được thêm vào giỏ hàng")
            } else {
                 _eventChannel.send("Vui lòng đăng nhập để thêm sản phẩm")
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            val userId = userRepository.authPreferencesFlow.first().loggedInUserId
            if (userId != -1) {
                val isCurrentlyFavorite = (_uiState.value as? ProductListUiState.Success)
                    ?.favoriteStatusMap?.get(product.id) ?: false

                if (isCurrentlyFavorite) {
                    favoriteRepository.removeFavorite(userId, product.id)
                } else {
                    favoriteRepository.addFavorite(userId, product.id)
                }
            } else {
                 _eventChannel.send("Vui lòng đăng nhập để yêu thích sản phẩm")
            }
        }
    }
}
