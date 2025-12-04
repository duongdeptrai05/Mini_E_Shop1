package com.example.mini_e_shop.data.local.dao

import androidx.room.*
import com.example.mini_e_shop.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface ProductDao {

    // 1) Basic SELECT: get all products
    @Query("SELECT * FROM products")
     fun getAllProducts():  Flow<List<ProductEntity>>

    // 2) FILTER + SORT: products by category, sorted by price ascending
    @Query(
        "SELECT * FROM products " +
                "WHERE category = :category " +
                "ORDER BY price ASC"
    )
    suspend fun getProductsByCategorySortedByPrice(category: String): List<ProductEntity>
    // 3) UPDATE: update stock after placing an order
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    suspend fun decreaseStock(productId: Int, quantity: Int)

    // 4) DELETE: remove a product (admin use only)
    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    // 5) INSERT: add sample data or new products
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)
    @Upsert
    suspend fun upsertProduct(product: ProductEntity)
    // Hàm này dùng để lấy một sản phẩm duy nhất dựa trên ID
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): ProductEntity?
}
