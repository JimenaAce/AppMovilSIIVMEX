package com.example.appmovilsiivmex.ui.screens.newpassword

data class CreateNewPasswordUiState(
    val password: String = "",
    val confirm: String = "",
    val showPassword1: Boolean = false,
    val showPassword2: Boolean = false,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)