package com.example.appmovilsiivmex.ui.screens.verifycode


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.domain.usecase.VerifyEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val verifyEmailUseCase: VerifyEmailUseCase
) : ViewModel() {

    companion object {
        private const val RESEND_INTERVAL_SECONDS = 300
    }

    private val _uiState = MutableStateFlow(
        VerifyCodeUiState(resendSeconds = RESEND_INTERVAL_SECONDS)
    )
    val uiState: StateFlow<VerifyCodeUiState> = _uiState.asStateFlow()

    init {
        startResendCountDown()
    }

    private fun startResendCountDown() {

        viewModelScope.launch {
            _uiState.update { it.copy(resendSeconds = RESEND_INTERVAL_SECONDS) }

            while (_uiState.value.resendSeconds > 0) {
                delay(1000L) // 1 segundo
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
        // Solo permitir reenviar cuando llegue a 0
        if (_uiState.value.resendSeconds != 0) return

        viewModelScope.launch {

            val result = ApiClient.reenviarCorreo(email)
            result.fold(

                onSuccess = { reenvio->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Emplear error en caso de que no se pueda
                        )
                    }
                    Log.d("VERIFY_CODE", "Reenvio exitoso: ${reenvio.message}")
                    startResendCountDown()
                },
                onFailure = { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Emplear error en caso de que no se pueda
                        )
                    }

                    Log.d("VERIFY_CODE", "Al reenviar el código tenemos el error: ${error.message}")

                }
            )
        }
    }

    fun verify(email: String, onSuccess: () -> Unit) {

        viewModelScope.launch {

            // Hacer una validación si es de registro o de reestablecer contraseña

            val code = _uiState.value.code
            _uiState.update { it.copy(isLoading = true) }

            val result = verifyEmailUseCase(
                email = email,
                code = code
            )

            Log.d("VERIFY_CODE", "El resultado es: ${result}")
            Log.d("VERIFY_CODE", "El correo es: ${email}")

            result.fold(
                onSuccess = {

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    onSuccess()
                },
                onFailure = { error ->

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // Emplear error en caso de que no se pueda
                        )
                    }
                }
            )
        }
    }
}
