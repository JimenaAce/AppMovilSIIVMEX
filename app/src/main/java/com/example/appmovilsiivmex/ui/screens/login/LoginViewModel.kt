package com.example.appmovilsiivmex.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.domain.usecase.LoginUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidateEmailUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Verificar la sesión al iniciar
    init {
        checkExistingSesssion()
    }

    // Función que se encarga de revisar una sesión existente
    private fun checkExistingSesssion(){
        viewModelScope.launch {
            if(sessionManager.isLoggedIn()){
                _uiState.update { it.copy(loginSuccess = true) }
            }
        }

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

        val isEmailValid = validateEmailUseCase(email)
        val isPasswordValid = password.length >= 8

        _uiState.update {
            it.copy(
                isLoginEnabled = isEmailValid && isPasswordValid,
                emailError = if (!isEmailValid && email.isNotBlank()) "Correo inválido" else null,
                passwordError = if (!isPasswordValid && password.isNotBlank()) "Mínimo 8 caracteres" else null
            )
        }


    }

    fun onLoginClick() {
        if (!_uiState.value.isLoginEnabled) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, loginError = null) }

            try {
                val result = loginUseCase(
                    email = _uiState.value.email,
                    password = _uiState.value.password
                )


                result.fold(
                    onSuccess = { loginResult->


                        val selectedId = loginResult.vehicles.firstOrNull()?.id

                        sessionManager.saveSession(
                            userId = loginResult.user.id,
                            email = loginResult.user.email,
                            name = loginResult.user.nombreCompleto,
                            vehicles = loginResult.vehicles,
                            selectedVehicleId = selectedId
                        )


                        /*
                        // Guardar datos de la sesión
                        sessionManager.saveSession(
                            userId = loginResult.user.id,
                            email = loginResult.user.email,
                            name = loginResult.user.nombreCompleto
                        )
                         */


                        // Registrar token FCM
                        registrarTokenFCM(loginResult.user.id)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginSuccess = true
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                loginError = error.message ?: "Error desconocido"
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = "Error de conexión. Intenta nuevamente"
                    )
                }
            }
        }
    }

    private fun registrarTokenFCM(userId: Int) {
        viewModelScope.launch {
            try {
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val fcmToken = task.result
                        if (fcmToken != null) {
                            viewModelScope.launch {
                                ApiClient.registrarToken(
                                    usuarioId = userId,
                                    token = fcmToken,
                                    dispositivo = "android"
                                )
                                println("✓ Token FCM registrado")
                            }
                        }
                    } else {
                        println("✗ Error obteniendo token FCM: ${task.exception?.message}")
                    }
                }
            } catch (e: Exception) {
                println("✗ Error registrando token FCM: ${e.message}")
            }
        }
    }
}