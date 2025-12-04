package com.example.mini_e_shop.data.local.dao


import androidx.room.Embedded
import androidx.room.Relation
import com.example.mini_e_shop.data.local.entity.CartItemEntity
import com.example.mini_e_shop.data.local.entity.ProductEntity

// Lớp này định nghĩa mối quan hệ giữa CartItem và Product
data class CartItemWithProduct(
    // @Embedded cho phép lồng các trường của CartItemEntity trực tiếp vào đối tượng này
    @Embedded
    val cartItem: CartItemEntity,

    // @Relation thiết lập mối quan hệ 1-1 giữa CartItem và Product
    @Relation(
        parentColumn = "productId", // Khóa ngoại trong CartItemEntity
        entityColumn = "id"         // Khóa chính trong ProductEntity
    )
    val product: ProductEntity
)
