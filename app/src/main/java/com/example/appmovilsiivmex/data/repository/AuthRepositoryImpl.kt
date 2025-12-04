package com.example.appmovilsiivmex.data.repository

import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.data.remote.dto.LoginResult
import com.example.appmovilsiivmex.data.remote.dto.toDomain
import com.example.appmovilsiivmex.domain.model.User
import com.example.appmovilsiivmex.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {

    override suspend fun login(email: String, password: String): Result<LoginResult> {
        return try {
            val response = ApiClient.login(email, password)

            response.fold(
                onSuccess = { loginResponse ->
                    if (loginResponse.success && loginResponse.user != null) {
                        val loginResult = LoginResult(
                            user = loginResponse.user.toDomain(),
                            vehicles = loginResponse.vehicles.map { it.toDomain() }
                        )
                        Result.success(loginResult)
                    } else {
                        Result.failure(Exception(loginResponse.message))
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try{
            val response = ApiClient.registrar(name, email, password)
            response.fold(
                onSuccess = { registerResponse ->
                    if (registerResponse.success && registerResponse.user != null) {
                        Result.success(registerResponse.user.toDomain())
                    } else{
                        Result.failure(Exception(registerResponse.message))
                    }

                },
                onFailure = {error ->
                    Result.failure(error)
                }

            )
        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun verifyEmail(email: String, code: String): Result<Unit> {
        return try {
            val response = ApiClient.verificarCorreo(email, code)
            response.fold(
                onSuccess = { verifyEmailResponse ->
                    if (verifyEmailResponse.success){
                        Result.success(Unit)

                    } else {
                        Result.failure(Exception(verifyEmailResponse.message))
                    }
                },

                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun resendEmail(email: String): Result<Unit> {
        return try{
            val response = ApiClient.reenviarCorreo(email)
            response.fold(
                onSuccess = { resendEmailResponse ->

                    if(resendEmailResponse.success){
                        Result.success(Unit)
                    }else{
                        Result.failure(Exception(resendEmailResponse.message))
                    }

                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {

        return try {
            val response = ApiClient.restablecerContrasenia(email)
            response.fold(
                onSuccess = { forgotPasswordResponse ->
                    if (forgotPasswordResponse.success){
                        Result.success(Unit)

                    } else {
                        Result.failure(Exception(forgotPasswordResponse.message))
                    }
                },

                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    }

    override suspend fun verifyEmailReset(email: String, code: String): Result<Unit> {
        return try {
            val response = ApiClient.verificarCorreoReestablecer(email, code)
            response.fold(
                onSuccess = { verifyEmailReset ->

                    if(verifyEmailReset.success){
                        Result.success(Unit)
                    }else {
                        Result.failure(Exception(verifyEmailReset.message))
                    }

                },
                onFailure = { error ->
                    Result.failure(Exception(error))
                }
            )

        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun resendEmailReset(email: String): Result<Unit> {

        return try{
            val response = ApiClient.reenviarCorreoRestablecer(email)
            response.fold(
                onSuccess = { resendEmailResetResponse ->

                    if(resendEmailResetResponse.success){
                        Result.success(Unit)
                    }else{
                        Result.failure(Exception(resendEmailResetResponse.message))
                    }

                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    override suspend fun changePassword(email: String, newPassword: String): Result<Unit> {
        return try {
            val response = ApiClient.cambiarContrasenia(email, newPassword)
            response.fold(
                onSuccess = { changePassword ->

                    if(changePassword.success){
                        Result.success(Unit)
                    }else{
                        Result.failure(Exception(changePassword.message))
                    }
                },
                onFailure = { error ->
                    Result.failure(Exception(error))
                }
            )

        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }


}