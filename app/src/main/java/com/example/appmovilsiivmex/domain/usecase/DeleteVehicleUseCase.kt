package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.data.remote.dto.DeleteVehicleResponse
import com.example.appmovilsiivmex.domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val repository: VehicleRepository
){

    suspend operator fun invoke(vehicleId:Int): Result<DeleteVehicleResponse> {
        return repository.deleteVehicle(vehicleId)
    }

}