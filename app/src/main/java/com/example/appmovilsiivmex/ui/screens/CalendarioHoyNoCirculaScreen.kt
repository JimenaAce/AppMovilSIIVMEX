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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioHoyNoCirculaScreen(
    onBack: () -> Unit = {}
) {

    Scaffold(
        topBar = { TopBar(onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .background(Color.White)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Calendario Hoy no Circula",
                    color = ColorAzulOscuro,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp) // tamaño típico del botón
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = ColorAzulOscuro
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
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