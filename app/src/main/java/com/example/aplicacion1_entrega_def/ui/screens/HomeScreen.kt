package com.example.aplicacion1_entrega_def.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.aplicacion1_entrega_def.data.model.Meal
import com.example.aplicacion1_entrega_def.ui.viewmodel.MealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: MealViewModel,
    onLogout: () -> Unit
) {
    val meals by viewModel.meals.collectAsState()
    val searchQuery = remember { mutableStateOf("") }
    val toastMessage by viewModel.toastMessage.collectAsState()

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            // Mostrar el toast y limpiar el mensaje después de un tiempo
            kotlinx.coroutines.delay(2000)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Recetas") },
                    actions = {
                        // Botón de favoritos
                        IconButton(onClick = { navController.navigate("favorites") }) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Ver favoritos"
                            )
                        }
                        // Botón de recetas completadas
                        IconButton(onClick = { navController.navigate("completed") }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Ver recetas completadas"
                            )
                        }
                        // Botón de cerrar sesión
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Cerrar sesión"
                            )
                        }
                    }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery.value,
                        onValueChange = { 
                            searchQuery.value = it
                            viewModel.searchMeals(it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar por ingrediente") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar"
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(meals) { meal ->
                    MealItem(
                        meal = meal,
                        isFavorite = viewModel.isFavorite(meal),
                        isCompleted = viewModel.isCompleted(meal),
                        onMealClick = {
                            viewModel.getMealById(meal.idMeal)
                            navController.navigate("meal_detail/${meal.idMeal}")
                        },
                        onFavoriteClick = {
                            viewModel.toggleFavorite(meal)
                        },
                        onCompletedClick = {
                            viewModel.toggleCompleted(meal)
                        }
                    )
                }
            }

            // Mostrar el toast
            toastMessage?.let { message ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(message)
                }
            }
        }
    }
}

@Composable
fun MealItem(
    meal: Meal,
    isFavorite: Boolean,
    isCompleted: Boolean,
    onMealClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCompletedClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onMealClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            AsyncImage(
                model = meal.strMealThumb,
                contentDescription = meal.strMeal,
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = meal.strMeal,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.strCategory ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(8.dp)
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onCompletedClick) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                        contentDescription = if (isCompleted) "Quitar de completadas" else "Marcar como completada",
                        tint = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
} 