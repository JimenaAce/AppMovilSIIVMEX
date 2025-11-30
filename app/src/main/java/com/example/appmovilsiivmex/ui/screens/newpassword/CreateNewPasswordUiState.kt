package com.example.appmovilsiivmex.ui.screens.newpassword

data class CreateNewPasswordUiState(
    val password: String = "",
    val confirm: String = "",
    val isChangeEnabled: Boolean = false,
    val showPassword1: Boolean = false,
    val showPassword2: Boolean = false,
    val isLoading: Boolean = false,
    val changePasswordSuccess: Boolean = false,

    // Error
    val passwordError: String? = null,
    val confirmError: String? = null,
)