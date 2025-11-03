package com.example.appmovilsiivmex.ui.screens.forgotpassword

data class ForgotPasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
)
