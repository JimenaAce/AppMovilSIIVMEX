package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovilsiivmex.ui.screens.*
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto
import com.example.appmovilsiivmex.ui.theme.ColorRosa
import com.example.appmovilsiivmex.ui.theme.ColorVerde

@Composable
fun HoyNoCirculaScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        EncabezadoHoyNoCircula()
        Spacer(modifier = Modifier.height(24.dp))
        TarjetaInformativa()
        Spacer(modifier = Modifier.height(32.dp))
        SeccionHolograma()
        Spacer(modifier = Modifier.height(24.dp))
        TextoHoyNoCircula()
    }
}

@Composable
fun EncabezadoHoyNoCircula() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { /* Acción de regresar */ }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
        }
        Text(
            text = "Hoy no circula",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = { /* Acción calendario */ }) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
        }
    }
}

@Composable
fun TarjetaInformativa() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ColorFondoTarjeta, shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Barra lateral verde
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(80.dp)
                .background(ColorVerde, shape = RoundedCornerShape(3.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.Default.DirectionsCar,
                contentDescription = "Auto",
                tint = ColorAzulOscuro,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = ColorRosa,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "7 y 8",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Puedes circular sin restricciones",
                color = ColorGrisTexto,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun SeccionHolograma() {
    Column {
        Text("Holograma", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("E", "00", "0", "1", "2").forEach { valor ->
                Surface(
                    color = if (valor == "0") ColorAzulOscuro else ColorChipInactivo,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = valor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = if (valor == "0") Color.White else Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun TextoHoyNoCircula() {
    Column {
        Text("Hoy no circula", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Circulas todos los días", color = ColorGrisTexto)
    }
}
