package com.example.appmovilsiivmex.ui.screens.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.domain.usecase.ForgotPasswordUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidateEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(

    private val validateEmailUseCase: ValidateEmailUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase

) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null)
        }
        validateForm()
    }

    private fun validateForm(){
        val email = _uiState.value.email
        val isEmailValid = validateEmailUseCase(email)

        _uiState.update {
            it.copy(
                emailError = if (!isEmailValid && email.isNotBlank()) "Correo inválido" else null
            )
        }
    }

    fun onSendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, forgotPasswordError = null) }

            try{

                val result = forgotPasswordUseCase(_uiState.value.email)
                result.fold(

                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forgotPasswordSuccess = true
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                forgotPasswordError = error.message ?: "Error desconocido"
                            )
                        }
                    }
                )

            } catch (e: Exception){

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        forgotPasswordError = "Error de conexión. Intenta nuevamente"

                    )
                }
            }
        }
    }
}
