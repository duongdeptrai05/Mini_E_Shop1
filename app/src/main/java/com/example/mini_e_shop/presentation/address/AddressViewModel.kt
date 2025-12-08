package com.example.mini_e_shop.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mini_e_shop.data.local.entity.AddressEntity
import com.example.mini_e_shop.data.preferences.UserPreferencesManager
import com.example.mini_e_shop.domain.repository.AddressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressUiState(
    val addresses: List<AddressEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    init {
        loadAddresses()
    }

    private fun loadAddresses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = userPreferencesManager.authPreferencesFlow.first().loggedInUserId
                if (userId != -1) {
                    addressRepository.getAddressesByUserId(userId).collect { addresses ->
                        _uiState.value = _uiState.value.copy(
                            addresses = addresses,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "User not logged in")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun addAddress(name: String, phone: String, address: String, isDefault: Boolean) {
        viewModelScope.launch {
            try {
                val userId = userPreferencesManager.authPreferencesFlow.first().loggedInUserId
                if (userId != -1) {
                    val newAddress = AddressEntity(
                        userId = userId,
                        name = name,
                        phone = phone,
                        address = address,
                        isDefault = isDefault
                    )
                    addressRepository.insertAddress(newAddress)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
    
    fun updateAddress(address: AddressEntity) {
        viewModelScope.launch {
             addressRepository.updateAddress(address)
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            addressRepository.deleteAddress(address)
        }
    }

    fun setDefault(addressId: Int) {
        viewModelScope.launch {
            val userId = userPreferencesManager.authPreferencesFlow.first().loggedInUserId
            if (userId != -1) {
                addressRepository.setDefaultAddress(userId, addressId)
            }
        }
    }
}
