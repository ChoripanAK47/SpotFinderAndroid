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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.spotfinder.viewmodel.SpotsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSpotScreen(spotsViewModel: SpotsViewModel, onSpotAdded: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Añadir Spot", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

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
                // Aquí se puede llamar a spotsViewModel para persistir el spot si el ViewModel
                // expone un método apropiado. Para evitar firmas desconocidas el botón
                // simplemente dispara la navegación de regreso via onSpotAdded.
                onSpotAdded()
            }) {
                Text("Agregar")
            }
        }
    }
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
