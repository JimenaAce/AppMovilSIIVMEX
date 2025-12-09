package com.example.appmovilsiivmex.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.ui.screens.vehicle.VehicleAddScreen
import com.example.appmovilsiivmex.ui.screens.vehicle.VehicleViewModel


@Composable
fun AddVehicleScreen(
    email: String,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: VehicleViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.value.vehicleRegisterSuccess) {
        if (uiState.value.vehicleRegisterSuccess) {
            onSuccess()
        }
    }

    VehicleAddScreen(
        email = email,
        updateSession = true,
        onBack = onBack,
    )
}
