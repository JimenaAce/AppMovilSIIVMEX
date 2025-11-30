package com.example.appmovilsiivmex.data.remote.dto

data class VehicleDetectionDto(
    val id: Int,
    val vehiculo_id: Int,
    val ubicacion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val fecha_hora: String?,
    val imagen_base64: String?
)
