package com.example.appmovilsiivmex.data.remote.dto

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserDto?
)