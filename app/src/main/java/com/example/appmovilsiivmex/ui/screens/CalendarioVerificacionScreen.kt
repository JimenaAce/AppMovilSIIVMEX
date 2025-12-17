package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.tooling.preview.Preview
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioVerificacionScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {TopBar(onBack)}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Spacer(modifier = Modifier.height(10.dp))
            // Título sección
            Text(
                text = "Periodo de verificación",
                color = ColorGrisTexto,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 5 y 6
            VerificacionRow(
                color = Color(0xFFFFF176), // amarillo
                terminacionLabel = "5 y 6",
                primerPeriodo = "Enero y Febrero",
                segundoPeriodo = "Julio y Agosto"
            )
            Spacer(modifier = Modifier.height(22.dp))

            // 7 y 8
            VerificacionRow(
                color = Color(0xFFF48FB1), // rosa
                terminacionLabel = "7 y 8",
                primerPeriodo = "Febrero y Marzo",
                segundoPeriodo = "Agosto y Septiembre"
            )
            Spacer(modifier = Modifier.height(22.dp))

            // 3 y 4
            VerificacionRow(
                color = Color(0xFFE57373), // rojo
                terminacionLabel = "3 y 4",
                primerPeriodo = "Marzo y Abril",
                segundoPeriodo = "Septiembre y Octubre"
            )
            Spacer(modifier = Modifier.height(22.dp))

            // 1 y 2
            VerificacionRow(
                color = Color(0xFF81C784), // verde
                terminacionLabel = "1 y 2",
                primerPeriodo = "Abril y Mayo",
                segundoPeriodo = "Octubre y Noviembre"
            )
            Spacer(modifier = Modifier.height(22.dp))

            // 9 y 0
            VerificacionRow(
                color = Color(0xFF64B5F6), // azul
                terminacionLabel = "9 y 0",
                primerPeriodo = "Mayo y Junio",
                segundoPeriodo = "Noviembre y Diciembre"
            )
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
                    text = "Calendario Verificación",
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
private fun VerificacionRow(
    color: Color,
    terminacionLabel: String,
    primerPeriodo: String,
    segundoPeriodo: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),   // un poco más compacto
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Bloque de color ligeramente más pequeño
        Surface(
            color = color,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .width(80.dp)     // antes 90
                .height(40.dp)    // antes 48
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = terminacionLabel,
                    color = Color.Black,
                    fontSize = 17.sp,       // antes 18sp
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp)) // menos separación

        // Columna 1er / 2do compacta
        Column(
            modifier = Modifier.width(48.dp)      // antes 55dp
        ) {
            Text(
                text = "1er",
                fontSize = 15.sp,    // antes 16sp
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "2do",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Columna de meses un poco más pequeña
        Column {
            Text(
                text = primerPeriodo,
                fontSize = 15.sp,  // antes 16sp
                color = ColorAzulOscuro
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = segundoPeriodo,
                fontSize = 15.sp,
                color = ColorAzulOscuro
            )
        }
    }
}


