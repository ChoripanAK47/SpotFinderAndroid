package com.example.spotfinder // Tu paquete principal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.* // Importa Layout
import androidx.compose.material3.* // Importa Material 3
import androidx.compose.runtime.* // Importa Runtime
import androidx.compose.ui.Modifier
// Importaciones necesarias para Navegación
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// Importa tus rutas y pantallas (VERIFICA ESTAS RUTAS)
import com.example.spotfinder.navigation.AppScreens
import com.example.spotfinder.screens.LoginScreen
// Asegúrate de importar el nombre correcto de tu pantalla de registro
import com.example.spotfinder.screens.CreateAccountScreen // O CreateAccountScreen
// *** ¡IMPORTA UserScreen! ***
import com.example.spotfinder.screens.UserScreen
import com.example.spotfinder.ui.theme.SpotFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // Establece el contenido de la actividad
            SpotFinderTheme { // Aplica el tema de la app
                Surface( // Contenedor principal
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llama a la función que configura la navegación
                    MinimalAppNavigation()
                }
            }
        }
    }
}

// Composable que configura la navegación MÍNIMA entre Login, Registro y User
@Composable
fun MinimalAppNavigation() {
    // 1. Crea el NavController
    val navController = rememberNavController()

    // 2. Define el NavHost
    NavHost(
        navController = navController,
        startDestination = AppScreens.LOGIN_SCREEN // Empieza en Login
    ) {
        // --- Pantalla de Login ---
        composable(AppScreens.LOGIN_SCREEN) { // Define la ruta para Login
            LoginScreen( // Llama a tu LoginScreen
                modifier = Modifier.fillMaxSize(),
                // **** ¡CAMBIO AQUÍ! ****
                onLoginSuccess = {
                    // Acción de login exitoso: Navegar a UserScreen
                    println("Login Exitoso -> Navegando a UserScreen")
                    navController.navigate(AppScreens.USER_SCREEN) { // <-- ¡Navega a USER_SCREEN!
                        // Limpia el historial para no volver al Login con el botón "atrás"
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true // Evita crear múltiples instancias de UserScreen
                    }
                },
                onNavigateToRegister = {
                    // Navega a la pantalla de registro
                    navController.navigate(AppScreens.CREATE_ACCOUNT_SCREEN) // Usa la ruta correcta
                }
            )
        }

        // --- Pantalla de Registro ---
        composable(AppScreens.CREATE_ACCOUNT_SCREEN) { // Define la ruta para Registro
            // Usa el nombre correcto de tu Composable de registro
            CreateAccountScreen( // O CreateAccountScreen
                modifier = Modifier.fillMaxSize(),
                onRegisterSuccess = {
                    // Después de registrarse, vuelve al Login
                    navController.navigate(AppScreens.LOGIN_SCREEN) {
                        popUpTo(AppScreens.CREATE_ACCOUNT_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    // Vuelve al Login
                    navController.navigate(AppScreens.LOGIN_SCREEN) {
                        popUpTo(AppScreens.CREATE_ACCOUNT_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // --- Pantalla de Perfil de Usuario ---
        composable(AppScreens.USER_SCREEN) { // <-- ¡BLOQUE AÑADIDO PARA USER_SCREEN!
            UserScreen( // Llama a tu UserScreen
                modifier = Modifier.fillMaxSize(),
                // Aquí pasarías datos reales del usuario más adelante
                onLogout = {
                    // Acción para el botón de logout en UserScreen: vuelve al Login
                    navController.navigate(AppScreens.LOGIN_SCREEN) {
                        // Limpia todo el historial hasta el inicio
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

    } // Fin NavHost
}

// Puedes borrar los Previews de MainActivity si quieres