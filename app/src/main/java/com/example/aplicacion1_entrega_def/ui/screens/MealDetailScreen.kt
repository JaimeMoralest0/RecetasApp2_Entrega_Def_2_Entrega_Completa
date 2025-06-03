package com.example.aplicacion1_entrega_def.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
fun MealDetailScreen(
    navController: NavController,
    viewModel: MealViewModel
) {
    val selectedMeal = viewModel.selectedMeal.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val toastMessage = viewModel.toastMessage.collectAsState().value

    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            kotlinx.coroutines.delay(2000)
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Receta") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    selectedMeal?.let { meal ->
                        // Botón de favoritos
                        IconButton(onClick = { viewModel.toggleFavorite(meal) }) {
                            Icon(
                                imageVector = if (viewModel.isFavorite(meal)) 
                                    Icons.Default.Favorite 
                                else 
                                    Icons.Default.FavoriteBorder,
                                contentDescription = if (viewModel.isFavorite(meal)) 
                                    "Quitar de favoritos" 
                                else 
                                    "Añadir a favoritos",
                                tint = if (viewModel.isFavorite(meal)) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                        // Botón de completar
                        IconButton(onClick = { viewModel.toggleCompleted(meal) }) {
                            Icon(
                                imageVector = if (viewModel.isCompleted(meal)) 
                                    Icons.Default.CheckCircle 
                                else 
                                    Icons.Default.CheckCircle,
                                contentDescription = if (viewModel.isCompleted(meal)) 
                                    "Quitar de completadas" 
                                else 
                                    "Marcar como completada",
                                tint = if (viewModel.isCompleted(meal)) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                selectedMeal?.let { meal ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        AsyncImage(
                            model = meal.strMealThumb,
                            contentDescription = meal.strMeal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = meal.strMeal,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            meal.strCategory?.let { category ->
                                Text(
                                    text = "Categoría: $category",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Instrucciones:",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = meal.strInstructions ?: "",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
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