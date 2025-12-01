package com.example.spotfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.AsyncImage
import com.example.spotfinder.data.model.Spot
import com.example.spotfinder.ui.theme.SpotFinderTheme
import com.example.spotfinder.util.SessionManager
import com.example.spotfinder.view.*
import com.example.spotfinder.viewmodel.SpotsViewModel
import com.example.spotfinder.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    containerColor = colorResource(id = R.color.brand_blue)
                ),
                actions = {
                    IconButton(onClick = {
                        sessionManager.setLoggedIn(false)
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
            } else if (uiState.error != null){
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.error!!)
                }
            }
            else {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = spot.imageUrl?.ifEmpty { R.drawable.spot_image_placeholder } ?: R.drawable.spot_image_placeholder,
                contentDescription = spot.name,
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
                        text = spot.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.deleteSpot(spot.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Spot",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = spot.description, fontSize = 14.sp, color = Color.Gray)
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