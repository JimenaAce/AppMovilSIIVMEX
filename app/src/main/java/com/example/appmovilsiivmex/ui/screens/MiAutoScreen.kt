package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.navigation.AppHeader
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto



data class VerificacionInfo(
    val terminacionLabel: String,
    val color: Color,
    val primerPeriodo: String,
    val segundoPeriodo: String
)

data class HoyNoCirculaInfo(
    val resumen: String,
    val detalleEntreSemana: String?,
    val detalleSabado: String?
)

@Composable
fun MiAutoScreen(
    navController: NavController,
) {
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current

    val selectedVehicle = vehicles.firstOrNull { it.id == selectedVehicleId }
        ?: vehicles.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        AppHeader(
            navController = navController,
            showMenu = true,
            showNotificationDot = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedVehicle == null) {
            EmptyVehicleState(
                onAddVehicle = { navController.navigate("agregar_vehiculo") }
            )
            return@Column
        }

        // ---------------- IMAGEN + PLACA + EDITAR ----------------
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.carrito),
                contentDescription = "Auto del usuario",
                modifier = Modifier
                    .width(220.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedVehicle.placa,
                    color = ColorAzulOscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { navController.navigate("editar_vehiculo") },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar datos del vehículo",
                        tint = ColorAzulOscuro,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- DATOS DEL VEHÍCULO ----------------
        InfoDatoFila(
            etiqueta = "Marca",
            valor = selectedVehicle.marca ?: "-"
        )
        InfoDatoFila(
            etiqueta = "Nombre",
            valor = selectedVehicle.nombre_vehiculo
        )
        InfoDatoFila(
            etiqueta = "Año",
            valor = selectedVehicle.anio?.toString() ?: "-",
            mostrarDividerFinal = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- HOLOGRAMA (chips) ----------------
        Text(
            text = "Holograma",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        val holograma = selectedVehicle.holograma   // 'Exento','0','00','1','2'

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HoloChip("E",  activo = isHoloActive("E", holograma))
            HoloChip("00", activo = isHoloActive("00", holograma))
            HoloChip("0",  activo = isHoloActive("0", holograma))
            HoloChip("1",  activo = isHoloActive("1", holograma))
            HoloChip("2",  activo = isHoloActive("2", holograma))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // PERIODO DE VERIFICACIÓN (dinámico según último dígito)
        // =========================================================

        val verifInfo = remember(selectedVehicle.placa) {
            buildVerificacionInfo(selectedVehicle.placa)
        }

        Text(
            text = "Periodo de verificación",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (verifInfo != null) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    color = verifInfo.color,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = verifInfo.terminacionLabel,
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.Top) {
                        Column(modifier = Modifier.widthIn(min = 48.dp)) {
                            Text(
                                text = "1er",
                                color = ColorAzulOscuro,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "2do",
                                color = ColorAzulOscuro,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = verifInfo.primerPeriodo,
                                color = ColorAzulOscuro,
                                fontSize = 14.sp
                            )
                            Text(
                                text = verifInfo.segundoPeriodo,
                                color = ColorAzulOscuro,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No se pudo determinar el período de verificación para esta placa.",
                fontSize = 14.sp,
                color = ColorGrisTexto
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================================================
        // HOY NO CIRCULA (dinámico según holograma + último dígito)
        // =========================================================

        val hoyInfo = remember(selectedVehicle.placa, holograma) {
            buildHoyNoCirculaInfo(selectedVehicle.placa, holograma)
        }

        Column {
            Text(
                text = "Hoy no circula",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hoyInfo.resumen,
                fontSize = 14.sp,
                color = ColorGrisTexto
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ---------------------------
// Estado vacío (sin vehículos)
// ---------------------------
@Composable
private fun EmptyVehicleState(
    onAddVehicle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Aún no has registrado vehículos",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Agrega tu primer vehículo para ver aquí sus detalles.",
            fontSize = 14.sp,
            color = ColorGrisTexto,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = ColorAzulOscuro,
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "Agregar vehículo",
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 10.dp)
                    .clickable { onAddVehicle() },
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ---------------------------
// Composables auxiliares
// ---------------------------

@Composable
private fun InfoDatoFila(
    etiqueta: String,
    valor: String,
    mostrarDividerFinal: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = etiqueta,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro.copy(alpha = 0.8f)
            )
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorAzulOscuro
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!mostrarDividerFinal) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HoloChip(texto: String, activo: Boolean) {
    val bg = if (activo) ColorAzulOscuro else ColorChipInactivo
    val fg = if (activo) Color.White else ColorAzulOscuro
    Surface(
        color = bg,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = texto,
            color = fg,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Mapea el valor de BD al chip visual.
 * En tabla: holograma = 'Exento','0','00','1','2'
 * En UI: mostramos "E" para Exento.
 */
private fun isHoloActive(chipLabel: String, hologramaDb: String?): Boolean {
    if (hologramaDb == null) return false
    return when (chipLabel) {
        "E"  -> hologramaDb.equals("Exento", ignoreCase = true)
        else -> hologramaDb.equals(chipLabel, ignoreCase = true)
    }
}

// =======================
// LÓGICA DE NEGOCIO
// =======================

/**
 * Determina el grupo de verificación (color + meses) a partir del último dígito de la placa.
 */
fun buildVerificacionInfo(placa: String?): VerificacionInfo? {
    val lastDigit = placa?.lastOrNull { it.isDigit() } ?: return null

    return when (lastDigit) {
        '5', '6' -> VerificacionInfo(
            terminacionLabel = "5 y 6",
            color = Color(0xFFFFF176),
            primerPeriodo = "Enero y Febrero",
            segundoPeriodo = "Julio y Agosto"
        )
        '7', '8' -> VerificacionInfo(
            terminacionLabel = "7 y 8",
            color = Color(0xFFF48FB1),
            primerPeriodo = "Febrero y Marzo",
            segundoPeriodo = "Agosto y Septiembre"
        )
        '3', '4' -> VerificacionInfo(
            terminacionLabel = "3 y 4",
            color = Color(0xFFE57373),
            primerPeriodo = "Marzo y Abril",
            segundoPeriodo = "Septiembre y Octubre"
        )
        '1', '2' -> VerificacionInfo(
            terminacionLabel = "1 y 2",
            color = Color(0xFF81C784),
            primerPeriodo = "Abril y Mayo",
            segundoPeriodo = "Octubre y Noviembre"
        )
        '9', '0' -> VerificacionInfo(
            terminacionLabel = "9 y 0",
            color = Color(0xFF64B5F6),
            primerPeriodo = "Mayo y Junio",
            segundoPeriodo = "Noviembre y Diciembre"
        )
        else -> null
    }
}

/**
 * Reglas de “Hoy no circula” según holograma y último dígito.
 */
fun buildHoyNoCirculaInfo(
    placa: String?,
    hologramaDb: String?
): HoyNoCirculaInfo {

    if (hologramaDb == null) {
        return HoyNoCirculaInfo(
            resumen = "No hay información de holograma.",
            detalleEntreSemana = null,
            detalleSabado = null
        )
    }

    // 0, 00 y Exento circulan todos los días
    if (hologramaDb.equals("Exento", true) ||
        hologramaDb == "0" ||
        hologramaDb == "00"
    ) {
        return HoyNoCirculaInfo(
            resumen = "Circulas todos los días",
            detalleEntreSemana = null,
            detalleSabado = null
        )
    }

    val lastDigit = placa?.lastOrNull { it.isDigit() }

    val diaSemana = when (lastDigit) {
        '5', '6' -> "Lunes"
        '7', '8' -> "Martes"
        '3', '4' -> "Miércoles"
        '1', '2' -> "Jueves"
        '9', '0' -> "Viernes"
        else -> null
    }

    val detalleEntreSemana = diaSemana?.let {
        "Entre semana no circulas los $it."
    }

    val detalleSabado = when (hologramaDb) {
        "1" -> {
            if (lastDigit != null && lastDigit in listOf('1', '3', '5', '7', '9')) {
                "No circulas los sábados 1 y 3 de cada mes."
            } else {
                "No circulas los sábados 2 y 4 de cada mes."
            }
        }
        "2" -> "No circulas todos los sábados del mes."
        else -> null
    }

    /*
    val resumen = buildString {
        if (diaSemana != null) append("No circulas los $diaSemana.")
        when (hologramaDb) {
            "1" -> append(" Además tienes restricción algunos sábados.")
            "2" -> append(" Además tienes restricción todos los sábados.")
        }
    }.ifBlank { "Tienes restricciones por holograma y terminación de placa." }


     */

    return HoyNoCirculaInfo(
        resumen = "$detalleEntreSemana $detalleSabado",
        detalleEntreSemana = detalleEntreSemana,
        detalleSabado = detalleSabado
    )
}
