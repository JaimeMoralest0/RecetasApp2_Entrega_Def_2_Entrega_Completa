package com.example.aplicacion1_entrega_def.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion1_entrega_def.data.model.Meal
import com.example.aplicacion1_entrega_def.data.repository.MealRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MealRepository()
    private val _meals = MutableStateFlow<List<Meal>>(emptyList())
    val meals: StateFlow<List<Meal>> = _meals.asStateFlow()

    private val _selectedMeal = MutableStateFlow<Meal?>(null)
    val selectedMeal: StateFlow<Meal?> = _selectedMeal.asStateFlow()

    private val _favorites = MutableStateFlow<List<Meal>>(emptyList())
    val favorites: StateFlow<List<Meal>> = _favorites.asStateFlow()

    private val _completed = MutableStateFlow<List<Meal>>(emptyList())
    val completed: StateFlow<List<Meal>> = _completed.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isCompletedMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val isCompletedMap: StateFlow<Map<String, Boolean>> = _isCompletedMap.asStateFlow()

    init {
        loadInitialMeals()
        loadFavorites()
        loadCompleted()
    }

    private fun loadInitialMeals() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val mealsList = repository.getMeals()
                _meals.value = mealsList
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar las recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchMeals(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                if (query.isEmpty()) {
                    loadInitialMeals()
                } else {
                    val searchResults = repository.searchMealsByIngredient(query)
                    _meals.value = searchResults
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al buscar recetas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMealById(id: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val meal = repository.getMealById(id)
                _selectedMeal.value = meal
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar los detalles: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(meal: Meal) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val isFavorite = isFavorite(meal)
                    if (isFavorite) {
                        repository.removeFromFavorites(meal)
                        _toastMessage.value = "Receta eliminada de favoritos"
                    } else {
                        repository.addToFavorites(meal)
                        _toastMessage.value = "Receta añadida a favoritos"
                    }
                    loadFavorites()
                } else {
                    _toastMessage.value = "Debes iniciar sesión para usar favoritos"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al actualizar favoritos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isFavorite(meal: Meal): Boolean {
        return _favorites.value.any { it.idMeal == meal.idMeal }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val favoritesList = repository.getFavorites()
                _favorites.value = favoritesList
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar favoritos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleCompleted(meal: Meal) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser != null) {
                    val isCompleted = repository.isCompleted(meal)
                    if (isCompleted) {
                        repository.removeFromCompleted(meal)
                        _toastMessage.value = "Receta eliminada de completadas"
                        updateCompletedStatus(meal, false)
                    } else {
                        repository.addToCompleted(meal)
                        _toastMessage.value = "Receta marcada como completada"
                        updateCompletedStatus(meal, true)
                    }
                    loadCompleted()
                } else {
                    _toastMessage.value = "Debes iniciar sesión para usar esta función"
                }
            } catch (e: Exception) {
                _toastMessage.value = "Error al actualizar recetas completadas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isCompleted(meal: Meal): Boolean {
        return _isCompletedMap.value[meal.idMeal] ?: false
    }

    private fun updateCompletedStatus(meal: Meal, isCompleted: Boolean) {
        val currentMap = _isCompletedMap.value.toMutableMap()
        currentMap[meal.idMeal] = isCompleted
        _isCompletedMap.value = currentMap
    }

    fun loadCompleted() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val completedList = repository.getCompleted()
                _completed.value = completedList
                // Actualizar el mapa de estado completado
                val completedMap = completedList.associate { it.idMeal to true }
                _isCompletedMap.value = completedMap
            } catch (e: Exception) {
                _toastMessage.value = "Error al cargar recetas completadas: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
