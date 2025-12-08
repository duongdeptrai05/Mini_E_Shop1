package com.example.mini_e_shop.data.repository

import com.example.mini_e_shop.data.local.dao.AddressDao
import com.example.mini_e_shop.data.local.entity.AddressEntity
import com.example.mini_e_shop.domain.repository.AddressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val addressDao: AddressDao
) : AddressRepository {
    override fun getAddressesByUserId(userId: Int): Flow<List<AddressEntity>> {
        return addressDao.getAddressesByUserId(userId)
    }

    override suspend fun getAddressById(id: Int): AddressEntity? {
        return addressDao.getAddressById(id)
    }

    override suspend fun insertAddress(address: AddressEntity) {
        addressDao.insertAddress(address)
    }

    override suspend fun updateAddress(address: AddressEntity) {
        addressDao.updateAddress(address)
    }

    override suspend fun deleteAddress(address: AddressEntity) {
        addressDao.deleteAddress(address)
    }

    override suspend fun setDefaultAddress(userId: Int, addressId: Int) {
        addressDao.clearDefaultAddress(userId)
        val address = addressDao.getAddressById(addressId)
        if (address != null) {
            addressDao.updateAddress(address.copy(isDefault = true))
        }
    }
}
