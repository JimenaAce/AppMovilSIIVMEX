package com.example.appmovilsiivmex.ui.screens.verifycode

data class VerifyCodeUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val resendSeconds: Int = 0,
    val verifyCode: Boolean = false,
    val verifyCodeSuccess: Boolean = false,

    // Errores
    val verifyCodeError: String? = null
)
