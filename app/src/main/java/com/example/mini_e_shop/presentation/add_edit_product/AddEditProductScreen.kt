package com.example.mini_e_shop.presentation.add_edit_product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    viewModel: AddEditProductViewModel = hiltViewModel(),
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    // 1. SỬA LẠI LOGIC: Lắng nghe sự kiện `saveEvent` từ ViewModel.
    //    Khi ViewModel lưu thành công và gửi sự kiện, hàm `onSave()` mới được gọi.
    LaunchedEffect(key1 = true) {
        viewModel.saveEvent.collectLatest {
            onSave() // Chỉ quay lại khi đã lưu xong.
        }
    }

    Scaffold(
        topBar = {
            // Sử dụng TopAppBar để có nút Quay lại và nút Lưu rõ ràng.
            TopAppBar(
                title = { Text("Thêm/Sửa Sản phẩm") },
                navigationIcon = {
                    IconButton(onClick = onBack) { // Gọi onBack khi nhấn nút quay lại.
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // 2. DI CHUYỂN NÚT LƯU LÊN ĐÂY
                    IconButton(onClick = { viewModel.saveProduct() }) {
                        Icon(Icons.Default.Done, contentDescription = "Save Product")
                    }
                }
            )
        }
        // 3. XÓA BỎ floatingActionButton VÌ ĐÃ CÓ NÚT LƯU TRÊN TOPAPPBAR.
    ) { padding ->
        // Lấy tất cả các state từ ViewModel.
        val name by viewModel.name.collectAsState()
        val brand by viewModel.brand.collectAsState()
        val category by viewModel.category.collectAsState()
        val origin by viewModel.origin.collectAsState()
        val price by viewModel.price.collectAsState()
        val stock by viewModel.stock.collectAsState()
        val imageUrl by viewModel.imageUrl.collectAsState()
        val description by viewModel.description.collectAsState()

        // 4. THÊM CÁC TEXTFIELD CÒN LẠI VÀ THÊM CUỘN DỌC.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = viewModel::onNameChange, label = { Text("Tên sản phẩm") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = brand, onValueChange = viewModel::onBrandChange, label = { Text("Thương hiệu") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = category, onValueChange = viewModel::onCategoryChange, label = { Text("Loại sản phẩm") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = origin, onValueChange = viewModel::onOriginChange, label = { Text("Xuất xứ") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = price, onValueChange = viewModel::onPriceChange, label = { Text("Giá") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = stock, onValueChange = viewModel::onStockChange, label = { Text("Số lượng tồn kho") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = imageUrl, onValueChange = viewModel::onImageUrlChange, label = { Text("URL Hình ảnh") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = viewModel::onDescriptionChange, label = { Text("Mô tả") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
        }
    }
}
