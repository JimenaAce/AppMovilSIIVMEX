package com.example.appmovilsiivmex.domain.model

data class Notification(
    val id: Int,
    val usuarioId: Int,
    val vehiculoId: Int?,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean,
    val fechaCreacion: String?, // ISO string
    val fechaLeida: String?
)