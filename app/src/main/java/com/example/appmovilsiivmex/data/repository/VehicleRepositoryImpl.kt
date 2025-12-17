package com.example.appmovilsiivmex.data.repository

import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.data.remote.dto.DeleteVehicleResponse
import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionResponse
import com.example.appmovilsiivmex.data.remote.dto.toDomain
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.repository.VehicleRepository

class VehicleRepositoryImpl: VehicleRepository {

    override suspend fun registerVehicle(email: String, carName: String, plate: String, brand: String, year: Int?, hologram: String, entidad_registro: String): Result<Vehicle> {
        return try{
            val response = ApiClient.registrarVehiculo(email, carName, plate, brand, year, hologram, entidad_registro)
            response.fold(
                onSuccess = { vehicleRegisterResponse ->
                    if(vehicleRegisterResponse.success && vehicleRegisterResponse.vehicle != null){
                        Result.success(vehicleRegisterResponse.vehicle.toDomain())
                    }else{
                        Result.failure(Exception(vehicleRegisterResponse.message))
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )

        } catch (e: Exception){
            Result.failure(Exception("Error en la conexión: ${e.message}"))
        }

    }

    override suspend fun editVehicle(vehicleId: Int, carName: String, brand: String, year: Int?, hologram: String): Result<Vehicle> {

        return try{
            val response = ApiClient.editarVehiculo(vehicleId, carName, brand, year, hologram)
            response.fold(
                onSuccess = { editVehicleResponse ->
                    if(editVehicleResponse.success && editVehicleResponse.vehicle != null){
                        Result.success(editVehicleResponse.vehicle.toDomain())
                    }else{
                        Result.failure(Exception(editVehicleResponse.message))
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

    override suspend fun deleteVehicle(vehicleId: Int): Result<DeleteVehicleResponse> {
        return try{
            val response = ApiClient.eliminarVehiculo(vehicleId)
            response.fold(
                onSuccess = {deleteVehicleResponse ->

                    if(deleteVehicleResponse.success){
                        Result.success(deleteVehicleResponse)
                    }else{
                        Result.failure(Exception(deleteVehicleResponse.message))
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