package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.ProductDao
import com.example.mini_e_shop.data.local.entity.ProductEntity
import com.example.mini_e_shop.domain.model.Product
import com.example.mini_e_shop.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override fun getAllProducts(): Flow<List<Product>> {
        return productDao.getAllProducts().map { listOfEntities ->
            listOfEntities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun upsertProduct(product: Product) {
        productDao.upsertProduct(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product.toEntity())
    }
}

// --- Các hàm Mapper (Chuyển đổi) ---

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

private fun Product.toEntity(): ProductEntity {
    return ProductEntity(
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
