package com.example.appmovilsiivmex.ui.screens.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RegistroFlowViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    var email: String?
        get() = savedStateHandle["email"]
        private set(value) { savedStateHandle["email"] = value}

    fun updateEmail(nuevoEmail: String){
        email = nuevoEmail
    }

}