package com.example.spotfinder.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.spotfinder.model.Spot
import com.example.spotfinder.viewmodel.SpotsViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotScreen(
    spotsViewModel: SpotsViewModel,
    onSpotAdded: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    // Launcher para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri = cameraUri
            }
        }
    )

    // Launcher para el permiso de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newUri = createImageFile(context)
                cameraUri = newUri
                cameraLauncher.launch(newUri)
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Launcher para la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Añadir un Nuevo Spot") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Spot") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = comuna, onValueChange = { comuna = it }, label = { Text("Comuna") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = ubicacion, onValueChange = { ubicacion = it }, label = { Text("Ubicación (Lat, Lon)") }, modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Desde Galería")
                }
                Button(onClick = {
                    val permission = Manifest.permission.CAMERA
                    when (ContextCompat.checkSelfPermission(context, permission)) {
                        PackageManager.PERMISSION_GRANTED -> {
                            val newUri = createImageFile(context)
                            cameraUri = newUri
                            cameraLauncher.launch(newUri)
                        }
                        else -> {
                            permissionLauncher.launch(permission)
                        }
                    }
                }) {
                    Text("Tomar Foto")
                }
            }

            Button(
                onClick = {
                    if (nombre.isNotBlank() && comuna.isNotBlank()) {
                        val newSpot = Spot(
                            nombreSpot = nombre,
                            descripcionSpot = descripcion,
                            comunaSpot = comuna,
                            ubicacionSpot = ubicacion,
                            imageUrl = imageUri?.toString() ?: ""
                        )
                        spotsViewModel.insert(newSpot)
                        onSpotAdded()
                    } else {
                        Toast.makeText(context, "Nombre y comuna son obligatorios", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar Spot")
            }
        }
    }
}

private fun createImageFile(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = context.externalCacheDir
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg",      /* suffix */
        storageDir     /* directory */
    )
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        image
    )
}