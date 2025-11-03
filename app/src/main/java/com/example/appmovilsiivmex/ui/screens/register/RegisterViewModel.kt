package com.example.appmovilsiivmex.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
        validateForm()
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
        validateForm()
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
        validateForm()
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    private fun validateForm() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val isEmailValid = email.contains("@") && email.contains(".")
        val isPasswordValid = password.length >= 8

        _uiState.update {
            it.copy(
                emailError = if (!isEmailValid && email.isNotBlank()) "Correo inválido" else null,
                passwordError = if (!isPasswordValid && password.isNotBlank()) "Mínimo 8 caracteres" else null
            )
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: Llama a tu caso de uso de registro
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
