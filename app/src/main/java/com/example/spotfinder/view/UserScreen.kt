package com.example.spotfinder.view

// --- (Tus importaciones actuales) ---
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.viewmodel.UpdateProfileState
import com.example.spotfinder.viewmodel.UsuarioViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import com.example.spotfinder.R

// --- (Importaciones de Biometría y Encriptación) ---
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.spotfinder.security.CryptoManager
import java.io.File
import java.io.FileOutputStream

// --- (Importación para Corutina) ---
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    usuarioViewModel: UsuarioViewModel
) {
    // --- (Estados) ---
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    val scrollState = rememberScrollState()

    val currentUser by usuarioViewModel.currentUser.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    val updateState by usuarioViewModel.updateProfileState.collectAsState()

    val context = LocalContext.current
    val activity = LocalContext.current as FragmentActivity
    val scope = rememberCoroutineScope()

    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordToVerify by remember { mutableStateOf("") }

    // --- Check de Biometría ---
    var biometricCheckDone by remember { mutableStateOf(false) }
    var canUseBiometrics by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> canUseBiometrics = true
            else -> canUseBiometrics = false
        }
        biometricCheckDone = true
    }
    // ----------------------------

    LaunchedEffect(updateState) {
        // ... (Tu LaunchedEffect de Update sin cambios)
    }

    if (showPasswordDialog) {
        // ... (Tu AlertDialog de contraseña sin cambios)
        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Confirmar Identidad") },
            text = {
                Column {
                    Text("Por favor, introduce tu contraseña actual para habilitar la biometría.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = passwordToVerify,
                        onValueChange = { passwordToVerify = it },
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val email = currentUser?.email
                        val password = passwordToVerify

                        if (email == null || password.isBlank()) {
                            Toast.makeText(context, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        scope.launch {
                            val isCorrectPassword = usuarioViewModel.verifyPassword(email, password)

                            if (isCorrectPassword) {
                                Toast.makeText(context, "Contraseña correcta. Prepara tu huella...", Toast.LENGTH_SHORT).show()
                                try {
                                    val cryptoManager = CryptoManager()
                                    val file = File(context.filesDir, "biometric_creds.bin")
                                    val fos = FileOutputStream(file)

                                    fos.use {
                                        cryptoManager.encrypt(
                                            bytes = "$email:$password".toByteArray(),
                                            outputStream = it
                                        )
                                    }
                                    Toast.makeText(context, "¡Biometría habilitada con éxito!", Toast.LENGTH_SHORT).show()

                                    showPasswordDialog = false
                                    passwordToVerify = ""

                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al habilitar la huella: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- ¡AQUÍ ESTÁ LA BARRA AZUL DE VUELTA! ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logospotfinder),
                            contentDescription = "Logo SpotFinder",
                            modifier = Modifier.height(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { paddingValues ->

        // --- AHORA TU COLUMN USA EL PADDING DE LA BARRA ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // <-- ¡APLICADO!
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- (Tu UI de Perfil sin cambios) ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Foto de perfil seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Seleccionar foto de perfil",
                        modifier = Modifier.size(70.dp),
                        tint = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isEditing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nuevo nombre") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(textAlign = TextAlign.Center),
                        enabled = updateState !is UpdateProfileState.Loading
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (editedName.isNotBlank()) {
                                usuarioViewModel.updateUserName(editedName)
                            }
                        },
                        enabled = updateState !is UpdateProfileState.Loading
                    ) {
                        if (updateState is UpdateProfileState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Default.Done, contentDescription = "Guardar nombre")
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentUser?.nombre ?: "Nombre Usuario",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        editedName = currentUser?.nombre ?: ""
                        isEditing = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar nombre")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider(modifier = Modifier.width(100.dp), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Usuario Promedio de SpotFinder", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            SuggestionChip(onClick = { /* No action */ }, label = { Text("Activo", fontSize = 12.sp) }, colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE6F4EA)))
            Spacer(modifier = Modifier.height(24.dp))
            Divider(modifier = Modifier.fillMaxWidth(0.8f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                InfoRow(label = "Correo:", value = currentUser?.email ?: "...")
            }
            Spacer(modifier = Modifier.height(32.dp))

            // --- (Botón de Huella) ---
            if (biometricCheckDone && canUseBiometrics) {
                OutlinedButton(
                    onClick = {
                        showPasswordDialog = true
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Habilitar inicio con huella")
                }
                Spacer(modifier = Modifier.height(8.dp))
            } else if (biometricCheckDone && !canUseBiometrics) {
                Text(
                    text = "Inicio con huella no disponible. Asegúrate de tener un sensor y un PIN/huella registrados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- (Botón Cerrar Sesión) ---
            OutlinedButton(
                onClick = {
                    usuarioViewModel.logout()
                    onLogout()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
            Spacer(modifier = Modifier.height(16.dp))

        } // Fin Column principal
    } // Fin Scaffold
}

// --- (InfoRow y Preview sin cambios) ---
@Composable
fun InfoRow(label: String, value: String) { /* ... */ }

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() { /* ... */ }