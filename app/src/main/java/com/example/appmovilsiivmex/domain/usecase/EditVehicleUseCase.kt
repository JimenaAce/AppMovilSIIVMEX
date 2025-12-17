package com.example.appmovilsiivmex.domain.usecase


import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.repository.VehicleRepository

class EditVehicleUseCase(
    private val repository: VehicleRepository
){

    suspend operator fun invoke(vehicleId:Int, carName:String, brand:String, year:Int?, hologram:String): Result<Vehicle> {

        // Validaciones previas
        if (carName.isBlank()) {
            return Result.failure(Exception("El nombre del vehículo es requerido"))
        }

        if (brand.isBlank()) {
            return Result.failure(Exception("La marca es requerido"))
        }

        if (hologram.isBlank()) {
            return Result.failure(Exception("El holograma es requerido"))
        }

        return repository.editVehicle(vehicleId, carName, brand, year, hologram)
    }

}