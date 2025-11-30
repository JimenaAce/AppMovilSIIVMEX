package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.repository.AuthRepository

class ChangePasswordUseCase(
    private val repository: AuthRepository
){
    suspend operator fun invoke(email:String, newPassword:String): Result<Unit> {

        if (email.isBlank()){
            return Result.failure(Exception("El correo electrónico es requerido"))
        }

        if(newPassword.isBlank()){
            return Result.failure(Exception("La nueva contraseña es requerida"))
        }

        return repository.changePassword(email, newPassword)
    }
}