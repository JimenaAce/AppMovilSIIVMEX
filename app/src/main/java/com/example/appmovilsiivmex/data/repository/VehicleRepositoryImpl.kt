package com.example.appmovilsiivmex.data.repository

import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionResponse
import com.example.appmovilsiivmex.data.remote.dto.toDomain
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.repository.VehicleRepository

class VehicleRepositoryImpl: VehicleRepository {

    override suspend fun registerVehicle(email: String, carName: String, plate: String, brand: String, year: Int?, hologram: String): Result<Vehicle> {
        return try{
            val response = ApiClient.registrarVehiculo(email, carName, plate, brand, year, hologram)
            response.fold(
                onSuccess = { vehicleRegisterResponse ->
                    if(vehicleRegisterResponse.success && vehicleRegisterResponse.vehicle != null){
                        Result.success(vehicleRegisterResponse.vehicle.toDomain())
                    }else{
                        Result.failure(Exception(vehicleRegisterResponse.message))
                    }
                },
                onFailure = { error ->
                    Result.failure(Exception(error))
                }
            )

        } catch (e: Exception){
            Result.failure(Exception("Error en la conexión: ${e.message}"))
        }

    }

    override suspend fun deteccionesVehiculo(vehicleId: Int): Result<VehicleDetectionResponse> {
        return try {
            val response = ApiClient.deteccionesVehiculo(vehicleId)
            response.fold(
                onSuccess = { vehicleDetectionResponse ->
                    if(vehicleDetectionResponse.success){
                        Result.success(vehicleDetectionResponse)
                    }else{
                        Result.failure(Exception("Error al obtener las detecciones"))
                    }
                },
                onFailure = { error ->
                    Result.failure(Exception(error))

                }
            )

        }catch (e: Exception){
            Result.failure(Exception("Error en la conexión: ${e.message}"))
        }
    }

}