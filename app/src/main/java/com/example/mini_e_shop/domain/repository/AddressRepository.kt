package com.example.mini_e_shop.domain.repository

import com.example.mini_e_shop.data.local.entity.AddressEntity
import kotlinx.coroutines.flow.Flow

interface AddressRepository {
    fun getAddressesByUserId(userId: Int): Flow<List<AddressEntity>>
    suspend fun getAddressById(id: Int): AddressEntity?
    suspend fun insertAddress(address: AddressEntity)
    suspend fun updateAddress(address: AddressEntity)
    suspend fun deleteAddress(address: AddressEntity)
    suspend fun setDefaultAddress(userId: Int, addressId: Int)
}
