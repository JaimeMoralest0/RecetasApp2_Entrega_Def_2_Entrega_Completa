package com.example.aplicacion1_entrega_def.data.model

data class CompletedRecipe(
    val id: String = "",
    val recipeId: String = "",
    val userId: String = "",
    val completedDate: Long = System.currentTimeMillis(),
    val rating: Int = 0,
    val notes: String = ""
) 