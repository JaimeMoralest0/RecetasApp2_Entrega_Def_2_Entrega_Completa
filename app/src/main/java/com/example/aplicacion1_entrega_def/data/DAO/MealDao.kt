package com.example.aplicacion1_entrega_def.data.DAO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.aplicacion1_entrega_def.data.model.Meal

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE idMeal = :id")
    suspend fun getMealById(id: String): Meal?

    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<Meal>

    @Delete
    suspend fun deleteMeal(meal: Meal)
}