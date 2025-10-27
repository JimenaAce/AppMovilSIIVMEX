package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmovilsiivmex.ui.screens.HoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.PantallaPlaceholder

    @Composable
    fun NavegacionAuto(
        controladorNavegacion: NavHostController,
        paddingValues: PaddingValues
    ) {
        NavHost(
            navController = controladorNavegacion,
            startDestination = "hoy_no_circula"
        ) {
            composable("hoy_no_circula") { HoyNoCirculaScreen() }
            composable("inicio") { PantallaPlaceholder("Inicio") }
            composable("multas") { PantallaPlaceholder("Multas") }
            composable("ubicacion") { PantallaPlaceholder("Ubicación") }
            composable("mi_auto") { PantallaPlaceholder("Mi auto") }
        }
    }
