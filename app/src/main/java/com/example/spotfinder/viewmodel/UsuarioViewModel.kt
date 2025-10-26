package com.example.spotfinder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spotfinder.model.Usuario
import com.example.spotfinder.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Estados para el Login ---
sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    object Success : LoginState
    data class Error(val message: String) : LoginState
}

// --- Estados para el Registro ---
sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // --- Lógica de Registro ---
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(email: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            if (email.isBlank() || pass.isBlank()) {
                _registerState.value = RegisterState.Error("Email y contraseña son obligatorios.")
                return@launch
            }
            if (pass != confirmPass) {
                _registerState.value = RegisterState.Error("Las contraseñas no coinciden.")
                return@launch
            }

            try {
                val newUser = Usuario(nombre = email, email = email, contrasena = pass, genero = "", aceptaTerminos = true)
                repository.insert(newUser)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                // Captura el error de restricción de email único (y otros posibles errores de DB)
                _registerState.value = RegisterState.Error("El correo electrónico ya está en uso.")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    // --- Lógica de Login ---
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = repository.getUsuario(email, password)
            if (user != null) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error("Credenciales inválidas.")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

// Factory
class UsuarioViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}