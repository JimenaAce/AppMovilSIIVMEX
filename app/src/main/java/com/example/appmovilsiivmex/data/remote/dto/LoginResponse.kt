package com.example.appmovilsiivmex.data.remote.dto

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val user: UserDto?,
    val vehicles: List<VehicleDto> = emptyList()
)