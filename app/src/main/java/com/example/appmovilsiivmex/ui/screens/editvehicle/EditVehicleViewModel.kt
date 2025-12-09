package com.example.appmovilsiivmex.ui.screens.editvehicle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.usecase.EditVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditVehicleViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val editVehicleUseCase: EditVehicleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditVehicleUiState())
    val uiState: StateFlow<EditVehicleUiState> = _uiState.asStateFlow()

    init {
        // Cargar vehículo seleccionado al iniciar
        viewModelScope.launch {
            try {
                val selectedId = sessionManager.getSelectedVehicleId()
                val vehicles = sessionManager.getVehicles()
                val vehicle = vehicles.firstOrNull { it.id == selectedId }

                if (vehicle == null) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "No se encontró el vehículo seleccionado."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        vehicleId = vehicle.id,
                        plate = vehicle.placa,
                        carName = vehicle.nombre_vehiculo,      // adapta a tu modelo real
                        year = vehicle.anio?.toString() ?: "",
                        brand = vehicle.marca ?: "",
                        hologram = mapHologramFromBackend(vehicle.holograma)
                    )
                }

            } catch (e: Exception) {
                Log.e("EditVehicleVM", "Error cargando vehículo seleccionado", e)
                _uiState.update {
                    it.copy(
                        errorMessage = "Error cargando vehículo seleccionado"
                    )
                }
            }
        }
    }

    // Si en BD guardas "Exento" pero en UI usas "E", mapeamos:
    private fun mapHologramFromBackend(holo: String?): String {
        return when (holo) {
            "Exento" -> "E"
            null -> "E"
            else -> holo
        }
    }

    // Si al guardar necesitas el valor para backend:
    private fun mapHologramToBackend(holoUi: String): String {
        return when (holoUi) {
            "E" -> "Exento"
            else -> holoUi
        }
    }

    fun onPlateChange(plate: String) {
        _uiState.update { it.copy(plate = plate, plateError = null) }
    }

    fun onCarNameChange(carName: String) {
        _uiState.update { it.copy(carName = carName) }
    }

    fun onYearChange(year: String) {
        _uiState.update { it.copy(year = year) }
    }

    fun onBrandChange(brand: String) {
        _uiState.update { it.copy(brand = brand) }
    }

    fun onHologramChange(hologram: String) {
        _uiState.update { it.copy(hologram = hologram) }
    }

    // ─────────────────────
    // Guardar cambios
    // ─────────────────────
    fun onSaveClick() {
        val current = _uiState.value
        val id = current.vehicleId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val result = editVehicleUseCase(
                    vehicleId = id,
                    carName = current.carName,
                    year = current.year.toIntOrNull(),
                    brand = current.brand,
                    hologram = mapHologramToBackend(current.hologram)
                )

                result.fold(
                    onSuccess = { updatedVehicle ->

                        updateSessionVehicle(updatedVehicle)

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                vehicleEditSuccess = true
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Error al actualizar vehículo"
                            )
                        }
                    }
                )


            } catch (e: Exception) {
                Log.e("EditVehicleVM", "Error guardando cambios", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión al actualizar vehículo"
                    )
                }
            }
        }
    }

    // Actualizar vehículo en SessionManager
    private suspend fun updateSessionVehicle(updated: Vehicle) {
        val currentList = sessionManager.getVehicles()
        val newList = currentList.map { v ->
            if (v.id == updated.id) updated else v
        }
        val userId = sessionManager.getUserId() ?: return
        val email = sessionManager.getUserEmail() ?: return
        val name = sessionManager.getUserName() ?: ""

        val selectedId = sessionManager.getSelectedVehicleId()

        sessionManager.saveSession(
            userId = userId,
            email = email,
            name = name,
            vehicles = newList,
            selectedVehicleId = selectedId
        )
    }
}
