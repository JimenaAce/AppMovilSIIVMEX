package com.example.appmovilsiivmex.ui.screens.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.domain.usecase.RegisterUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidateEmailUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
): ViewModel() {

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
    fun onConfirmChange(confirm: String){
        _uiState.update { it.copy(confirm = confirm, confirmError = null) }
        validateForm()
    }

    fun onTogglePasswordVisibility1() {
        _uiState.update { it.copy(showPassword1 = !it.showPassword1) }
    }
    fun onTogglePasswordVisibility2(){
        _uiState.update { it.copy(showPassword2 = !it.showPassword2) }
    }

    private fun validateForm() {
        val email = _uiState.value.email
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirm

        val isEmailValid = validateEmailUseCase(email)
        val (isPasswordValid, passwordMessage) = validatePasswordUseCase(password)
        val isConfirmPassword = password == confirmPassword

        _uiState.update {
            it.copy(
                isRegisterEnabled = isEmailValid && isPasswordValid && isConfirmPassword,
                emailError = if (!isEmailValid && email.isNotBlank()) "Correo inválido" else null,
                passwordError = if (!isPasswordValid && password.isNotBlank()) passwordMessage else null,
                confirmError = if(!isConfirmPassword && confirmPassword.isNotBlank()) "Las contraseñas no coinciden" else null,
            )
        }
    }

    fun onRegisterClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true)}

            try{
                val result = registerUseCase(
                    name = _uiState.value.name,
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )

                result.fold(

                    onSuccess = {user ->

                        Log.d("Registro", "El usuario es: $user")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registerSuccess = true
                            )
                        }
                    },
                    onFailure = {error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registerError = error.message
                            )
                        }
                    }
                )



            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registerError = "Error de conexión: ${e.message}"
                    )
                }
            }
        }
    }
}
