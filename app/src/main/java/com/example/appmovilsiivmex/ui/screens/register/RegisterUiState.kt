package com.example.appmovilsiivmex.ui.screens.register


data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,

    // Errores
    val emailError: String? = null,
    val passwordError: String? = null
)
