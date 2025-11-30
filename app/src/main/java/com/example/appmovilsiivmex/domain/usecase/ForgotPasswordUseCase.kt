package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.repository.AuthRepository

class ForgotPasswordUseCase(
    private val repository: AuthRepository
){

    suspend operator fun invoke(email:String): Result<Unit> {
        if(email.isBlank()){
            return Result.failure(Exception("Falta correo"))
        }
        return repository.forgotPassword(email)
    }
}
