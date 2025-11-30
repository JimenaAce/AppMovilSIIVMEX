package com.example.appmovilsiivmex.domain.usecase


import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionResponse
import com.example.appmovilsiivmex.domain.repository.VehicleRepository

class VehicleDetectionsUseCase(
    private  val repository: VehicleRepository
) {
    suspend operator fun invoke(vehicleId: Int): Result<VehicleDetectionResponse> {
        return repository.deteccionesVehiculo(vehicleId)
    }
}