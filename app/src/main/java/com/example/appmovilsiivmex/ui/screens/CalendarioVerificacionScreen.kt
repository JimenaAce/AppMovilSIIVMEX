package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.EngomadoAmarillo
import com.example.appmovilsiivmex.ui.theme.EngomadoAzul
import com.example.appmovilsiivmex.ui.theme.EngomadoRojo
import com.example.appmovilsiivmex.ui.theme.EngomadoRosa
import com.example.appmovilsiivmex.ui.theme.EngomadoVerde
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioVerificacionScreen(navController: NavController) {
    val estadoDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = estadoDrawer,
        drawerContent = {
            MenuHamburguesa(
                onOpcionSeleccionada = { ruta ->
                    navController.navigate(ruta)
                },
                onCerrarSesion = { /* Acción de cerrar sesión */ }
            )
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Calendario verificación") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { estadoDrawer.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Periodo de verificación",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                ColumnaCalendarioVerificacion()
            }
        }
    }
}

@Composable
fun ColumnaCalendarioVerificacion() {
    val filas = listOf(
        EngomadoVerificacion("5 y 6", EngomadoAmarillo, "Enero y Febrero", "Julio y Agosto"),
        EngomadoVerificacion("7 y 8", EngomadoRosa, "Febrero y Marzo", "Agosto y Septiembre"),
        EngomadoVerificacion("3 y 4", EngomadoRojo, "Marzo y Abril", "Septiembre y Octubre"),
        EngomadoVerificacion("1 y 2", EngomadoVerde, "Abril y Mayo", "Octubre y Noviembre"),
        EngomadoVerificacion("9 y 0", EngomadoAzul, "Mayo y Junio", "Noviembre y Diciembre")
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FilaTextoVerificacion(
            titulo = "Terminación",
            periodo1 = "1er",
            periodo2 = "2do",
            esEncabezado = true
        )

        filas.forEach { fila ->
            FilaColorVerificacion(
                terminacion = fila.terminacion,
                colorFondo = fila.color,
                periodo1 = fila.periodo1,
                periodo2 = fila.periodo2
            )
        }
    }
}

@Composable
fun FilaColorVerificacion(
    terminacion: String,
    colorFondo: Color,
    periodo1: String,
    periodo2: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(colorFondo, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = terminacion,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "1er  $periodo1",
                color = ColorAzulOscuro,
                fontSize = 14.sp
            )
            Text(
                text = "2do $periodo2",
                color = ColorAzulOscuro,
                fontSize = 14.sp
            )
        }
    }
}
@Composable
fun FilaTextoVerificacion(
    titulo: String,
    periodo1: String,
    periodo2: String,
    esEncabezado: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = titulo,
            fontWeight = if (esEncabezado) FontWeight.SemiBold else FontWeight.Normal,
            color = if (esEncabezado) Color.Gray else Color.Black
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = periodo1,
                fontWeight = if (esEncabezado) FontWeight.SemiBold else FontWeight.Normal,
                color = if (esEncabezado) Color.Gray else Color.Black
            )
           Text(
               text = periodo2,
               fontWeight = FontWeight.SemiBold,
               color = if (esEncabezado) Color.Gray else Color.Black
           )
        }
    }
}
/*
Text(
fontWeight = if (esEncabezado) FontWeight.SemiBold else Color.Black,
)*/


data class EngomadoVerificacion(
    val terminacion: String,
    val color: Color,
    val periodo1: String,
    val periodo2: String
)
