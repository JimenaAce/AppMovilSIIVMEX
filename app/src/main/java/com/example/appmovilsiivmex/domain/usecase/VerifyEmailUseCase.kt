package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.repository.AuthRepository

class VerifyEmailUseCase(
    private val repository: AuthRepository
){
    suspend operator fun invoke(email: String, code: String): Result<Unit> {
        if (email.isBlank()){
            return Result.failure(Exception("Falta el correo"))
        }
        if(code.length != 4){
            return Result.failure(Exception("El código debe tener 4 dígitos"))
        }
        return repository.verifyEmail(email, code)
    }
}