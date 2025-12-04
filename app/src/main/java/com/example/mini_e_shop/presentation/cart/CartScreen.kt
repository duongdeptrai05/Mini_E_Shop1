package com.example.mini_e_shop.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mini_e_shop.domain.model.CartItem
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.ui.theme.PrimaryBlue

// A data class to hold the combined details for the UI
data class CartItemDetails(
    val cartItem: CartItem,
    val product: Product
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel) {
    val state by viewModel.cartState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng của bạn", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (state is CartUiState.Success) {
                CheckoutBar(totalPrice = (state as CartUiState.Success).totalPrice) {
                    viewModel.placeOrder()
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF3F4F6))
        ) {
            when (val currentState = state) {
                is CartUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CartUiState.Empty -> {
                    Text("Giỏ hàng của bạn đang trống.", modifier = Modifier.align(Alignment.Center))
                }
                is CartUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(currentState.items, key = { it.cartItem.id }) {
                            CartItemRow(item = it, onQuantityChange = viewModel::onQuantityChange)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItemDetails, onQuantityChange: (Int, Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Giả lập ảnh
            Box(modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("$${item.product.price}", color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(16.dp))
            QuantitySelector(item = item.cartItem, onQuantityChange = { newQuantity -> onQuantityChange(item.cartItem.id, newQuantity) })
        }
    }
}

@Composable
fun QuantitySelector(item: CartItem, onQuantityChange: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onQuantityChange(item.quantity - 1) }) {
            Icon(if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove, contentDescription = "Remove")
        }
        Text("${item.quantity}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
        IconButton(onClick = { onQuantityChange(item.quantity + 1) }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun CheckoutBar(totalPrice: Double, onCheckout: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Tổng cộng", color = Color.Gray)
                Text("$${String.format("%.2f", totalPrice)}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            Button(
                onClick = onCheckout,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                modifier = Modifier.height(50.dp)
            ) {
                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = "Checkout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thanh toán", fontWeight = FontWeight.Bold)
            }
        }
    }
}
