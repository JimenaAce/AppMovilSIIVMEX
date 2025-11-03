package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro

@Composable
fun MultasScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {}
) {
    val fondoApp = Color(0xFFFFFFFF)
    var filtroSeleccionado by remember { mutableStateOf("CDMX") }

    // data dummy
    val multasCDMX = listOf(
        MultaUi("CDMX MUL12345", "5 de mayo de 2025", "Exceso de velocidad"),
        MultaUi("CDMX MUL67890", "30 de abril de 2025", "Uso de celular"),
    )
    val multasEDOMEX = listOf(
        MultaUi("EDOMEX MUL12345", "5 de mayo de 2025", "Exceso de velocidad"),
        MultaUi("EDOMEX MUL67890", "28 de abril de 2025", "Verificación vencida"),
    )

    val multasMostrar = when (filtroSeleccionado) {
        "CDMX" -> multasCDMX
        "EDOMEX" -> multasEDOMEX
        else -> multasCDMX + multasEDOMEX
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = ColorAzulOscuro
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NVW1118",
                        color = ColorAzulOscuro,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Cambiar placa",
                        tint = ColorAzulOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { navController.navigate("notificaciones") }) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notificaciones",
                    tint = ColorAzulOscuro
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JC",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorAzulOscuro
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SELECTOR (CDMX / EDOMEX / Todas)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MultaTab(
                texto = "CDMX",
                seleccionado = filtroSeleccionado == "CDMX",
                onClick = { filtroSeleccionado = "CDMX" }
            )
            MultaTab(
                texto = "EDOMEX",
                seleccionado = filtroSeleccionado == "EDOMEX",
                onClick = { filtroSeleccionado = "EDOMEX" }
            )
            MultaTab(
                texto = "Todas",
                seleccionado = filtroSeleccionado == "Todas",
                onClick = { filtroSeleccionado = "Todas" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LISTA
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            multasMostrar.forEach { multa ->
                MultaItem(multa)
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ---------------- datos y composables ----------------

data class MultaUi(
    val folio: String,
    val fecha: String,
    val motivo: String
)

@Composable
private fun RowScope.MultaTab(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val bg = if (seleccionado) ColorAzulOscuro else Color(0xFFE6EDF4)
    val fg = if (seleccionado) Color.White else ColorAzulOscuro

    Surface(
        color = bg,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        modifier = Modifier
            .weight(1f)
            .height(38.dp),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = fg,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MultaItem(multa: MultaUi) {
    Surface(
        color = Color(0xFFF0F0F0),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = multa.folio,
                color = ColorAzulOscuro,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ColorAzulOscuro,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = multa.fecha,
                    color = ColorAzulOscuro,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = ColorAzulOscuro,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = multa.motivo,
                    color = ColorAzulOscuro,
                    fontSize = 13.sp
                )
            }
        }
    }
}
