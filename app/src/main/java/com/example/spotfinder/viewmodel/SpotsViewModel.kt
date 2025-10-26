package com.example.spotfinder.viewmodel

import androidx.lifecycle.*
import com.example.spotfinder.model.Spot
import com.example.spotfinder.repository.SpotRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

// El estado de la UI
data class HomeScreenUiState(
    val spots: List<Spot> = emptyList(),
    val isLoading: Boolean = true // Empezamos en estado de carga
)

// ViewModel que depende de un Repository
class SpotsViewModel(private val repository: SpotRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        // Iniciar la recolección de datos desde el repositorio
        viewModelScope.launch {
            repository.allSpots.collect { spotsList ->
                _uiState.value = HomeScreenUiState(spots = spotsList, isLoading = false)
            }
        }
    }

    // Función para insertar un nuevo spot
    fun insert(spot: Spot) = viewModelScope.launch {
        repository.insert(spot)
    }

    // --- ¡AÑADE ESTA FUNCIÓN! (Paso 3) ---
    fun delete(spot: Spot) = viewModelScope.launch {
        repository.delete(spot)
    }
    // -------------------------------------
}

// ViewModel Factory para poder pasar el Repository al ViewModel
class SpotsViewModelFactory(private val repository: SpotRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpotsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SpotsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}