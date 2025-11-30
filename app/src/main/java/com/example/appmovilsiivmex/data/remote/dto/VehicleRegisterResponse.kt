package com.example.appmovilsiivmex.data.remote.dto

data class VehicleRegisterResponse(
    val success: Boolean,
    val message: String,
    val vehicle: VehicleDto?
)