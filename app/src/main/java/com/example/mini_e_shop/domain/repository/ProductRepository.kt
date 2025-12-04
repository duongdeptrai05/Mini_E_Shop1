package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.domain.model.Product
import kotlinx.coroutines.flow.Flow
interface ProductRepository {
    fun getAllProducts(): Flow<List<Product>>
    suspend fun deleteProduct(product: Product)
    suspend fun upsertProduct(product: Product)
    suspend fun getProductById(id: Int): Product?
}
