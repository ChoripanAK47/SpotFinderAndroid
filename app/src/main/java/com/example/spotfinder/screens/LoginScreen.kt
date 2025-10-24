package com.example.spotfinder.screens

import android.widget.Toast
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spotfinder.ui.theme.SpotFinderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Iniciar sesión",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Accede con tu cuenta o crea una nueva para comenzar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(
                        modifier = Modifier.width(60.dp),
                        thickness = 1.dp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    error?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF0F0F0), focusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF0F0F0), focusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            error = null
                            if (email.isBlank() || password.isBlank()) {
                                error = "Email y contraseña son requeridos."
                            } else if (email == "test@test.com" && password == "123456") {
                                Toast.makeText(context, "Inicio de sesión exitoso (simulado)", Toast.LENGTH_SHORT).show()
                                // **** ¡LLAMA A LA LAMBDA CUANDO EL LOGIN SEA EXITOSO! ****
                                onLoginSuccess()
                            } else {
                                error = "Credenciales inválidas (simulado)."
                            }
                        },
                        /* ... (resto del Button sin cambios) ... */
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC16200))
                    ) {
                        Text("Iniciar sesión", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Al iniciar sesión, aceptas los Términos del Servicio y la Política de Privacidad.",
                        style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "¿No tienes una cuenta? ", style = MaterialTheme.typography.bodySmall, color = Color.Gray
                        )
                        TextButton(
                            // **** ¡LLAMA A LA LAMBDA AL HACER CLIC! ****
                            onClick = { onNavigateToRegister() },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Regístrate ahora")
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun LoginScreenPreview() {
    SpotFinderTheme {
        // **** ¡LLAMA A LoginScreen CON LAMBDAS VACÍAS PARA EL PREVIEW! ****
        LoginScreen(onLoginSuccess = {}, onNavigateToRegister = {})
    }
}