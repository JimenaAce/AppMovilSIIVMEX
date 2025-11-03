package com.example.appmovilsiivmex.ui.screens.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



class VehicleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    fun onPlateChange(v: String)     = _uiState.update { it.copy(plate = v) }
    fun onNicknameChange(v: String)  = _uiState.update { it.copy(nickname = v) }
    fun onYearChange(v: String)      = _uiState.update { it.copy(year = v.take(4)) }
    fun onBrandChange(v: String)     = _uiState.update { it.copy(brand = v) }
    fun onHologramChange(v: String)  = _uiState.update { it.copy(hologram = v) }

    fun submit(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Validaciones básicas (ajusta a tu dominio)
            val yOk = _uiState.value.year.toIntOrNull()?.let { it in 1980..2100 } == true
            val pOk = _uiState.value.plate.length >= 6
            val bOk = _uiState.value.brand.isNotBlank()

            // Simula llamada a backend
            delay(400)

            _uiState.update { it.copy(isLoading = false) }
            if (pOk && yOk && bOk) onSuccess()
            // si necesitas errores, agrega campos de error al UiState
        }
    }
}
