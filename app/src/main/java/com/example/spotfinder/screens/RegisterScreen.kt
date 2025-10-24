package com.example.spotfinder.screens // Asegúrate que sea tu paquete

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Para hacer scroll si el contenido no cabe
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll // Para hacer scroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// --- ¡Se quitaron las importaciones de Room/Coroutines! ---
import com.example.spotfinder.ui.theme.SpotFinderTheme // Importa tu tema

// Opciones para el desplegable de género
val genderOptions = listOf("Hombre", "Mujer", "Otro")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAccountScreen( // Nombre de la función
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit, // Navegar después de registrarse
    onNavigateToLogin: () -> Unit // Navegar si ya tiene cuenta
) {
    // Estados para los campos del formulario (igual que antes)
    var nombreCompleto by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>(null) } // Empieza sin selección
    var isGenderMenuExpanded by remember { mutableStateOf(false) } // Estado para el menú desplegable
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState() // Para permitir scroll

    // --- Ya no se necesita acceso a la BD aquí ---

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Habilita el scroll vertical
                .padding(horizontal = 32.dp, vertical = 24.dp), // Padding general
            verticalArrangement = Arrangement.Center, // Centra verticalmente el contenido
            horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente el contenido
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), // Bordes redondeados
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White) // Fondo blanco
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear Cuenta", // Título
                        style = MaterialTheme.typography.headlineSmall, // Tamaño del título
                        fontWeight = FontWeight.Bold // Negrita
                    )
                    Spacer(modifier = Modifier.height(24.dp)) // Espacio

                    // Mensaje de error (si hay)
                    error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error, // Color rojo para errores
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // --- Campos del formulario (igual que antes) ---
                    OutlinedTextField(value = nombreCompleto, onValueChange = { nombreCompleto = it }, label = { Text("Nombre Completo:") }, placeholder = { Text("Ingresa tu nombre") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next), singleLine = true, shape = RoundedCornerShape(8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, placeholder = { Text("Ingresa tu email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next), singleLine = true, shape = RoundedCornerShape(8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña:") }, placeholder = { Text("Ingresa tu contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next), singleLine = true, shape = RoundedCornerShape(8.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = passwordConfirm, onValueChange = { passwordConfirm = it }, label = { Text("Confirma contraseña:") }, placeholder = { Text("Confirma tu contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next), singleLine = true, shape = RoundedCornerShape(8.dp))
                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Selector de Género (igual que antes) ---
                    ExposedDropdownMenuBox(
                        expanded = isGenderMenuExpanded,
                        onExpandedChange = { isGenderMenuExpanded = !isGenderMenuExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedGender ?: "Tu Género:",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenderMenuExpanded) },
                            label = { Text("Género:") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isGenderMenuExpanded,
                            onDismissRequest = { isGenderMenuExpanded = false }
                        ) {
                            genderOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedGender = option
                                        isGenderMenuExpanded = false
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                        }
                    } // Fin ExposedDropdownMenuBox
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Botón Registrarse (onClick SIMULADO) ---
                    Button(
                        onClick = {
                            focusManager.clearFocus() // Oculta teclado
                            error = null // Limpia errores previos
                            // Validaciones
                            if (nombreCompleto.isBlank() || email.isBlank() || password.isBlank() || passwordConfirm.isBlank() || selectedGender == null) {
                                error = "Todos los campos son obligatorios."
                            } else if (password != passwordConfirm) {
                                error = "Las contraseñas no coinciden."
                            } else {
                                // --- Lógica de Registro (Simulada) ---
                                // ¡Ya no interactúa con la base de datos!
                                println("Simulando registro: $nombreCompleto, $email, $selectedGender")
                                Toast.makeText(context, "Registro exitoso (simulado)", Toast.LENGTH_SHORT).show()
                                onRegisterSuccess() // Llama a la función para navegar (vuelve al Login)
                                // --- Fin Lógica Simulada ---
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp), // Altura del botón
                        shape = RoundedCornerShape(8.dp) // Bordes redondeados
                    ) {
                        Text("Registrarse", fontWeight = FontWeight.Bold) // Texto en negrita
                    }

                    Spacer(modifier = Modifier.height(24.dp)) // Espacio

                    // --- Texto y Link para Iniciar Sesión (igual que antes) ---
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "¿Ya tienes una cuenta? ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        TextButton(onClick = { onNavigateToLogin() }, contentPadding = PaddingValues(0.dp)) {
                            Text("Inicia sesión")
                        }
                    }
                } // Fin Column interna
            } // Fin Card
        } // Fin Column principal
    } // Fin Box
}


// --- Preview (Asegúrate que el nombre coincida con tu función principal) ---
@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun CreateAccountScreenPreview() { // O RegisterScreenPreview
    SpotFinderTheme {
        CreateAccountScreen(onRegisterSuccess = {}, onNavigateToLogin = {}) // O RegisterScreen
    }
}