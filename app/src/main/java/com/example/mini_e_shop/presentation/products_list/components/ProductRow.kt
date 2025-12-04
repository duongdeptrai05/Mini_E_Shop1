package com.example.mini_e_shop.presentation.products_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mini_e_shop.domain.model.Product

@Composable
fun ProductRow(product: Product) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = product.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "Brand: ${product.brand}")
        Text(text = "Price: $${product.price}")
    }
}
