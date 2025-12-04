package com.example.appmovilsiivmex.ui.screens.verifycodereset


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.domain.usecase.ResendEmailResetUseCase
import com.example.appmovilsiivmex.domain.usecase.VerifyEmailResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeResetViewModel @Inject constructor(
    private val verifyEmailResetUseCase: VerifyEmailResetUseCase,
    private val resendEmailResetUseCase: ResendEmailResetUseCase
) : ViewModel() {

    companion object {
        private const val RESEND_INTERVAL_SECONDS = 300
    }

    private val _uiState = MutableStateFlow(
        VerifyCodeResetUiState(resendSeconds = RESEND_INTERVAL_SECONDS)
    )
    val uiState: StateFlow<VerifyCodeResetUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        startResendCountDown()
    }

    private fun startResendCountDown() {
        // Cancelar el job anterior si existe
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            _uiState.update { it.copy(resendSeconds = RESEND_INTERVAL_SECONDS) }

            while (_uiState.value.resendSeconds > 0) {
                delay(1000L)
                _uiState.update { state ->
                    state.copy(resendSeconds = state.resendSeconds - 1)
                }
            }
        }
    }

    fun onCodeChange(code: String) {
        _uiState.update { it.copy(code = code) }
    }

    fun resendCode(email: String) {
        if (_uiState.value.resendSeconds != 0) return

        viewModelScope.launch {

            val result = resendEmailResetUseCase(email)
            result.fold(
                onSuccess = { reenvio ->
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    Log.d("VERIFY_CODE_RESET", "Reenvio exitoso: ${reenvio}")
                    startResendCountDown()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    Log.d("VERIFY_CODE_RESET", "Al reenviar el código tenemos el error: ${error.message}")
                }
            )
        }
    }

    fun verify(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val code = _uiState.value.code
            _uiState.update { it.copy(isLoading = true) }

            val result = verifyEmailResetUseCase(
                email = email,
                code = code
            )

            Log.d("VERIFY_CODE_RESET", "El resultado es: ${result}")
            Log.d("VERIFY_CODE_RESET", "El correo es: ${email}")

            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                    Log.d("VERIFY_CODE_RESET", "Error en verificación: ${error.message}")
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}