package com.example.appmovilsiivmex.domain.repository
import com.example.appmovilsiivmex.data.remote.dto.DeleteVehicleResponse
import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionResponse
import com.example.appmovilsiivmex.domain.model.Vehicle

interface VehicleRepository {

    suspend fun registerVehicle(email:String, carName:String, plate:String, brand:String, year:Int?, hologram:String, entidad_registro:String): Result<Vehicle>
    suspend fun editVehicle(vehicleId: Int, carName:String, brand: String, year: Int?, hologram: String): Result<Vehicle>
    suspend fun deleteVehicle(vehicleId: Int): Result<DeleteVehicleResponse>
    suspend fun deteccionesVehiculo(vehicleId: Int): Result<VehicleDetectionResponse>

}
