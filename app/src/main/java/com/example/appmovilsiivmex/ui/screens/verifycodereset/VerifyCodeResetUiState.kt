package com.example.appmovilsiivmex.ui.screens.verifycodereset

data class VerifyCodeResetUiState(
    val code: String = "",
    val isLoading: Boolean = false,
    val resendSeconds: Int = 0,
)
