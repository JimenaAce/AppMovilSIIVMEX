package com.example.appmovilsiivmex.data.remote.dto

data class NotificationDto(
    val id: Int,
    val usuario_id: Int,
    val vehiculo_id: Int?,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean,
    val fecha_creacion: String?,
    val fecha_leida: String?
)