package com.example.appmovilsiivmex.domain.model

data class Vehicle(
    val id: Int,
    val usuario_id: Int,
    val nombre_vehiculo: String,
    val placa: String,
    val marca: String?,
    val anio: Int?,
    val holograma: String,
    val entidad_registro: String,
)