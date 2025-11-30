package com.example.appmovilsiivmex.data.remote.dto

data class VehicleDto(
    val id: Int,
    val usuario_id: Int,
    val nombre_vehiculo: String,
    val placa: String,
    val marca: String?,
    val anio: Int?,
    val holograma: String,
    val entidad_registro: String,
)