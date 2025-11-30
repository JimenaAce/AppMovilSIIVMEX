package com.example.appmovilsiivmex.ui.screens.vehicle

data class VehicleUiState(
    val plate: String = "",
    val carName: String = "",
    val year: String = "",
    val brand: String = "",
    val hologram: String = "0",
    val isLoading: Boolean = false,
    val vehicleRegisterSuccess: Boolean = false,

    // Errores
    val plateError: String? = null
)