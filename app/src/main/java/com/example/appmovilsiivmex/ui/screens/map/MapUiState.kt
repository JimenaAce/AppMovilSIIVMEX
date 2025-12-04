package com.example.appmovilsiivmex.ui.screens.map

import com.example.appmovilsiivmex.domain.model.VehicleDetection

data class MapUiState(

    val isLoading: Boolean = false,
    val detections: List<VehicleDetection> = emptyList(),
    val selectedVehicleId: Int? = null,

    // Errores
    val error: String? = null
)