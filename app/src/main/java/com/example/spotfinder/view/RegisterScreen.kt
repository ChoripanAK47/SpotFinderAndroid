package com.example.spotfinder.view

import android.widget.Toast
import androidx.compose.foundation.BorderStroke // Importar
import androidx.compose.foundation.Image // Importar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card // Importar
import androidx.compose.material3.CardDefaults // Importar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton // Importar
import androidx.compose.material3.TextField // Importar (reemplaza OutlinedTextField)
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource // Importar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.spotfinder.R
import com.example.spotfinder.viewmodel.RegisterState
import com.example.spotfinder.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, usuarioViewModel: UsuarioViewModel) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val state by usuarioViewModel.registerState.collectAsState()

    LaunchedEffect(state) {
        when (val currentState = state) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                // Volvemos a Login (o puedes navegar a "home" si prefieres)
                navController.popBackStack()
                usuarioViewModel.resetRegisterState()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                usuarioViewModel.resetRegisterState()
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
                .padding(horizontal = 32.dp), // Igual que en Login
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- AÑADIDO: Logo igual que en Login ---
            Image(
                painter = painterResource(id = R.drawable.logospotfinder),
                contentDescription = "Logo SpotFinder",
                modifier = Modifier.height(70.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- AÑADIDO: Card igual que en Login ---
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

                    // --- MODIFICADO: Título dentro de la Card ---
                    Text(
                        "Crear Cuenta",
                        style = MaterialTheme.typography.headlineSmall, // Estilo de Login
                        color = colorResource(id = R.color.brand_blue), // Color de Login
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- Definimos los colores para los TextField (igual que en Login) ---
                    val textFieldColors = TextFieldDefaults.colors(
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

                    // --- MODIFICADO: Usamos TextField en lugar de OutlinedTextField ---
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de Usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldColors,
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            usuarioViewModel.register(username, email, password, confirmPassword)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = state !is RegisterState.Loading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.brand_orange))
                    ) {
                        if (state is RegisterState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Registrarse", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // --- AÑADIDO: Link para volver a Login ---
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Ya tienes una cuenta? ", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        TextButton(onClick = { navController.popBackStack() }, contentPadding = PaddingValues(0.dp)) {
                            Text("Inicia sesión", color = colorResource(id = R.color.brand_orange))
                        }
                    }
                }
            }
        }
    }
}