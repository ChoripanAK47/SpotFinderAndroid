package com.example.spotfinder.viewmodel

import androidx.lifecycle.*
import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.repository.SpotsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

// El estado de la UI
data class HomeScreenUiState(
    val spots: List<Spot> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed interface SpotState {
    object Idle : SpotState
    object Loading : SpotState
    object Success : SpotState
    data class Error(val message: String) : SpotState
}

// ViewModel que depende de un Repository
class SpotsViewModel(private val repository: SpotsRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    private val _spotState = MutableStateFlow<SpotState>(SpotState.Idle)
    val spotState: StateFlow<SpotState> = _spotState.asStateFlow()

    init {
        // Iniciar la recolección de datos desde el repositorio
        fetchSpots()
    }

    fun fetchSpots() {
        _uiState.value = HomeScreenUiState(isLoading = true)
        viewModelScope.launch {
            try {
                val response = repository.getSpots()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = HomeScreenUiState(spots = response.body()!!, isLoading = false)
                } else {
                    _uiState.value = HomeScreenUiState(isLoading = false, error = "Error fetching spots: ${response.message()}")
                }
            } catch (e: IOException) {
                _uiState.value = HomeScreenUiState(isLoading = false, error = "Error fetching spots: ${e.message}")
            }
        }
    }

    fun createSpot(spot: Spot) {
        viewModelScope.launch {
            _spotState.value = SpotState.Loading
            try {
                val response = repository.createSpot(spot)
                if (response.isSuccessful) {
                    _spotState.value = SpotState.Success
                    fetchSpots()
                } else {
                    _spotState.value = SpotState.Error("Error creating spot")
                }
            } catch (e: Exception) {
                _spotState.value = SpotState.Error("Error creating spot: ${e.message}")
            }
        }
    }

    // Nueva sobrecarga para crear spot con multipart (JSON spot + imágenes)
    fun createSpotMultipart(spotJson: okhttp3.RequestBody, files: List<okhttp3.MultipartBody.Part>?) {
        viewModelScope.launch {
            _spotState.value = SpotState.Loading
            try {
                val response = repository.createSpotMultipart(spotJson, files)
                if (response.isSuccessful) {
                    _spotState.value = SpotState.Success
                    fetchSpots()
                } else {
                    _spotState.value = SpotState.Error("Error creating spot: ${response.message()}")
                }
            } catch (e: Exception) {
                _spotState.value = SpotState.Error("Error creating spot: ${e.message}")
            }
        }
    }

    fun deleteSpot(id: Long) {
        // API might not support delete yet, or we need to add it to repository
        // For now, just log or do nothing if not implemented in repo
        /*
        viewModelScope.launch {
            _spotState.value = SpotState.Loading
            try {
                val response = repository.deleteSpot(id)
                if (response.isSuccessful) {
                    _spotState.value = SpotState.Success
                    fetchSpots()
                } else {
                    _spotState.value = SpotState.Error("Error deleting spot")
                }
            } catch (e: Exception) {
                _spotState.value = SpotState.Error("Error deleting spot: ${e.message}")
            }
        }
        */
    }

    fun resetSpotState() {
        _spotState.value = SpotState.Idle
    }
}

// ViewModel Factory para poder pasar el Repository al ViewModel
class SpotsViewModelFactory(private val repository: SpotsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpotsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpotsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}