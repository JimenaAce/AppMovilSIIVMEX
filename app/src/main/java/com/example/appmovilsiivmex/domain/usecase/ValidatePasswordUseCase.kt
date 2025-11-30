package com.example.appmovilsiivmex.domain.usecase

class ValidatePasswordUseCase {

    operator fun invoke(password: String): Pair<Boolean, String?> {

        if (password.isBlank()) {
            return false to "La contraseña es requerida"
        }

        val specialChars = "!@#\$%&*?._-"

        if (password.length < 8) {
            return false to "Debe tener al menos 8 caracteres"
        }

        if (password.any { it.isWhitespace() }) {
            return false to "No debe contener espacios en blanco"
        }

        if (!password.any { it.isDigit() }) {
            return false to "Debe contener al menos un número"
        }

        if (!password.any { it.isLowerCase() }) {
            return false to "Debe contener al menos una letra minúscula"
        }

        if (!password.any { it.isUpperCase() }) {
            return false to "Debe contener al menos una letra mayúscula"
        }

        if (!password.any { it in specialChars }) {
            return false to "Debe contener al menos un carácter especial ($specialChars)"
        }

        return true to null
    }

}
