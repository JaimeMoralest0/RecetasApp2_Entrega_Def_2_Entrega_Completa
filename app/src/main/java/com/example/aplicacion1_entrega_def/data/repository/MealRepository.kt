package com.example.aplicacion1_entrega_def.data.repository

import com.example.aplicacion1_entrega_def.data.api.RetrofitClient
import com.example.aplicacion1_entrega_def.data.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealRepository {
    private val mealApi = RetrofitClient.mealApi
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getMeals(): List<Meal> {
        return try {
            val response = mealApi.getMeals()
            response.meals?.map { meal ->
                // Obtener detalles completos de cada comida
                val details = mealApi.getMealById(meal.idMeal)
                details.meals?.firstOrNull() ?: meal
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun searchMealsByIngredient(ingredient: String): List<Meal> {
        return try {
            val response = mealApi.searchMeals(ingredient)
            response.meals ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMealById(id: String): Meal? {
        return try {
            val response = mealApi.getMealById(id)
            response.meals?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addToFavorites(meal: Meal) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val favoritesRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("favorites")
                .document(meal.idMeal)

            val mealMap = mapOf(
                "idMeal" to meal.idMeal,
                "strMeal" to meal.strMeal,
                "strMealThumb" to meal.strMealThumb,
                "strInstructions" to meal.strInstructions,
                "strCategory" to meal.strCategory
            )

            favoritesRef.set(mealMap).await()
        }
    }

    suspend fun removeFromFavorites(meal: Meal) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("favorites")
                .document(meal.idMeal)
                .delete()
                .await()
        }
    }

    suspend fun getFavorites(): List<Meal> {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return try {
                val snapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("favorites")
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        Meal(
                            idMeal = data["idMeal"] as String,
                            strMeal = data["strMeal"] as String,
                            strMealThumb = data["strMealThumb"] as String,
                            strInstructions = data["strInstructions"] as? String,
                            strCategory = data["strCategory"] as? String
                        )
                    } else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
        return emptyList()
    }

    suspend fun addToCompleted(meal: Meal, rating: Int = 0, notes: String = "") {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val completedRef = firestore.collection("users")
                .document(currentUser.uid)
                .collection("completed")
                .document(meal.idMeal)

            val mealMap = mapOf(
                "idMeal" to meal.idMeal,
                "strMeal" to meal.strMeal,
                "strMealThumb" to meal.strMealThumb,
                "strInstructions" to meal.strInstructions,
                "strCategory" to meal.strCategory,
                "rating" to rating,
                "notes" to notes,
                "completedAt" to System.currentTimeMillis()
            )

            completedRef.set(mealMap).await()
        }
    }

    suspend fun removeFromCompleted(meal: Meal) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users")
                .document(currentUser.uid)
                .collection("completed")
                .document(meal.idMeal)
                .delete()
                .await()
        }
    }

    suspend fun getCompleted(): List<Meal> {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return try {
                val snapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("completed")
                    .get()
                    .await()

                snapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    if (data != null) {
                        Meal(
                            idMeal = data["idMeal"] as String,
                            strMeal = data["strMeal"] as String,
                            strMealThumb = data["strMealThumb"] as String,
                            strInstructions = data["strInstructions"] as? String,
                            strCategory = data["strCategory"] as? String
                        )
                    } else null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
        return emptyList()
    }

    suspend fun isCompleted(meal: Meal): Boolean {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            return try {
                val doc = firestore.collection("users")
                    .document(currentUser.uid)
                    .collection("completed")
                    .document(meal.idMeal)
                    .get()
                    .await()
                doc.exists()
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
        return false
    }
} 