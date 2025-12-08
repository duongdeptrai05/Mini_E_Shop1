package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun getFavoriteProducts(userId: Int): Flow<List<Product>>

    suspend fun addFavorite(userId: Int, productId: Int)

    suspend fun removeFavorite(userId: Int, productId: Int)

    fun isFavorite(userId: Int, productId: Int): Flow<Boolean>
    
    fun getFavoriteProductIds(userId: Int): Flow<Set<Int>>

}
