package com.example.aplicacion1_entrega_def.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion1_entrega_def.data.model.FavoriteRecipe
import com.example.aplicacion1_entrega_def.data.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {
    private val repository = RecipeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _favorites = MutableStateFlow<List<FavoriteRecipe>>(emptyList())
    val favorites: StateFlow<List<FavoriteRecipe>> = _favorites.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                repository.getFavorites(user.uid).collect { favorites ->
                    _favorites.value = favorites
                }
            }
        }
    }

    fun removeFromFavorites(recipeId: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                repository.removeFromFavorites(recipeId, user.uid)
            }
        }
    }
} 