package com.example.mini_e_shop.presentation.address

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    viewModel: AddressViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var addressDetail by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm địa chỉ mới", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Liên hệ", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { if (it.all { char -> char.isDigit() }) phone = it },
                label = { Text("Số điện thoại") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Text("Địa chỉ", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            OutlinedTextField(
                value = addressDetail,
                onValueChange = { addressDetail = it },
                label = { Text("Tỉnh/Thành phố, Quận/Huyện, Phường/Xã, Tên đường...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                minLines = 3
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(checked = isDefault, onCheckedChange = { isDefault = it })
                Spacer(modifier = Modifier.width(12.dp))
                Text("Đặt làm địa chỉ mặc định")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && addressDetail.isNotBlank()) {
                        viewModel.addAddress(name, phone, addressDetail, isDefault)
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = name.isNotBlank() && phone.isNotBlank() && addressDetail.isNotBlank()
            ) {
                Text("HOÀN THÀNH", fontWeight = FontWeight.Bold)
            }
        }
    }
}
