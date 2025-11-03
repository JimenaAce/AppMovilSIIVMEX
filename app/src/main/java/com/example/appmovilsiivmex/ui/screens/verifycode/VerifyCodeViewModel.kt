package com.example.appmovilsiivmex.ui.screens.verifycode


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyCodeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VerifyCodeUiState())
    val uiState: StateFlow<VerifyCodeUiState> = _uiState.asStateFlow()

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun resendCode() {
        viewModelScope.launch {
            if (_uiState.value.resendSeconds != 0) return@launch
            _uiState.update { it.copy(resendSeconds = 30) }
            while (_uiState.value.resendSeconds > 0) {
                delay(1000)
                _uiState.update { st -> st.copy(resendSeconds = st.resendSeconds - 1) }
            }
        }
    }

    fun verify(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: validar el código con tu backend/Firebase
            delay(500)
            _uiState.update { it.copy(isLoading = false) }
            if (_uiState.value.code.length == 4) onSuccess()
        }
    }
}
