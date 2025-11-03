package com.example.appmovilsiivmex.ui.screens.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNewPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNewPasswordUiState())
    val uiState: StateFlow<CreateNewPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(pwd: String) {
        _uiState.update { it.copy(password = pwd) }
        validate()
    }

    fun onConfirmChange(confirm: String) {
        _uiState.update { it.copy(confirm = confirm) }
        validate()
    }

    fun togglePassword1() { _uiState.update { it.copy(showPassword1 = !it.showPassword1) } }
    fun togglePassword2() { _uiState.update { it.copy(showPassword2 = !it.showPassword2) } }

    private fun validate() {
        val s = _uiState.value
        val minLenOk = s.password.length >= 8
        val matchOk  = s.password == s.confirm

        _uiState.update {
            it.copy(
                passwordError = if (s.password.isNotEmpty() && !minLenOk) "Mínimo 8 caracteres" else null,
                confirmError  = if (s.confirm.isNotEmpty() && !matchOk) "Las contraseñas no coinciden" else null,
                isFormValid   = minLenOk && matchOk
            )
        }
    }

    fun submit(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: Llama a tu backend/Firebase para establecer nueva contraseña
            delay(600)

            _uiState.update { it.copy(isLoading = false) }
            if (_uiState.value.isFormValid) onSuccess()
        }
    }
}
