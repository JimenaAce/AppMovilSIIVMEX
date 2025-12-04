package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.repository.AuthRepository

class ResendEmailResetUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(email: String): Result<Unit> {

        if(email.isBlank()){
            return Result.failure(Exception("Falta correo"))
        }

        return repository.resendEmailReset(email)
    }
}