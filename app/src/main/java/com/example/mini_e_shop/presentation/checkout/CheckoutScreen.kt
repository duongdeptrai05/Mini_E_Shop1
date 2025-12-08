package com.example.mini_e_shop.presentation.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mini_e_shop.data.local.entity.AddressEntity
import com.example.mini_e_shop.utils.formatPrice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // State to control Address Selection Dialog
    var showAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isOrderPlaced) {
        if (state.isOrderPlaced) {
            onShowSnackbar("Đặt hàng thành công!")
            onNavigateBack()
        }
    }
    
    // Load data only once or when needed.
    // Assuming Item IDs are passed via Navigation arguments and viewmodel handles loading via init block or LaunchedEffect from arguments passed to Screen composable if structured that way.
    // Here we assume ViewModel is already initialized properly or we need to trigger load.
    // However, in MainActivity, we passed cartItemIds in route.
    // The ViewModel needs to parse this. 
    // Since we are using Hilt, the SavedStateHandle in ViewModel can be used to retrieve arguments automatically if defined.
    // Let's assume ViewModel handles it. 
    // Wait, in MainActivity: navController.navigate("${Screen.Checkout.route}/$cartItemIds")
    // And CheckoutScreen receives arguments?
    // We need to pass the IDs to ViewModel manually if not using SavedStateHandle logic inside VM or if we want explicit call.
    // In MainActivity:
    // composable(route = "${Screen.Checkout.route}/{cartItemIds}", ...) { backStackEntry -> 
    //      val cartItemIds = backStackEntry.arguments?.getString("cartItemIds")
    //      CheckoutScreen(...)
    // }
    // We should probably update ViewModel to take IDs or parse from SavedStateHandle.
    // Let's assume for now we need to trigger it here if not already done.
    // But since we can't easily change MainActivity's lambda to pass IDs into Screen composable without changing signature,
    // let's rely on SavedStateHandle in ViewModel.
    // Let's update ViewModel first to be sure.
    // Actually, I already updated CheckoutViewModel to have `loadCheckoutItems`. It needs to be called.
    // Let's use SavedStateHandle in ViewModel or call it from LaunchedEffect here if we can get the ID.
    // But we don't have the ID here in params.
    // Let's rely on Hilt's SavedStateHandle injection in ViewModel.
    // Since I cannot change ViewModel constructor easily without seeing the file again (I wrote it but didn't add SavedStateHandle),
    // let's re-write ViewModel to include SavedStateHandle and init logic.
    
    if (showAddressDialog) {
        AddressSelectionDialog(
            addresses = state.addresses,
            selectedAddress = state.selectedAddress,
            onDismiss = { showAddressDialog = false },
            onAddressSelected = { 
                viewModel.selectAddress(it)
                showAddressDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Order", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = { 
                    if (state.selectedAddress == null) {
                        Toast.makeText(context, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.placeOrder()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4)
                ),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Confirm Order - ${formatPrice(state.totalPrice)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Address Section
            Text("Địa chỉ giao hàng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddressDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val address = state.selectedAddress
                        if (address != null) {
                            Text("${address.name} | ${address.phone}", fontWeight = FontWeight.Bold)
                            Text(address.address, color = Color.Gray, fontSize = 14.sp)
                        } else {
                            Text("Chọn địa chỉ giao hàng", color = Color.Gray)
                        }
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.scale(-1f, 1f), tint = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Text("Items in your order:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            state.cartItems.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${item.product.name} (x${item.cartItem.quantity})",
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        formatPrice(item.product.price * item.cartItem.quantity),
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
fun AddressSelectionDialog(
    addresses: List<AddressEntity>,
    selectedAddress: AddressEntity?,
    onDismiss: () -> Unit,
    onAddressSelected: (AddressEntity) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn địa chỉ", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(addresses) { address ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAddressSelected(address) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (address.id == selectedAddress?.id),
                            onClick = { onAddressSelected(address) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(address.name, fontWeight = FontWeight.Bold)
                            Text(address.address, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
                if (addresses.isEmpty()) {
                    item {
                        Text("Chưa có địa chỉ nào. Vui lòng thêm trong phần Cá nhân.", color = Color.Red)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}
