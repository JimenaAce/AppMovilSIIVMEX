package com.example.appmovilsiivmex.domain.repository

import com.example.appmovilsiivmex.data.remote.dto.LoginResult
import com.example.appmovilsiivmex.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResult>
    suspend fun register(name:String, email: String, password: String): Result<User>
    suspend fun verifyEmail(email: String, code: String): Result<Unit>
    suspend fun resendEmail(email:String): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun verifyEmailReset(email: String, code: String): Result<Unit>
    suspend fun resendEmailReset(email: String): Result<Unit>
    suspend fun changePassword(email: String, newPassword: String): Result<Unit>

}