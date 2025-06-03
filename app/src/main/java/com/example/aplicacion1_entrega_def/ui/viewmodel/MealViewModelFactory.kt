package com.example.aplicacion1_entrega_def.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

// Esta clase es para que el viewModel se cree con todas las dependencias necesarias
object MealViewModelFactory {
    val factory = viewModelFactory {
        initializer {
            val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            MealViewModel(application)
        }
    }
}