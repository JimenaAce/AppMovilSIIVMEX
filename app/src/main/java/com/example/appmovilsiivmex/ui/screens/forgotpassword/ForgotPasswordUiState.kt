package com.example.appmovilsiivmex.ui.screens.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val forgotPasswordSuccess: Boolean = false,
    val isLoading: Boolean = false,

    // Errores
    val emailError: String? = null,
    val forgotPasswordError: String? = null
)
