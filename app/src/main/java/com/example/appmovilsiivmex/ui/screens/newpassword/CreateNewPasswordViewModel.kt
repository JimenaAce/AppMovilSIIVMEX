package com.example.appmovilsiivmex.ui.screens.newpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.domain.usecase.ChangePasswordUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewPasswordViewModel @Inject constructor(
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateNewPasswordUiState())
    val uiState: StateFlow<CreateNewPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
        validateForm()
    }

    fun onConfirmChange(confirm: String) {
        _uiState.update { it.copy(confirm = confirm) }
        validateForm()
    }

    fun onTogglePasswordVisibility1() {
        _uiState.update { it.copy(showPassword1 = !it.showPassword1) }
    }
    fun onTogglePasswordVisibility2(){
        _uiState.update { it.copy(showPassword2 = !it.showPassword2) }
    }

    private fun validateForm() {

        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirm

        val (isPasswordValid, passwordMessage) = validatePasswordUseCase(password)
        val isConfirmPassword = password == confirmPassword

        _uiState.update {
            it.copy(
                isChangeEnabled = isPasswordValid && isConfirmPassword,
                passwordError = if (!isPasswordValid && password.isNotBlank()) passwordMessage else null,
                confirmError = if(!isConfirmPassword && confirmPassword.isNotBlank()) "Las contraseñas no coinciden" else null,
            )
        }
    }
    fun onSubmitClick(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val result = changePasswordUseCase(email = email, newPassword = _uiState.value.password)

                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                changePasswordSuccess = true
                            )
                        }
                    },
                    onFailure = {error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }
                )

            } catch (e: Exception){
                _uiState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }


        }
    }
}
