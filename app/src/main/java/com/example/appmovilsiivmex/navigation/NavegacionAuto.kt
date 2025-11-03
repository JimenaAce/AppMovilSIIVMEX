package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmovilsiivmex.ui.screens.EditarVehiculoScreen
import com.example.appmovilsiivmex.ui.screens.HoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.InicioScreen
import com.example.appmovilsiivmex.ui.screens.MiAutoScreen
import com.example.appmovilsiivmex.ui.screens.NotificacionesScreen
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
        composable("inicio") {
            InicioScreen(navController = controladorNavegacion)
        }
        composable("multas") { PantallaPlaceholder("Multas") }
        composable("ubicacion") { PantallaPlaceholder("Ubicación") }

        composable("mi_auto") {
            MiAutoScreen(navController = controladorNavegacion)
        }

        // nueva ruta de edición
        composable("editar_vehiculo") {
            EditarVehiculoScreen(
                navController = controladorNavegacion,
                placaInicial = "NVW1118",
                marcaInicial = "Volkswagen",
                nombreInicial = "Vehículo",
                anioInicial = "2019",
                hologramaInicial = "0",
                onSave = { nuevaPlaca, nuevaMarca, nuevoNombre, nuevoAnio, nuevoHolo ->
                    // Aquí es donde se guardaría en BD...
                    // Por ahora solo estamos simulando guardado...
                }
            )
        }

        composable("hoy_no_circula") { HoyNoCirculaScreen() }

        composable("notificaciones") {
            NotificacionesScreen(navController = controladorNavegacion)
        }

    }
}
