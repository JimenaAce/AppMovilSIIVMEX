package com.example.appmovilsiivmex.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

data class OpcionNavegacion(
    val ruta: String,
    val icono: ImageVector,
    val etiqueta: String
)

@Composable
fun BarraNavegacionInferior(navController: NavController) {
    val opciones = listOf(
        OpcionNavegacion("panel", Icons.Default.Home, "Panel"),
        OpcionNavegacion("multas", Icons.Default.ReceiptLong, "Multas"),
        OpcionNavegacion("mi_verificacion", Icons.Filled.DateRange, "Mi Verif."),
        OpcionNavegacion("hoy_no_circula", Icons.Filled.DateRange, "Circul."),
        OpcionNavegacion("ubicacion", Icons.Default.Place, "Ubic."),
        OpcionNavegacion("mi_auto", Icons.Default.DirectionsCar, "Mi auto")
    )

    NavigationBar {
        val entradaActual = navController.currentBackStackEntryAsState().value
        val rutaActual = entradaActual?.destination?.route

        opciones.forEach { opcion ->
            NavigationBarItem(
                selected = rutaActual == opcion.ruta,
                onClick = { navController.navigate(opcion.ruta) },
                icon = { Icon(opcion.icono, contentDescription = opcion.etiqueta) },
                label = { Text(opcion.etiqueta) }
            )
        }
    }
}