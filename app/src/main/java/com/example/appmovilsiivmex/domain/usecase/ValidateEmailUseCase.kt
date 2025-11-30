package com.example.appmovilsiivmex.domain.usecase

class ValidateEmailUseCase {
    operator fun invoke(email: String): Boolean {

        if (email.isBlank()) return false

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}