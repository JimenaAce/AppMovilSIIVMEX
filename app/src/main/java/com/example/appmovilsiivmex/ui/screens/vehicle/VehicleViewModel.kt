package com.example.appmovilsiivmex.ui.screens.vehicle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

        _uiState.update {
            it.copy(
                plateError = if( !validPlate.isValid && plate.isNotBlank()) validPlate.message else null
            )
        }
    }

    fun onRegisterVehicle(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try{

                Log.d("RegistroVehiculo", "El email es: $email")

                val result = vehicleRegisterUseCase(
                    email = email,
                    plate = _uiState.value.plate,
                    carName = _uiState.value.carName,
                    year = _uiState.value.year.toIntOrNull(),
                    brand = _uiState.value.brand,
                    hologram = _uiState.value.hologram
                )

                result.fold(

                    onSuccess = { vehicle ->

                        Log.d("RegistroVehiculo", "El vehículo es: $vehicle")

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vehicleRegisterSuccess = true
                            )
                        }

                    },
                    onFailure = {

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vehicleRegisterSuccess = false
                            )
                        }

                    }
                )

            } catch (e: Exception){

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        vehicleRegisterSuccess = false
                    )
                }

            }
        }
    }
}
