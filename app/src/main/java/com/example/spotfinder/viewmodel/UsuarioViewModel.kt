package com.example.spotfinder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spotfinder.model.Usuario
import com.example.spotfinder.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Estados para el Login (Sin cambios) ---
sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    object Success : LoginState
    data class Error(val message: String) : LoginState
}

// --- Estados para el Registro (Sin cambios) ---
sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}

// --- Estados para Actualizar Perfil (Sin cambios) ---
sealed interface UpdateProfileState {
    object Idle : UpdateProfileState
    object Loading : UpdateProfileState
    object Success : UpdateProfileState
    data class Error(val message: String) : UpdateProfileState
}

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // --- Lógica de Registro (Sin cambios) ---
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    fun register(username: String, email: String, pass: String, confirmPass: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading

            // Validación (Sin cambios)
            if (username.isBlank() || email.isBlank() || pass.isBlank()) {
                _registerState.value = RegisterState.Error("Todos los campos son obligatorios.")
                return@launch
            }
            if (pass != confirmPass) {
                _registerState.value = RegisterState.Error("Las contraseñas no coinciden.")
                return@launch
            }

            try {
                // Creación de usuario (Sin cambios)
                val newUser = Usuario(nombre = username, email = email, contrasena = pass, genero = "", aceptaTerminos = true)
                repository.insert(newUser)
                _registerState.value = RegisterState.Success
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("El correo electrónico ya está en uso.")
            }
        }
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    // --- Lógica de Login (Sin cambios) ---
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val user = repository.getUsuario(email, password)
            if (user != null) {
                _currentUser.value = user
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error("Credenciales inválidas.")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    // --- Función para cerrar sesión (Sin cambios) ---
    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Idle
    }

    // --- Lógica de Actualización de Perfil (Sin cambios) ---
    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState.asStateFlow()

    fun updateUserName(newName: String) {
        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            val currentUser = _currentUser.value

            if (currentUser == null || currentUser.nombre == newName || newName.isBlank()) {
                _updateProfileState.value = UpdateProfileState.Idle
                return@launch
            }

            try {
                val updatedUser = currentUser.copy(nombre = newName)
                repository.update(updatedUser)
                _currentUser.value = updatedUser
                _updateProfileState.value = UpdateProfileState.Success
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al actualizar el nombre: ${e.message}")
                _updateProfileState.value = UpdateProfileState.Error("Error al actualizar el nombre.")
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }

    // -----------------------------------------------------------------
    // --- ¡AQUÍ ESTÁ LA NUEVA FUNCIÓN AÑADIDA! (Paso 3) ---
    // -----------------------------------------------------------------

    /**
     * Verifica si un email y contraseña coinciden en la BD.
     * Es 'suspend' para no bloquear el hilo principal.
     */
    suspend fun verifyPassword(email: String, password: String): Boolean {
        // Llama al repositorio (igual que como lo hace la función login)
        val user = repository.getUsuario(email, password)

        // Devuelve 'true' si el usuario existe (contraseña correcta)
        // y 'false' si 'user' es null (contraseña incorrecta).
        return user != null
    }

} // Esta es la llave que cierra tu clase UsuarioViewModel

// Factory (Sin cambios)
class UsuarioViewModelFactory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}