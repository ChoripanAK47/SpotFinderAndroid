package com.example.spotfinder

import android.app.Application
import android.os.Bundle
// --- 1. IMPORTACIÓN CAMBIADA ---
// Se borró: import androidx.activity.ComponentActivity
import androidx.fragment.app.FragmentActivity // <-- ESTA ES LA IMPORTACIÓN CORRECTA
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.spotfinder.model.AppDatabase
import com.example.spotfinder.model.Spot
import com.example.spotfinder.repository.SpotRepository
import com.example.spotfinder.repository.UsuarioRepository
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.view.AddSpotScreen
import com.example.spotfinder.view.LoginScreen
import com.example.spotfinder.view.RegisterScreen
import com.example.spotfinder.viewmodel.SpotsViewModel
import com.example.spotfinder.viewmodel.SpotsViewModelFactory
import com.example.spotfinder.viewmodel.UsuarioViewModel
import com.example.spotfinder.viewmodel.UsuarioViewModelFactory
import com.example.spotfinder.view.UserScreen

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete

// --- 2. CLASE CAMBIADA ---
class MainActivity : FragmentActivity() { // <-- ESTA LÍNEA ES LA CORRECCIÓN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpotFinderApp()
                }
            }
        }
    }
}

@Composable
fun SpotFinderApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val database = AppDatabase.getDatabase(application)

    val spotRepository = SpotRepository(database.spotDao())
    val spotsViewModel: SpotsViewModel = viewModel(factory = SpotsViewModelFactory(spotRepository))

    val usuarioRepository = UsuarioRepository(database.usuarioDao())
    val usuarioViewModel: UsuarioViewModel = viewModel(factory = UsuarioViewModelFactory(usuarioRepository))

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, usuarioViewModel = usuarioViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, usuarioViewModel = usuarioViewModel)
        }
        composable("home") {
            HomeScreen(spotsViewModel = spotsViewModel, navController = navController)
        }
        composable("add_spot") {
            AddSpotScreen(spotsViewModel = spotsViewModel, onSpotAdded = {
                navController.popBackStack()
            })
        }
        composable("user_screen") {
            UserScreen(
                usuarioViewModel = usuarioViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(spotsViewModel: SpotsViewModel, navController: NavController) {
    val uiState by spotsViewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .clickable { menuExpanded = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logospotfinder),
                            contentDescription = "Logo SpotFinder",
                            modifier = Modifier.height(32.dp)
                        )
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mi Perfil") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("user_screen")
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.brand_blue)
                ),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_spot") }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Spot")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            SearchBar()
            Spacer(modifier = Modifier.height(16.dp))
            FilterChips()
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                SpotList(spots = uiState.spots, spotsViewModel = spotsViewModel)
            }
        }
    }
}

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it }, // Corregido de onValueValueChange
        label = { Text("Buscar Spot...") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun FilterChips() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { /* TODO */ }) { Text("Todos") }
        Button(onClick = { /* TODO */ }) { Text("Skateparks") }
        Button(onClick = { /* TODO */ }) { Text("Plazas") }
    }
}


@Composable
fun SpotList(spots: List<Spot>, spotsViewModel: SpotsViewModel) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(spots) { spot ->
            SpotCard(spot = spot, viewModel = spotsViewModel)
        }
    }
}

@Composable
fun SpotCard(spot: Spot, viewModel: SpotsViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(
                    model = spot.imageUrl.ifEmpty { R.drawable.spot_image_placeholder }
                ),
                contentDescription = spot.nombreSpot,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = spot.nombreSpot,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.delete(spot) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Spot",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = spot.comunaSpot, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpotFinderTheme {
        // Previewing requires a NavController, which is complex for previews.
    }
}