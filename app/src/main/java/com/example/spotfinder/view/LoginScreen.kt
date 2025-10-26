package com.example.spotfinder.view

import android.widget.Toast
// --- ¡NUEVAS IMPORTACIONES! ---
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import com.example.spotfinder.security.CryptoManager
import java.io.File
import java.io.FileInputStream
// ------------------------------
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.spotfinder.R
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.viewmodel.LoginState
import com.example.spotfinder.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val state by usuarioViewModel.loginState.collectAsState()

    // --- ¡NUEVA LÓGICA DE BIOMETRÍA (LECTURA)! ---
    val cryptoManager = remember { CryptoManager() }
    val biometricFile = remember { File(context.filesDir, "biometric_creds.bin") }
    var biometricEnabled by remember { mutableStateOf(false) }

    // Comprobamos (solo una vez) si el archivo de huella existe
    LaunchedEffect(biometricFile) {
        biometricEnabled = biometricFile.exists()
    }
    // ---------------------------------------------

    // Efecto para reaccionar a los cambios de estado del login (Sin cambios)
    LaunchedEffect(state) {
        when (val currentState = state) {
            is LoginState.Success -> {
                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                usuarioViewModel.resetLoginState()
            }
            is LoginState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                usuarioViewModel.resetLoginState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.brand_blue))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = R.drawable.logospotfinder), contentDescription = "Logo SpotFinder", modifier = Modifier.height(70.dp))
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(5.dp, color = colorResource(id = R.color.brand_gray))
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall, color = colorResource(id = R.color.brand_blue), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = colorResource(id = R.color.brand_blue),
                            unfocusedTextColor = colorResource(id = R.color.brand_blue),
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedLabelColor = colorResource(id = R.color.brand_blue),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = colorResource(id = R.color.brand_blue),
                            unfocusedTextColor = colorResource(id = R.color.brand_blue),
                            focusedContainerColor = Color(0xFFF0F0F0),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedLabelColor = colorResource(id = R.color.brand_blue),
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            usuarioViewModel.login(email, password)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = state !is LoginState.Loading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.brand_orange))
                    ) {
                        if (state is LoginState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Iniciar sesión", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿No tienes una cuenta? ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        TextButton(onClick = { navController.navigate("register") }, contentPadding = PaddingValues(0.dp)) {
                            Text("Regístrate ahora", color = colorResource(id = R.color.brand_orange))
                        }
                    }

                    // --- ¡NUEVO BOTÓN DE HUELLA AÑADIDO! ---
                    if (biometricEnabled) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                try {
                                    // 1. Abrimos el archivo guardado
                                    val fis = FileInputStream(biometricFile)

                                    // 2. Desencriptamos. ESTO DISPARARÁ LA HUELLA
                                    val decryptedBytes = fis.use { cryptoManager.decrypt(it) }

                                    // 3. Convertimos a String
                                    val credentials = String(decryptedBytes, Charsets.UTF_8)

                                    // 4. Separamos "email:password"
                                    val (decryptedEmail, decryptedPassword) = credentials.split(":", limit = 2)

                                    // 5. ¡Hacemos login!
                                    if (decryptedEmail.isNotEmpty() && decryptedPassword.isNotEmpty()) {
                                        usuarioViewModel.login(decryptedEmail, decryptedPassword)
                                    } else {
                                        Toast.makeText(context, "Error al leer credenciales", Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    // Si el usuario cancela o la huella falla, salta un error
                                    Log.e("LoginScreen", "Error de biometría: ${e.message}")
                                    Toast.makeText(context, "Autenticación cancelada o fallida", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Huella",
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar con huella", color = Color.Gray)
                        }
                    }
                    // --- FIN DEL NUEVO BOTÓN ---
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SpotFinderTheme {
        // ... (sin cambios)
    }
}