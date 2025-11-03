package com.example.appmovilsiivmex.ui.screens.login

data class LoginUiState(

    val email: String = "",
    val password: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val isLoginEnabled: Boolean = false,
    val loginSuccess: Boolean = false,

    // Errores
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginError: String? = null,
)