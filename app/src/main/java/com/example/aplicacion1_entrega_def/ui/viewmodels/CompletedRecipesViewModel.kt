package com.example.aplicacion1_entrega_def.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacion1_entrega_def.data.model.CompletedRecipe
import com.example.aplicacion1_entrega_def.data.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CompletedRecipesViewModel : ViewModel() {
    private val repository = RecipeRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _completedRecipes = MutableStateFlow<List<CompletedRecipe>>(emptyList())
    val completedRecipes: StateFlow<List<CompletedRecipe>> = _completedRecipes.asStateFlow()

    init {
        loadCompletedRecipes()
    }

    private fun loadCompletedRecipes() {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                repository.getCompleted(user.uid).collect { completed ->
                    _completedRecipes.value = completed
                }
            }
        }
    }

    fun removeFromCompleted(recipeId: String) {
        viewModelScope.launch {
            auth.currentUser?.let { user ->
                repository.removeFromCompleted(recipeId, user.uid)
            }
        }
    }
} 