package com.example.mini_e_shop.presentation.add_edit_product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // --- State cho các trường của sản phẩm ---
    // Các StateFlow này sẽ liên kết với các TextField trên giao diện
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _brand = MutableStateFlow("")
    val brand = _brand.asStateFlow()

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _origin = MutableStateFlow("")
    val origin = _origin.asStateFlow()

    private val _price = MutableStateFlow("")
    val price = _price.asStateFlow()

    private val _stock = MutableStateFlow("")
    val stock = _stock.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    val imageUrl = _imageUrl.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    // Kênh sự kiện để thông báo cho UI sau khi lưu thành công
    private val _saveEvent = Channel<Unit>()
    val saveEvent = _saveEvent.receiveAsFlow()

    // Biến để lưu ID của sản phẩm đang được sửa
    private var currentProductId: Int? = null


    init {
        // Lấy 'productId' từ arguments được truyền qua khi điều hướng
        savedStateHandle.get<Int>("productId")?.let { productId ->
            if (productId != -1) { // -1 là giá trị mặc định cho sản phẩm mới
                this.currentProductId = productId
                viewModelScope.launch {
                    // Gọi repository để lấy thông tin sản phẩm dựa trên ID
                    // Cần có hàm getProductById trong ProductRepository
                    productRepository.getProductById(productId)?.also { product ->
                        // Điền thông tin sản phẩm đã có vào các StateFlow
                        _name.value = product.name
                        _brand.value = product.brand
                        _category.value = product.category
                        _origin.value = product.origin
                        _price.value = product.price.toString()
                        _stock.value = product.stock.toString()
                        _imageUrl.value = product.imageUrl
                        _description.value = product.description
                    }
                }
            }
        }
    }

    // --- Các hàm xử lý sự kiện thay đổi từ UI ---
    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onBrandChange(newBrand: String) {
        _brand.value = newBrand
    }

    fun onCategoryChange(newCategory: String) {
        _category.value = newCategory
    }

    fun onOriginChange(newOrigin: String) {
        _origin.value = newOrigin
    }

    fun onPriceChange(newPrice: String) {
        _price.value = newPrice
    }

    fun onStockChange(newStock: String) {
        _stock.value = newStock
    }

    fun onImageUrlChange(newUrl: String) {
        _imageUrl.value = newUrl
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    fun saveProduct() {
        viewModelScope.launch {
            try {
                // Tạo đối tượng Product từ các giá trị hiện tại trong StateFlow
                val productToSave = Product(
                    // Nếu currentProductId là null (sản phẩm mới), id sẽ là 0.
                    // Nếu không (sản phẩm cũ), id sẽ là giá trị đã có.
                    id = currentProductId ?: 0,
                    name = name.value.trim(),
                    brand = brand.value.trim(),
                    category = category.value.trim(),
                    origin = origin.value.trim(),
                    price = price.value.toDoubleOrNull() ?: 0.0,
                    stock = stock.value.toIntOrNull() ?: 0,
                    imageUrl = imageUrl.value.trim(),
                    description = description.value.trim()
                )

                // SỬA LỖI: Gọi hàm upsertProduct duy nhất cho cả 2 trường hợp
                productRepository.upsertProduct(productToSave)

                // Gửi sự kiện lưu thành công để UI có thể điều hướng quay lại
                _saveEvent.send(Unit)

            } catch (e: Exception) {
                // TODO: Xử lý lỗi (ví dụ: người dùng nhập chữ vào ô giá, hoặc để trống tên)
                // và hiển thị thông báo lỗi cho người dùng.
            }
        }
    }
}