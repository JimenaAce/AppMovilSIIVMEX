package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.data.remote.dto.LoginResult
import com.example.appmovilsiivmex.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<LoginResult> {
        // Validaciones previas
        if (email.isBlank()) {
            return Result.failure(Exception("El correo electrónico es requerido"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }

        return repository.login(email, password)
    }
}