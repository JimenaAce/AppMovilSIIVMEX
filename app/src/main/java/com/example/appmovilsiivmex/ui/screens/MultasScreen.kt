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
import com.example.appmovilsiivmex.navigation.AppHeader
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import okio.ByteString.Companion.encodeUtf8

@Composable
fun MultasScreen(
    navController: NavController
) {
    val fondoApp = Color(0xFFFFFFFF)
    var filtroSeleccionado by remember { mutableStateOf("CDMX") }

    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val selectedVehicle =
        vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    val placa = selectedVehicle?.placa

    // data dummy
    val multasCDMX = if (placa.equals("MNJ421A") ) emptyList() else listOf(
        MultaUi("CDMX MUL4230380382", "30 de abril de 2024", "Documentaci<f3>n incompleta"),
        MultaUi("CDMX MUL4230380345", "3 de febrero de 2025", "Exceder límites de velocidad"),
        MultaUi("CDMX MUL4230380345", "5 de mayo de 2025", "Violaciones de motocicletas"),

    )
    val multasEDOMEX =if (placa.equals("MNJ421A") ) emptyList() else listOf(
        MultaUi("EDOMEX MUL4229208845", "3 de marzo de 2024", "Exceder límites de velocidad"),
        MultaUi("EDOMEX MUL4229208827", "2 de enero de 2025", "Documentaci<f3>n incompleta"),

    )

    val multasMostrar = when (filtroSeleccionado) {
        "CDMX" -> multasCDMX
        "EDOMEX" -> multasEDOMEX
        else -> (multasCDMX + multasEDOMEX)
            .sortedByDescending { it.fecha }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // HEADER
        AppHeader(
            navController = navController,
            showMenu = true,
            showNotificationDot = false
        )


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
