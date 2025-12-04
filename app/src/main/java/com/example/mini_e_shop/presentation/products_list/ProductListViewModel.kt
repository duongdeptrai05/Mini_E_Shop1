package com.example.mini_e_shop.presentation.products_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.CartRepository
import com.example.mini_e_shop.domain.repository.ProductRepository
import com.example.mini_e_shop.presentation.main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductListUiState>(ProductListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getProducts()
    }

    private fun getProducts() {
        productRepository.getAllProducts()
            .onEach { products ->
                _uiState.value = ProductListUiState.Success(products)
            }
            .catch { e ->
                _uiState.value = ProductListUiState.Error(e.message ?: "Đã có lỗi không xác định xảy ra")
            }
            .launchIn(viewModelScope)
    }
    // --- CÁC HÀM XỬ LÝ CHO ADMIN ---
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(product)
            } catch (e: Exception) {
                _uiState.value = ProductListUiState.Error("Xóa sản phẩm thất bại: ${e.message}")
            }
        }
    }
    // --- CÁC HÀM XỬ LÝ CHO USER ---
    fun addToCart(product: Product, mainViewModel: MainViewModel) {
        viewModelScope.launch {
            // Lấy userId từ MainViewModel
            val userId = mainViewModel.currentUser.value?.id
            if (userId != null) {
                cartRepository.addProductToCart(product, userId)
                // TODO: Hiển thị thông báo "Đã thêm vào giỏ hàng"
            } else {
                // TODO: Xử lý trường hợp không tìm thấy user
            }
        }
    }

    // 3. THÊM HÀM toggleFavorite (hiện tại chỉ là logic giả)
    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            // TODO: Triển khai logic cho chức năng Yêu thích
            // Bước 1: Tạo FavoriteEntity và FavoriteDao
            // Bước 2: Cập nhật FavoriteRepository
            // Bước 3: Gọi repository ở đây để thêm/xóa sản phẩm khỏi danh sách yêu thích
            println("Toggled favorite for ${product.name}")
        }
    }
}

sealed class ProductListUiState {
    object Loading : ProductListUiState()
    data class Success(val products: List<Product>) : ProductListUiState()
    data class Error(val message: String) : ProductListUiState()
}
