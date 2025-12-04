package com.example.appmovilsiivmex.ui.screens.register


data class RegisterUiState(

    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val isRegisterEnabled: Boolean = false,
    val showPassword1: Boolean = false,
    val showPassword2: Boolean = false,
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,

    // Errores
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val registerError: String? = null
)
