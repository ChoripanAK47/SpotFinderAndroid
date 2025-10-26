package com.example.spotfinder.view // Asegúrate que sea tu paquete

// --- Importaciones para UI y Funcionalidad ---
import android.net.Uri // Necesario para la URI de la imagen
import androidx.activity.compose.rememberLauncherForActivityResult // Para lanzar la galería
import androidx.activity.result.contract.ActivityResultContracts // Para obtener contenido
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Para hacer clickeable la imagen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // Importa rememberScrollState
import androidx.compose.foundation.shape.CircleShape // Para el placeholder de imagen
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll // Importa verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person // Icono placeholder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Para recortar en forma de círculo
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale // Para escalar la imagen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- Importación de Coil ---
import coil.compose.AsyncImage // Para cargar la imagen desde la URI
// --- NO SE NECESITAN importaciones de Room/Session/Coroutines ---
import com.example.spotfinder.ui.theme.SpotFinderTheme // Importa tu tema

@Composable
fun UserScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit // Función para navegar al cerrar sesión
) {
    // --- Estado para la URI de la imagen de perfil seleccionada ---
    var imageUri by remember { mutableStateOf<Uri?>(null) } // Guarda la URI de la imagen

    // --- Lanzador para el selector de imágenes de la galería ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // Contrato estándar para obtener contenido
    ) { uri: Uri? -> // Se ejecuta cuando el usuario selecciona una imagen (o cancela)
        imageUri = uri // Actualiza el estado con la URI seleccionada (o null si cancela)
        // Aquí podrías guardar la URI (como String) en SharedPreferences si quieres persistencia
        // saveProfileImageUri(context, uri) // Función auxiliar (ver al final)
    }

    val scrollState = rememberScrollState() // Estado para el scroll

    // --- Columna principal MODIFICADA ---
    // Quitamos el padding vertical general para que la Card pueda tocar los bordes
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Permite scroll vertical
            .padding(horizontal = 16.dp), // Mantenemos padding horizontal
        verticalArrangement = Arrangement.Top, // Empieza desde arriba
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp)) // Espacio arriba opcional

        // --- Card MODIFICADA ---
        Card(
            modifier = Modifier
                .fillMaxWidth() // Ocupa todo el ancho disponible
                .weight(1f), // <-- ¡NUEVO! Hace que la Card ocupe todo el espacio vertical restante
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), // Redondea solo arriba (opcional)
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            // Columna interna AHORA con padding vertical
            Column(
                modifier = Modifier
                    // .fillMaxSize() // No necesita fillMaxSize si la Card ya tiene weight(1f)
                    .padding(horizontal = 24.dp, vertical = 32.dp), // Padding interno
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- Imagen de Perfil (Clickeable con Coil) ---
                Box( // Box para darle forma circular y fondo
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape) // Recorta en círculo
                        .background(Color.LightGray) // Fondo mientras carga o si no hay imagen
                        .clickable { // Hace que la imagen/box sea clickeable
                            // Lanza el selector de imágenes (pide elegir de galería, etc.)
                            imagePickerLauncher.launch("image/*") // Busca solo imágenes
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // Si hay URI, muestra la imagen usando Coil
                        AsyncImage(
                            model = imageUri, // La URI de la imagen a cargar
                            contentDescription = "Foto de perfil seleccionada",
                            modifier = Modifier.fillMaxSize(), // Llena el Box circular
                            contentScale = ContentScale.Crop // Recorta/escala la imagen
                        )
                    } else {
                        // Si no hay URI, muestra el icono placeholder
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Seleccionar foto de perfil",
                            modifier = Modifier.size(70.dp),
                            tint = Color.Gray
                        )
                    }
                } // Fin Box imagen
                Spacer(modifier = Modifier.height(16.dp))

                // --- Nombre (Placeholder) ---
                Text(
                    text = "Nombre Usuario", // Placeholder
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Divider(modifier = Modifier.width(100.dp), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                // Tipo de Usuario y Estado Activo (sin cambios)
                Text(text = "Usuario Promedio de SpotFinder", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                SuggestionChip(onClick = { /* No action */ }, label = { Text("Activo", fontSize = 12.sp) }, colors = SuggestionChipDefaults.suggestionChipColors(containerColor = Color(0xFFE6F4EA)))
                Spacer(modifier = Modifier.height(24.dp))
                Divider(modifier = Modifier.fillMaxWidth(0.8f), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // --- Datos: Correo y Género (Placeholders) ---
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    InfoRow(label = "Correo:", value = "correo@ejemplo.com") // Placeholder
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(label = "Género:", value = "No especificado") // Placeholder
                }
                Spacer(modifier = Modifier.height(32.dp)) // Espacio antes del botón

                // --- Botón Cerrar Sesión (sin SessionManager) ---
                OutlinedButton(
                    onClick = {
                        // Ya no llamamos a sessionManager.clearSession()
                        onLogout() // Llama directamente a la lambda para navegar al Login
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Text("Cerrar sesión")
                }
                Spacer(modifier = Modifier.height(16.dp)) // Espacio extra al final si haces scroll
            } // Fin Column interna
        } // Fin Card
        Spacer(modifier = Modifier.height(16.dp)) // Espacio abajo opcional
    } // Fin Column principal
}

// Composable auxiliar InfoRow (sin cambios)
@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    SpotFinderTheme {
        UserScreen(onLogout = {})
    }
}

// --- Funciones Auxiliares (Opcional) para guardar/cargar URI en SharedPreferences ---
/* // Descomenta y ponlas fuera del @Composable UserScreen si quieres persistencia simple
private fun saveProfileImageUri(context: Context, uri: Uri?) {
    val prefs = context.getSharedPreferences("UserProfilePrefs", Context.MODE_PRIVATE)
    prefs.edit().putString("profileImageUri", uri?.toString()).apply()
}

private fun getProfileImageUri(context: Context): String? {
    val prefs = context.getSharedPreferences("UserProfilePrefs", Context.MODE_PRIVATE)
    return prefs.getString("profileImageUri", null)
}
*/