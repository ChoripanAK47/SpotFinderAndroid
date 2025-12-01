// Kotlin
package com.example.spotfinder.view

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import com.example.spotfinder.R
import com.example.spotfinder.viewmodel.SpotsViewModel
import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.data.model.Spot as SpotEntity
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotScreen(spotsViewModel: SpotsViewModel, onSpotAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    // Launcher para seleccionar imagen desde la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Añadir Spot", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        // Image placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F0F0))
                .clickable {
                    // Abrir selector de imágenes (galería)
                    galleryLauncher.launch("image/*")
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri == null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.spot_image_placeholder),
                        contentDescription = "Placeholder",
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Tocar para añadir imagen", fontSize = 14.sp, color = Color.Gray)
                }
            } else {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text(text = if (selectedImageUri == null) "Seleccionar imagen" else "Cambiar imagen")
            }
            if (selectedImageUri != null) {
                OutlinedButton(onClick = { selectedImageUri = null }) {
                    Text("Eliminar")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = {
                // Construir SpotRequestDto JSON y parts
                val gson = Gson()
                val spotRequest = mapOf(
                    "nombre" to name,
                    "descripcion" to description,
                    "ubicacion" to mapOf("lat" to 0.0, "lng" to 0.0),
                    "comuna" to "",
                    "servicios" to mapOf("tieneBanos" to false, "tieneZonasRecreativas" to false, "tieneComercioCercano" to false)
                )
                val json = gson.toJson(spotRequest)
                val spotBody = json.toRequestBody("application/json".toMediaTypeOrNull())

                var parts: List<MultipartBody.Part>? = null
                if (selectedImageUri != null) {
                    val file = uriToFile(context, selectedImageUri!!)
                    val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("files", file.name, reqFile)
                    parts = listOf(part)
                }

                spotsViewModel.createSpotMultipart(spotBody, parts)
                onSpotAdded()

            }) {
                Text("Agregar")
            }
        }
    }
}

// Helper para convertir URI a File (temporal) usando ContentResolver
fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("Cannot open input stream from URI")
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val file = File(context.cacheDir, "spot_$timeStamp.jpg")
    FileOutputStream(file).use { output ->
        inputStream.copyTo(output)
    }
    return file
}

fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ?: context.filesDir // fallback si no hay external files dir

    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg",       /* suffix */
        storageDir    /* directory */
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        image
    )
}
