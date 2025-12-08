package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.FavoriteDao
import com.example.mini_e_shop.data.local.entity.FavoriteEntity
import com.example.mini_e_shop.data.local.entity.ProductEntity
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getFavoriteProducts(userId: Int): Flow<List<Product>> {
        return favoriteDao.getFavoriteProducts(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFavorite(userId: Int, productId: Int) {
        favoriteDao.addFavorite(FavoriteEntity(userId = userId, productId = productId))
    }

    override suspend fun removeFavorite(userId: Int, productId: Int) {
        favoriteDao.removeFavorite(userId, productId)
    }

    override fun isFavorite(userId: Int, productId: Int): Flow<Boolean> {
        return flow {
            emit(favoriteDao.isFavorite(userId, productId))
        }
    }

    override fun getFavoriteProductIds(userId: Int): Flow<Set<Int>> {
        return favoriteDao.getFavoriteProductIds(userId).map { it.toSet() }
    }
}

// Mapper function
private fun ProductEntity.toDomain(): Product {
    return Product(
        id = this.id,
        name = this.name,
        brand = this.brand,
        category = this.category,
        origin = this.origin,
        price = this.price,
        stock = this.stock,
        imageUrl = this.imageUrl,
        description = this.description
    )
}
