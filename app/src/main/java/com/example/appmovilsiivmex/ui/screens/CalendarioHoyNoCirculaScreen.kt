package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioHoyNoCirculaScreen (navController: NavController) {
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
                    title = { Text("Bienvenido") },
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
                // Encabezado
                Text(
                    text = "Hologramas 0, 00 y exentos",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Text(text = "Circulas todos los días")

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hologramas 1 y 2 entre semana",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )

                // Tabla de terminaciones y días
                ColumnaHoyNoCircula()

                Spacer(modifier = Modifier.height(12.dp))

                // Sección holograma 1 sabatino
                Text(
                    text = "Holograma 1 sabatino",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                FilaTexto(
                    titulo = "Terminación",
                    valor = "Sábados del mes"
                )
                FilaTexto(
                    titulo = "1, 3, 5, 7 y 9",
                    valor = "1 y 3"
                )
                FilaTexto(
                    titulo = "0, 2, 4, 6 y 8",
                    valor = "2 y 4"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sección holograma 2 sabatino
                Text(
                    text = "Holograma 2 sabatino",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray
                )
                Text(text = "Descansan todos los sábados del mes")
            }

        }
    }
}

@Composable
fun ColumnaHoyNoCircula() {
    val filas = listOf(
        Triple("5 y 6", Color(0xFFFFF176), "Lunes"),        // Amarillo
        Triple("7 y 8", Color(0xFFF48FB1), "Martes"),       // Rosa
        Triple("3 y 4", Color(0xFFE57373), "Miércoles"),    // Rojo
        Triple("1 y 2", Color(0xFF81C784), "Jueves"),       // Verde
        Triple("9 y 0", Color(0xFF64B5F6), "Viernes")       // Azul
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilaTexto(
            titulo = "Terminación",
            valor = "Descanso",
            esEncabezado = true
        )

        filas.forEach { (terminacion, color, dia) ->
            FilaColor(terminacion = terminacion, colorFondo = color, dia = dia)
        }
    }
}

/**
 * Fila con un recuadro de color y el día correspondiente.
 */
@Composable
fun FilaColor(terminacion: String, colorFondo: Color, dia: String) {
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

        Text(
            text = dia,
            color = ColorAzulOscuro,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

/**
 * Fila genérica de texto con dos columnas.
 */
@Composable
fun FilaTexto(
    titulo: String,
    valor: String,
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
        Text(
            text = valor,
            fontWeight = if (esEncabezado) FontWeight.SemiBold else FontWeight.Normal,
            color = if (esEncabezado) Color.Gray else Color.Black
        )
    }
}