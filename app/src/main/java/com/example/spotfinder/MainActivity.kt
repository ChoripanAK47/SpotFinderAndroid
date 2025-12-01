package com.example.spotfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.ui.theme.BrandBlue
import com.example.spotfinder.ui.theme.BrandOrange
import com.example.spotfinder.util.SessionManager
import com.example.spotfinder.view.*
import com.example.spotfinder.viewmodel.SpotsViewModel
import com.example.spotfinder.viewmodel.UsuarioViewModel
import java.io.File

// Helper para convertir posibles valores nulos a String seguro para Text composable
private fun safeStr(value: Any?): String = value?.toString() ?: ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Manejo global de excepciones para capturar crash y volcar stacktrace a archivo
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val file = File(filesDir, "last_crash.log")
                file.writeText("Thread: ${thread.name}\n")
                file.appendText("${throwable.stackTraceToString()}")
            } catch (e: Exception) {
                // Si falla la escritura, al menos lo mostramos en logcat
                android.util.Log.e("GlobalCrashHandler", "Failed to write crash file", e)
            }
            // También imprimir en logcat para captura por adb logcat
            android.util.Log.e("GlobalCrashHandler", "Uncaught exception in thread ${thread.name}", throwable)
            // Re-lanzar la excepción original para que el proceso termine como normalmente
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(2)
        }
        setContent {
            SpotFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph()
                }
            }
        }
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Crear repositorios y factories para inyectarlos en los ViewModels
    val userRepository = remember { com.example.spotfinder.repository.UserRepository() }
    val spotsRepository = remember { com.example.spotfinder.repository.SpotsRepository() }

    val usuarioViewModel: UsuarioViewModel = viewModel(factory = com.example.spotfinder.viewmodel.UsuarioViewModelFactory(userRepository))
    val spotsViewModel: SpotsViewModel = viewModel(factory = com.example.spotfinder.viewmodel.SpotsViewModelFactory(spotsRepository))
    val context = LocalContext.current
    val sessionManager = SessionManager(context.applicationContext)
    val startDestination = if (sessionManager.isLoggedIn()) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, usuarioViewModel = usuarioViewModel, sessionManager = sessionManager)
        }
        composable("register") {
            RegisterScreen(navController = navController, usuarioViewModel = usuarioViewModel)
        }
        composable("home") {
            HomeScreen(spotsViewModel = spotsViewModel, navController = navController, sessionManager = sessionManager)
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
                    sessionManager.setLoggedIn(false)
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("map_screen") {
            MapScreen(navController = navController)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(spotsViewModel: SpotsViewModel, navController: NavController, sessionManager: SessionManager) {
    val uiState by spotsViewModel.uiState.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.clickable { menuExpanded = true }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logospotfinder),
                            contentDescription = "Logo SpotFinder",
                            modifier = Modifier.height(36.dp)
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
                            DropdownMenuItem(
                                text = { Text("Mapa de Spots") },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate("map_screen")
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBlue
                ),
                actions = {
                    IconButton(onClick = {
                        sessionManager.setLoggedIn(false)
                        sessionManager.setToken(null)
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add_spot") },
                containerColor = BrandOrange
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Spot", tint = Color.White)
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
                    CircularProgressIndicator(color = BrandBlue)
                }
            } else if (uiState.error != null){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!)
                }
            }
            else {
                // Adaptive layout: grid on wide screens, list on narrow
                val configuration = LocalConfiguration.current
                val screenWidth = configuration.screenWidthDp
                if (screenWidth > 600) {
                    // grid with 2 columns for tablets/large screens
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        gridItems(uiState.spots) { spot ->
                            SpotCardGrid(spot = spot, viewModel = spotsViewModel)
                        }
                    }
                } else {
                    SpotList(spots = uiState.spots, spotsViewModel = spotsViewModel)
                }
            }
        }
    }
}

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
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
        items(items = spots) { spot ->
            SpotCard(spot = spot, viewModel = spotsViewModel)
        }
    }
}

@Composable
fun SpotCard(spot: Spot, viewModel: SpotsViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AsyncImage(
                model = spot.imageUrl?.takeIf { it.isNotBlank() } ?: R.drawable.spot_image_placeholder,
                contentDescription = safeStr(spot.name),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = safeStr(spot.name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    IconButton(onClick = { spot.id?.let { viewModel.deleteSpot(it) } }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Spot",
                            tint = BrandBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = safeStr(spot.description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Placeholder for meta data (rating / location)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // El backend/DB actual no expone 'comuna' en el modelo Spot local.
                    // Mostrar solo coordenadas (latitude, longitude)
                    Text(text = "", style = MaterialTheme.typography.labelSmall)
                    Text(text = safeStr(spot.latitude) + ", " + safeStr(spot.longitude), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun SpotCardGrid(spot: Spot, viewModel: SpotsViewModel) {
    Box(modifier = Modifier.padding(4.dp)) {
        SpotCard(spot = spot, viewModel = viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpotFinderTheme {
        // Previewing requires a NavController, which is complex for previews.
    }
}