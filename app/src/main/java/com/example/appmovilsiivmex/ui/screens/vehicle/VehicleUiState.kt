package com.example.appmovilsiivmex.ui.screens.vehicle

import com.example.appmovilsiivmex.domain.usecase.PlateRegion

data class VehicleUiState(
    val plate: String = "",
    val carName: String = "",
    val year: String = "",
    val brand: String = "",
    val hologram: String = "0",
    val region: String? = null,
    val isLoading: Boolean = false,
    val vehicleRegisterSuccess: Boolean = false,

    // Errores
    val plateError: String? = null
)