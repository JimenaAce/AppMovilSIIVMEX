package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmovilsiivmex.ui.screens.CalendarioHoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.CalendarioVerificacionScreen
import com.example.appmovilsiivmex.ui.screens.HoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.MisVehiculosScreen
import com.example.appmovilsiivmex.ui.screens.PanelScreen
import com.example.appmovilsiivmex.ui.screens.PantallaPlaceholder

    @Composable
    fun NavegacionAuto(
        controladorNavegacion: NavHostController,
        paddingValues: PaddingValues
    ) {
        NavHost(
            navController = controladorNavegacion,
            startDestination = "mis_vehiculos"
        ) {
            // Menú de Hamburguesa
            composable("mis_vehiculos") { MisVehiculosScreen(controladorNavegacion) }
            composable("cal_verificacion") { CalendarioVerificacionScreen(controladorNavegacion) }
            composable("cal_hoy_no_circula") { CalendarioHoyNoCirculaScreen(controladorNavegacion) }

            // Barra de Navegación Inferior
            composable("panel") { PanelScreen(controladorNavegacion) }
            composable("multas") { PantallaPlaceholder("Multas") }
            composable("mi_verificacion") { PantallaPlaceholder("Verificación") }
            composable("hoy_no_circula") { HoyNoCirculaScreen() }
            composable("ubicacion") { PantallaPlaceholder("Ubicación") }
            composable("mi_auto") { PantallaPlaceholder("Mi auto") }

            // Opciones extras
            composable("agregar_vehiculo") { PantallaPlaceholder("Agregar vehículo") }
            composable("editar_vehiculo") { PantallaPlaceholder("Editar vehículo") }

            // De Prueba

        }
    }
