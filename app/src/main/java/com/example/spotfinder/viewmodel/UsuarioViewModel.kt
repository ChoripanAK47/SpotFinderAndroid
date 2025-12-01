package com.example.spotfinder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spotfinder.data.model.LoginRequest
import com.example.spotfinder.data.model.User
import com.example.spotfinder.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val token: String) : LoginState
    data class Error(val message: String) : LoginState
}

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    object Success : RegisterState
    data class Error(val message: String) : RegisterState
}

sealed interface UpdateProfileState {
    object Idle : UpdateProfileState
    object Loading : UpdateProfileState
    object Success : UpdateProfileState
    data class Error(val message: String) : UpdateProfileState
}

class UsuarioViewModel(private val repository: UserRepository) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState.asStateFlow()

    // Stub for currentUser to satisfy UserScreen
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Stub for updateProfileState
    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState.asStateFlow()

    fun register(username: String, email: String, pass: String, confirmPass: String) {
        // Registration not yet implemented in API/Repository
    }

    fun resetRegisterState() {
        _registerState.value = RegisterState.Idle
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val loginRequest = LoginRequest(email, password)
            try {
                val response = repository.login(loginRequest)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token
                    if (token != null) {
                        _loginState.value = LoginState.Success(token)
                        // Update current user (usar valores por defecto si faltan campos)
                        val id = loginResponse.id ?: 0L
                        val name = loginResponse.name ?: ""
                        val emailResp = loginResponse.email ?: ""
                        if (name.isEmpty() || emailResp.isEmpty()) {
                            Log.w("UsuarioViewModel", "Login response missing user fields: id=$id name='$name' email='$emailResp'")
                        }
                        _currentUser.value = User(
                            id = id,
                            name = name,
                            email = emailResp,
                            token = token
                        )
                    } else {
                        Log.w("UsuarioViewModel", "Login successful but token is null")
                        _loginState.value = LoginState.Error("Error en el login: Token nulo")
                    }
                } else {
                    // Leer el cuerpo de error del servidor para diagnóstico
                    val errorBody = try {
                        response.errorBody()?.string()
                    } catch (e: Exception) {
                        "(no error body)"
                    }
                    Log.e("UsuarioViewModel", "Login failed: code=${response.code()} message=${response.message()} body=$errorBody")
                    val serverMsg = if (!errorBody.isNullOrEmpty()) errorBody else "Credenciales inválidas o error del servidor."
                    _loginState.value = LoginState.Error("${response.code()}: $serverMsg")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Login exception", e)
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    // Stubs for UserScreen
    fun logout() {
        _currentUser.value = null
        _loginState.value = LoginState.Idle
    }

    fun updateUserName(newName: String) {
        // Not implemented
    }

    suspend fun verifyPassword(email: String, pass: String): Boolean {
        // Mock implementation
        return true
    }
}

class UsuarioViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}