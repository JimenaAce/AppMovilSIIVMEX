package com.example.appmovilsiivmex.data.remote.dto

data class VehicleDetectionResponse(
    val success: Boolean,
    val detections: List<VehicleDetectionDto>
)