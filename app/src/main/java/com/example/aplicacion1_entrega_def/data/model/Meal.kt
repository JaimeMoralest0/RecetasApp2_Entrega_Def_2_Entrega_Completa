package com.example.aplicacion1_entrega_def.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey val idMeal: String,
    val strMeal: String,
    val strMealThumb: String,
    val strInstructions: String?,
    val strCategory: String?
)

data class MealResponse(
    val meals: List<Meal>?
) 