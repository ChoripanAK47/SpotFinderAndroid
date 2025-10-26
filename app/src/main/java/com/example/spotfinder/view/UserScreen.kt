package com.example.spotfinder.view

// --- Importaciones (sin cambios) ---
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.viewmodel.UpdateProfileState
import com.example.spotfinder.viewmodel.UsuarioViewModel
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

// --- ¡NUEVAS IMPORTACIONES NECESARIAS! ---
// import androidx.compose.material.icons.automirrored.filled.Logout // <-- ¡YA NO SE USA!
import androidx.compose.material.icons.filled.Public // (Por si usas el icono genérico)
import androidx.compose.ui.res.painterResource
import com.example.spotfinder.R // Asegúrate de tener tu logo en R.drawable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit,
    usuarioViewModel: UsuarioViewModel
) {
    // --- (Estados y LaunchedEffect sin cambios) ---
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

    LaunchedEffect(updateState) {
        when (val currentState = updateState) {
            is UpdateProfileState.Success -> {
                Toast.makeText(context, "Nombre actualizado con éxito", Toast.LENGTH_SHORT).show()
                usuarioViewModel.resetUpdateProfileState()
                isEditing = false
            }
            is UpdateProfileState.Error -> {
                Toast.makeText(context, currentState.message, Toast.LENGTH_SHORT).show()
                usuarioViewModel.resetUpdateProfileState()
            }
            else -> Unit
        }
    }

    // --- SCAFFOLD (Modificado) ---
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

                // --- ¡BLOQUE "ACTIONS" ELIMINADO! ---

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Color de fondo
                    titleContentColor = MaterialTheme.colorScheme.onPrimary, // Color del título
                )
            )
        }
    ) { paddingValues ->

        // --- TU COLUMN ORIGINAL ---
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues) // <-- Se aplica el padding
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- (Imagen de Perfil) ---
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

            // --- (Bloque de nombre) ---
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

            // --- (Resto de la pantalla) ---
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

            // --- ESTE BOTÓN AHORA ES TU ÚNICO LOGOUT ---
            OutlinedButton(
                onClick = {
                    usuarioViewModel.logout()
                    onLogout()
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("Cerrar sesión")
            }
            Spacer(modifier = Modifier.height(16.dp))

        } // Fin Column principal
    } // Fin Scaffold
}

// --- (InfoRow y Preview sin cambios) ---
@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    SpotFinderTheme {
        //Text("Preview de UserScreen (requiere ViewModel)")
    }
}