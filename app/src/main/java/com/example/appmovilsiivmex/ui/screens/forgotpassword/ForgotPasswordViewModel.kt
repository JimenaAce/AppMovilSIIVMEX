package com.example.appmovilsiivmex.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(
                email = email,
                emailError = if (email.isNotBlank() && !email.contains("@")) "Correo inválido" else null
            )
        }
    }

    fun sendReset(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: integra tu backend / Firebase:
            // FirebaseAuth.getInstance().sendPasswordResetEmail(_uiState.value.email).await()
            delay(500) // simula la llamada

            _uiState.update { it.copy(isLoading = false) }
            onSuccess()
        }
    }
}
