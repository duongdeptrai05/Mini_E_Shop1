package com.example.mini_e_shop.data.local.dao

import androidx.room.*
import com.example.mini_e_shop.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses WHERE userId = :userId")
    fun getAddressesByUserId(userId: Int): Flow<List<AddressEntity>>

    @Query("SELECT * FROM addresses WHERE id = :id")
    suspend fun getAddressById(id: Int): AddressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)
    
    @Query("UPDATE addresses SET isDefault = 0 WHERE userId = :userId")
    suspend fun clearDefaultAddress(userId: Int)
}
