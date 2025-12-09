package com.example.appmovilsiivmex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HoyNoCirculaScreen(
    onBack: () -> Unit,
    openCalendar: () -> Unit
) {
    // Vehículo actual desde los CompositionLocal
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val selectedVehicle =
        vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    val placa = selectedVehicle?.placa
    val hologramaDb = selectedVehicle?.holograma

    val verifInfo = buildVerificacionInfo(placa)
    val hoyInfo = buildHoyNoCirculaInfo(placa, hologramaDb)
    val restringidoHoy = tieneRestriccionHoy(placa, hologramaDb)

    // Para saber cuál chip de holograma marcar
    val hologramaUi = when {
        hologramaDb == null -> ""
        hologramaDb.equals("Exento", true) -> "E"
        else -> hologramaDb
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Encabezado
        EncabezadoHoyNoCircula(
            onBack = onBack,
            onOpenCalendar = openCalendar
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta grande con info de hoy
        TarjetaInformativa(
            verifInfo = verifInfo,
            restringidoHoy = restringidoHoy
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Holograma
        SeccionHolograma(
            hologramaSeleccionado = hologramaUi
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Texto "Hoy no circula"
        TextoHoyNoCircula(
            resumen = hoyInfo.resumen
        )
    }
}

@Composable
private fun EncabezadoHoyNoCircula(
    onBack: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Regresar",
                tint = ColorAzulOscuro
            )
        }
        Text(
            text = "Hoy no circula",
            color = ColorAzulOscuro,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onOpenCalendar) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = "Calendario",
                tint = ColorAzulOscuro
            )
        }
    }
}

@Composable
private fun TarjetaInformativa(
    verifInfo: VerificacionInfo?,
    restringidoHoy: Boolean
) {
    val colorBarra = if (restringidoHoy) Color(0xFFE53935) else Color(0xFF4CAF50)
    val fondoSuave = if (restringidoHoy) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val mensaje =
        if (restringidoHoy) "No circulas el día de hoy"
        else "Puedes circular el día de hoy"

    val icono = if (restringidoHoy) Icons.Default.Block else Icons.Default.Check
    val colorIcono = if (restringidoHoy) Color(0xFFE53935) else Color(0xFF4CAF50)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(fondoSuave, shape = RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BARRA LATERAL
        Box(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .background(colorBarra, RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ÍCONO GRANDE
            Icon(
                icono,
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(52.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // CHIP DE TERMINACIÓN (opcional si existe verifInfo)
            if (verifInfo != null) {
                Surface(
                    color = verifInfo.color,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = verifInfo.terminacionLabel,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // MENSAJE PRINCIPAL
            Text(
                text = mensaje,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
        }
    }
}


@Composable
private fun SeccionHolograma(
    hologramaSeleccionado: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            "Holograma",
            fontWeight = FontWeight.Bold,
            color = ColorGrisTexto,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                12.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("E", "00", "0", "1", "2").forEach { valor ->
                val seleccionado = valor == hologramaSeleccionado
                Surface(
                    color = if (seleccionado) ColorAzulOscuro else ColorChipInactivo,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = valor,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        color = if (seleccionado) Color.White else Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TextoHoyNoCircula(
    resumen: String
) {
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Hoy no circula",
            fontWeight = FontWeight.Bold,
            color = ColorGrisTexto,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            resumen,
            color = ColorGrisTexto,
            fontSize = 14.sp
        )
    }
}
