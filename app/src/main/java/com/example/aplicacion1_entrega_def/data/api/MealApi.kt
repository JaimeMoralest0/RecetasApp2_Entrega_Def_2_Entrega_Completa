package com.example.aplicacion1_entrega_def.data.api

import com.example.aplicacion1_entrega_def.data.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MealApi {
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealResponse

    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse

    @GET("filter.php")
    suspend fun getMeals(@Query("c") category: String = "Beef"): MealResponse
} 