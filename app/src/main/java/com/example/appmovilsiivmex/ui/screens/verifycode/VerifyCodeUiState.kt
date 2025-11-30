package com.example.appmovilsiivmex.ui.screens.verifycode

data class VerifyCodeUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val resendSeconds: Int = 0,
)
