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

// mis pantallas nuevas
import com.example.appmovilsiivmex.ui.screens.InicioScreen
import com.example.appmovilsiivmex.ui.screens.EditarVehiculoScreen
import com.example.appmovilsiivmex.ui.screens.MiAutoConDrawerScreen
import com.example.appmovilsiivmex.ui.screens.MultasconDrawerScreen
import com.example.appmovilsiivmex.ui.screens.NotificacionesconDrawerScreen

@Composable
fun NavegacionAuto(
    controladorNavegacion: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = controladorNavegacion,
        // dejamos el que traía el main para no romper el flujo original
        startDestination = "mis_vehiculos"
    ) {

        // ─────────────────────
        // MENÚ LATERAL (venían del main)
        // ─────────────────────
        composable("mis_vehiculos") {
            MisVehiculosScreen(controladorNavegacion)
        }
        composable("cal_verificacion") {
            CalendarioVerificacionScreen(controladorNavegacion)
        }
        composable("cal_hoy_no_circula") {
            CalendarioHoyNoCirculaScreen(controladorNavegacion)
        }

        // ─────────────────────
        // BOTTOM / PANEL (venía del main)
        // ─────────────────────
        composable("panel") {
            PanelScreen(controladorNavegacion)
        }
        composable("multas") {
            MultasconDrawerScreen(navController = controladorNavegacion)
        }

        composable("mi_verificacion") {
            PantallaPlaceholder("Verificación")
        }
        composable("hoy_no_circula") {
            HoyNoCirculaScreen()
        }
        composable("ubicacion") {
            PantallaPlaceholder("Ubicación")
        }

        // ─────────────────────
        // RUTAS QUE EN MAIN ERAN PLACEHOLDER
        // PERO YA TENEMOS PANTALLA REAL
        // ─────────────────────
        composable("mi_auto") {
            MiAutoConDrawerScreen(navController = controladorNavegacion)
        }


        composable("editar_vehiculo") {
            EditarVehiculoScreen(
                navController = controladorNavegacion,
                placaInicial = "NVW1118",
                marcaInicial = "Volkswagen",
                nombreInicial = "Vehículo",
                anioInicial = "2019",
                hologramaInicial = "0",
                onSave = { _, _, _, _, _ ->
                    // aquí luego guardamos en BD
                }
            )
        }

        // ─────────────────────
        // MIS RUTAS NUEVAS
        // ─────────────────────
        composable("inicio") {
            InicioScreen(navController = controladorNavegacion)
        }

        composable("notificaciones") {
            NotificacionesconDrawerScreen(navController = controladorNavegacion)
        }

        // ─────────────────────
        // RUTA EXTRA DEL MAIN
        // ─────────────────────
        composable("agregar_vehiculo") {
            PantallaPlaceholder("Agregar vehículo")
        }
    }
}
