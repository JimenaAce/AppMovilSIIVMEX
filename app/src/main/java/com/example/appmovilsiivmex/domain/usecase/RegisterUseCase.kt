package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.model.User
import com.example.appmovilsiivmex.domain.repository.AuthRepository

class RegisterUseCase(
    private  val repository: AuthRepository
) {
    suspend operator fun invoke(name:String, email:String, password:String): Result<User> {

        // Validaciones previas
        if(name.isBlank())
            return Result.failure(Exception("El nombre completo es obligatorio"))

        if (email.isBlank()) {
            return Result.failure(Exception("El correo electrónico es requerido"))
        }

        if (password.isBlank()) {
            return Result.failure(Exception("La contraseña es requerida"))
        }

        return repository.register(name, email, password)
    }
}