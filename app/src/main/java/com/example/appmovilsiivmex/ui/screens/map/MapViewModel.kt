package com.example.appmovilsiivmex.ui.screens.map


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.remote.dto.toDomain
import com.example.appmovilsiivmex.domain.usecase.VehicleDetectionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(

    private val vehicleDetectionsUseCase: VehicleDetectionsUseCase

) : ViewModel() {

    private val _mapState = MutableStateFlow(MapUiState())
    val mapState: StateFlow<MapUiState> = _mapState

    fun loadDetections(vehicleId: Int) {
        viewModelScope.launch {
            _mapState.value = _mapState.value.copy(isLoading = true, error = null)

            val result = vehicleDetectionsUseCase(vehicleId)
            result.onSuccess { vehicleDetection ->
                Log.d("MAPVIEW", "El resultado es: ${vehicleDetection.detections.size}.")
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    detections = vehicleDetection.detections.map { it.toDomain() }
                )
            }.onFailure { e ->
                _mapState.value = _mapState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al obtener detecciones"
                )
            }
        }
    }

}
