package com.example.appmovilsiivmex.ui.screens.vehicle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.usecase.PlateRegion
import com.example.appmovilsiivmex.domain.usecase.ValidatePlateUseCase
import com.example.appmovilsiivmex.domain.usecase.VehicleRegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VehicleViewModel @Inject constructor(

    private val sessionManager: SessionManager,
    private val vehicleRegisterUseCase: VehicleRegisterUseCase,
    private val validatePlateUseCase: ValidatePlateUseCase

): ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    fun onPlateChange(plate: String){
        _uiState.update { it.copy(plate = plate) }
        validateForm()
    }

    fun onCarNameChange(carName: String){
        _uiState.update { it.copy(carName = carName) }
        // Poner una función para validar este parámetro
    }

    fun onYearChange(year: String){
        _uiState.update { it.copy(year = year) }
        // Poner una función para validar este parámetro
    }

    fun onBrandChange(brand: String){
        _uiState.update { it.copy(brand = brand) }
        // Poner una función para validar este parámetro
    }

    fun onHologramChange(hologram: String){
        _uiState.update { it.copy(hologram = hologram) }
        // Poner una función para validar este parámetro
    }


    private fun validateForm(){

        val plate = _uiState.value.plate
        val validPlate = validatePlateUseCase(plate)

        val regionString: String? =
            if (validPlate.isValid) {
                when (validPlate.region) {
                    PlateRegion.CDMX -> "CDMX"
                    PlateRegion.EDOMEX -> "EDOMEX"
                    null -> null
                }
            } else null

        _uiState.update {
            it.copy(
                plateError = if( !validPlate.isValid && plate.isNotBlank()) validPlate.message else null,
                region = regionString
            )
        }
    }

    fun onRegisterVehicle(email: String, updateSession: Boolean) {
        viewModelScope.launch {

            val region = _uiState.value.region
            if (region.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        plateError = "La placa debe corresponder a CDMX o EDOMEX antes de registrar el vehículo."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true) }

            try {
                Log.d("RegistroVehiculo", "El email es: $email y la región es: $region")

                val result = vehicleRegisterUseCase(
                    email = email,
                    plate = _uiState.value.plate,
                    carName = _uiState.value.carName,
                    year = _uiState.value.year.toIntOrNull(),
                    brand = _uiState.value.brand,
                    hologram = _uiState.value.hologram,
                    entidad_registro = region
                )

                result.fold(
                    onSuccess = { vehicle ->

                        Log.d("RegistroVehiculo", "El vehículo es: $vehicle")

                        if (updateSession) {
                            Log.d("RegistroVehiculo", "Un nuevo vehículo: $vehicle")
                            actualizarSessionConNuevoVehiculo(email, vehicle)

                        }else {
                            Log.d("RegistroVehiculo", "Primer vehículo: $vehicle")
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vehicleRegisterSuccess = true
                            )
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vehicleRegisterSuccess = false,
                                vehicleRegisterError = error.message
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        vehicleRegisterSuccess = false,
                        vehicleRegisterError = "Error de conexión. ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun actualizarSessionConNuevoVehiculo(emailFallback: String, vehicle: Vehicle) {

        // Si por alguna razón aún no hay sesión, mejor no marcamos logged-in aquí
        val currentlyLogged = sessionManager.isLoggedIn()
        if (!currentlyLogged) {
            Log.w("RegistroVehiculo", "Intento de actualizar sesión sin estar logueado. Se omite.")
            return
        }

        val currentVehicles = sessionManager.getVehicles()
        val updatedVehicles = currentVehicles + vehicle

        val userId = sessionManager.getUserId() ?: vehicle.usuario_id
        val storedEmail = sessionManager.getUserEmail() ?: emailFallback
        val storedName = sessionManager.getUserName() ?: ""

        val selectedId = sessionManager.getSelectedVehicleId() ?: vehicle.id

        sessionManager.saveSession(
            userId = userId,
            email = storedEmail,
            name = storedName,
            vehicles = updatedVehicles,
            selectedVehicleId = selectedId
        )
    }
}
