package com.example.appmovilsiivmex.ui.screens.editvehicle

data class EditVehicleUiState(
    val isLoading: Boolean = false,
    val vehicleId: Int? = null,

    val plate: String = "",
    val plateError: String? = null,

    val carName: String = "",
    val year: String = "",
    val brand: String = "",
    val hologram: String = "E",

    val vehicleEditSuccess: Boolean = false,
    val errorMessage: String? = null,

    val isDeleting: Boolean = false,
    val vehicleDeleteSuccess: Boolean = false,
    val deleteMessage: String? = null

)
