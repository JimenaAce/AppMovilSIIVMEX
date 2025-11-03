package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta

/**
 * Representa cada opción del menú lateral (drawer).
 */
data class OpcionDrawer(
    val icono: @Composable () -> Unit,
    val texto: String,
    val ruta: String
)

@Composable
fun MenuHamburguesa(
    onOpcionSeleccionada: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val opciones = listOf(
        OpcionDrawer(
            icono = { Icon(Icons.Default.DirectionsCar, contentDescription = "Mis vehículos", tint = ColorAzulOscuro) },
            texto = "Mis vehículos",
            ruta = "mis_vehiculos"
        ),
        OpcionDrawer(
            icono = { Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario Verificación", tint = ColorAzulOscuro) },
            texto = "Verificación",
            ruta = "cal_verificacion"
        ),
        OpcionDrawer(
            icono = { Icon(Icons.Default.Block, contentDescription = "Calendario Hoy no circula", tint = ColorAzulOscuro) },
            texto = "Hoy no circula",
            ruta = "cal_hoy_no_circula"
        )
    )

    ModalDrawerSheet(
        drawerContainerColor = ColorFondoTarjeta
    ) {
        // Encabezado con el nombre del usuario
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Nombre de usuario",
                fontWeight = FontWeight.Bold,
                color = ColorAzulOscuro,
                style = MaterialTheme.typography.titleLarge
            )
        }

        // Opciones del menú
        opciones.forEach { opcion ->
            NavigationDrawerItem(
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        opcion.icono()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(opcion.texto)
                    }
                },
                selected = false,
                onClick = { onOpcionSeleccionada(opcion.ruta) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Cerrar sesión
        NavigationDrawerItem(
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión", tint = ColorAzulOscuro)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cerrar sesión")
                }
            },
            selected = false,
            onClick = { onCerrarSesion() },
            modifier = Modifier
                .padding(NavigationDrawerItemDefaults.ItemPadding)
                .padding(bottom = 16.dp)
        )
    }
}
