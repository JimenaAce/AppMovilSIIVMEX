package com.example.appmovilsiivmex.domain.model

data class VehicleDetection(
    val id: Int,
    val vehicleId: Int,
    val ubicacion: String?,
    val lat: Double?,
    val lng: Double?,
    val fechaHora: String?,
    val imagenBase64: String?
)