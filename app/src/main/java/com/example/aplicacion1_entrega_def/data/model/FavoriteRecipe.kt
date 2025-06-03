package com.example.aplicacion1_entrega_def.data.model

data class FavoriteRecipe(
    val id: String = "",
    val recipeId: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
) 