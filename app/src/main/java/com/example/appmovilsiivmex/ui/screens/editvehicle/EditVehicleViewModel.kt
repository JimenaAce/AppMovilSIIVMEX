package com.example.appmovilsiivmex.ui.screens.editvehicle

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.usecase.DeleteVehicleUseCase
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
    private val editVehicleUseCase: EditVehicleUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditVehicleUiState())
    val uiState: StateFlow<EditVehicleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val vehicles = sessionManager.getVehicles()
                if (vehicles.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            vehicleId = null,
                            plate = "",
                            carName = "",
                            year = "",
                            brand = "",
                            hologram = "E",
                            errorMessage = null
                        )
                    }
                    return@launch
                }

                val selectedId = sessionManager.getSelectedVehicleId()
                val vehicle = vehicles.firstOrNull { it.id == selectedId } ?: vehicles.first()

                _uiState.update {
                    it.copy(
                        vehicleId = vehicle.id,
                        plate = vehicle.placa,
                        carName = vehicle.nombre_vehiculo,
                        year = vehicle.anio?.toString() ?: "",
                        brand = vehicle.marca ?: "",
                        hologram = mapHologramFromBackend(vehicle.holograma),
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                Log.e("EditVehicleVM", "Error cargando vehículo", e)
                _uiState.update {
                    it.copy(errorMessage = "Error cargando vehículo")
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

    // NUEVO: úsalo desde VerificacionScreen
    fun updateHologramFromVerification(vehicleId: Int, hologramaUi: String) {
        viewModelScope.launch {
            try {
                val currentList = sessionManager.getVehicles()
                val currentVehicle = currentList.firstOrNull { it.id == vehicleId }
                    ?: return@launch

                val carName: String = currentVehicle.nombre_vehiculo
                val brand: String = currentVehicle.marca ?: ""
                val year: Int? = currentVehicle.anio

                val hologramBackend: String = mapHologramToBackend(hologramaUi)

                val result = editVehicleUseCase(
                    vehicleId = vehicleId,
                    carName = carName,
                    year = year,
                    brand = brand,
                    hologram = hologramBackend
                )

                result.fold(
                    onSuccess = { updatedVehicle ->
                        updateSessionVehicle(updatedVehicle)
                    },
                    onFailure = { e ->
                        Log.e("VerifVM", "Error actualizando holograma", e)
                    }
                )
            } catch (e: Exception) {
                Log.e("VerifVM", "Error de conexión", e)
            }
        }
    }

    fun onDeleteConfirm(vehicleId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null, vehicleDeleteSuccess = false) }

            val result = deleteVehicleUseCase(vehicleId)

            result.fold(
                onSuccess = { resp ->
                    removeVehicleFromSession(vehicleId)

                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            vehicleDeleteSuccess = true,
                            deleteMessage = resp.message
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = e.message ?: "Error al eliminar vehículo"
                        )
                    }
                }
            )
        }
    }

    private suspend fun removeVehicleFromSession(vehicleId: Int) {
        val currentList = sessionManager.getVehicles()
        val newList = currentList.filterNot { it.id == vehicleId }

        val userId = sessionManager.getUserId() ?: return
        val email = sessionManager.getUserEmail() ?: return
        val name = sessionManager.getUserName() ?: ""

        val currentSelectedId = sessionManager.getSelectedVehicleId()

        val newSelectedId =
            if (currentSelectedId == vehicleId) newList.firstOrNull()?.id else currentSelectedId

        sessionManager.saveSession(
            userId = userId,
            email = email,
            name = name,
            vehicles = newList,
            selectedVehicleId = newSelectedId
        )
    }



}
