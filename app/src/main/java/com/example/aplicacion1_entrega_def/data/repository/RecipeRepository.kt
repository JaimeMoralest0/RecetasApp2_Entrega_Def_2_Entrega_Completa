package com.example.aplicacion1_entrega_def.data.repository

import com.example.aplicacion1_entrega_def.data.model.FavoriteRecipe
import com.example.aplicacion1_entrega_def.data.model.CompletedRecipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers

class RecipeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val favoritesCollection = db.collection("favorites")
    private val completedCollection = db.collection("completed")

    suspend fun addToFavorites(recipeId: String, userId: String) {
        val favorite = FavoriteRecipe(
            recipeId = recipeId,
            userId = userId
        )
        favoritesCollection.add(favorite).await()
    }

    suspend fun addToCompleted(recipeId: String, userId: String, rating: Int = 0, notes: String = "") {
        val completed = CompletedRecipe(
            recipeId = recipeId,
            userId = userId,
            rating = rating,
            notes = notes
        )
        completedCollection.add(completed).await()
    }

    fun getFavorites(userId: String): Flow<List<FavoriteRecipe>> = flow {
        val snapshot = favoritesCollection
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
        emit(snapshot.toObjects(FavoriteRecipe::class.java))
    }.flowOn(Dispatchers.IO)

    fun getCompleted(userId: String): Flow<List<CompletedRecipe>> = flow {
        val snapshot = completedCollection
            .whereEqualTo("userId", userId)
            .orderBy("completedDate", Query.Direction.DESCENDING)
            .get()
            .await()
        emit(snapshot.toObjects(CompletedRecipe::class.java))
    }.flowOn(Dispatchers.IO)

    suspend fun removeFromFavorites(recipeId: String, userId: String) {
        val query = favoritesCollection
            .whereEqualTo("recipeId", recipeId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
        
        query.documents.forEach { doc ->
            doc.reference.delete().await()
        }
    }

    suspend fun removeFromCompleted(recipeId: String, userId: String) {
        val query = completedCollection
            .whereEqualTo("recipeId", recipeId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
        
        query.documents.forEach { doc ->
            doc.reference.delete().await()
        }
    }
} 