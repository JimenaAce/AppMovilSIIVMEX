package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
        OpcionNavegacion("inicio", Icons.Default.Home, "Inicio"),
        OpcionNavegacion("multas", Icons.AutoMirrored.Filled.ReceiptLong, "Multas"),
        OpcionNavegacion("ubicacion", Icons.Default.Place, "Ubicación"),
        OpcionNavegacion("mi_auto", Icons.Default.DirectionsCar, "Mi auto")
    )

    val fondoBarra = Color(0xFF2A3E55) // Color exacto que pediste

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(25.dp),
            shadowElevation = 16.dp,
            tonalElevation = 0.dp,
            color = fondoBarra
        ) {
            val entradaActual by navController.currentBackStackEntryAsState()
            val rutaActual = entradaActual?.destination?.route

            NavigationBar(
                containerColor = Color.Transparent,
                tonalElevation = 0.dp,
                modifier = Modifier.height(75.dp)
            ) {
                opciones.forEach { opcion ->
                    NavigationBarItem(
                        selected = rutaActual == opcion.ruta,
                        onClick = {
                            navController.navigate(opcion.ruta) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = opcion.icono,
                                contentDescription = opcion.etiqueta
                            )
                        },
                        label = { Text(opcion.etiqueta) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color(0xFFD6DEE8),
                            unselectedTextColor = Color(0xFFD6DEE8),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
