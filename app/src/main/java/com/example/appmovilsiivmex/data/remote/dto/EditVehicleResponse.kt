package com.example.appmovilsiivmex.data.remote.dto

data class EditVehicleResponse(
    val success: Boolean,
    val message: String,
    val vehicle: VehicleDto?
)