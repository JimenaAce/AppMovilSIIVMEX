package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.navigation.AppHeader
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.ui.screens.editvehicle.EditVehicleViewModel
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
    viewModel: EditVehicleViewModel = hiltViewModel()
) {
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current

    val selectedVehicle = vehicles.firstOrNull { it.id == selectedVehicleId }
        ?: vehicles.firstOrNull()

    val uiState by viewModel.uiState.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Para cambiar la pantalla al inicio
    LaunchedEffect(uiState.vehicleDeleteSuccess) {
        if (uiState.vehicleDeleteSuccess) {
            // Puedes cambiarlo por navController.navigate("inicio") etc.
            //navController.popBackStack()
            navController.navigate("inicio")
        }
    }

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

        // ---------------- IMAGEN + PLACA + EDITAR + ELIMINAR ----------------
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = selectedVehicle.placa,
                    color = ColorAzulOscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { navController.navigate("editar_vehiculo") },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar datos del vehículo",
                        tint = ColorAzulOscuro
                    )
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar vehículo",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
        }

        // ---------------- DIALOG ELIMINAR ----------------
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar vehículo") },
                text = {
                    Text(
                        "¿Seguro que deseas eliminar el vehículo ${selectedVehicle.placa}? " +
                                "Esta acción no se puede deshacer."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.onDeleteConfirm(selectedVehicle.id)
                        },
                        enabled = !uiState.isDeleting
                    ) {
                        if (uiState.isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text("Eliminar", color = Color(0xFFD32F2F))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false },
                        enabled = !uiState.isDeleting
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // (Opcional) Mostrar error simple
        if (!uiState.errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = uiState.errorMessage ?: "",
                color = Color(0xFFD32F2F),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---------------- DATOS DEL VEHÍCULO ----------------
        InfoDatoFila("Marca", selectedVehicle.marca ?: "-")
        InfoDatoFila("Nombre", selectedVehicle.nombre_vehiculo)
        InfoDatoFila("Año", selectedVehicle.anio?.toString() ?: "-", mostrarDividerFinal = true)

        Spacer(modifier = Modifier.height(16.dp))

        // ---------------- HOLOGRAMA (chips) ----------------
        Text(
            text = "Holograma",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))

        val holograma = selectedVehicle.holograma

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HoloChip("E", activo = isHoloActive("E", holograma))
            HoloChip("00", activo = isHoloActive("00", holograma))
            HoloChip("0", activo = isHoloActive("0", holograma))
            HoloChip("1", activo = isHoloActive("1", holograma))
            HoloChip("2", activo = isHoloActive("2", holograma))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- PERIODO DE VERIFICACIÓN ----------------
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
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.widthIn(min = 48.dp)) {
                        Text("1er", color = ColorAzulOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("2do", color = ColorAzulOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(verifInfo.primerPeriodo, color = ColorAzulOscuro, fontSize = 14.sp)
                        Text(verifInfo.segundoPeriodo, color = ColorAzulOscuro, fontSize = 14.sp)
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

        // ---------------- HOY NO CIRCULA ----------------
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
private fun EmptyVehicleState(onAddVehicle: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
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

        Surface(color = ColorAzulOscuro, shape = RoundedCornerShape(50)) {
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

@Composable
private fun InfoDatoFila(etiqueta: String, valor: String, mostrarDividerFinal: Boolean = false) {
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
    Surface(color = bg, shape = RoundedCornerShape(50)) {
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

private fun isHoloActive(chipLabel: String, hologramaDb: String?): Boolean {
    if (hologramaDb == null) return false
    return when (chipLabel) {
        "E" -> hologramaDb.equals("Exento", ignoreCase = true)
        else -> hologramaDb.equals(chipLabel, ignoreCase = true)
    }
}

// =======================
// LÓGICA DE NEGOCIO (igual que tu código)
// =======================
fun buildVerificacionInfo(placa: String?): VerificacionInfo? {
    val lastDigit = placa?.lastOrNull { it.isDigit() } ?: return null
    return when (lastDigit) {
        '5', '6' -> VerificacionInfo("5 y 6", Color(0xFFFFF176), "Enero y Febrero", "Julio y Agosto")
        '7', '8' -> VerificacionInfo("7 y 8", Color(0xFFF48FB1), "Febrero y Marzo", "Agosto y Septiembre")
        '3', '4' -> VerificacionInfo("3 y 4", Color(0xFFE57373), "Marzo y Abril", "Septiembre y Octubre")
        '1', '2' -> VerificacionInfo("1 y 2", Color(0xFF81C784), "Abril y Mayo", "Octubre y Noviembre")
        '9', '0' -> VerificacionInfo("9 y 0", Color(0xFF64B5F6), "Mayo y Junio", "Noviembre y Diciembre")
        else -> null
    }
}

fun buildHoyNoCirculaInfo(placa: String?, hologramaDb: String?): HoyNoCirculaInfo {
    if (hologramaDb == null) {
        return HoyNoCirculaInfo("No hay información de holograma.", null, null)
    }

    if (hologramaDb.equals("Exento", true) || hologramaDb == "0" || hologramaDb == "00") {
        return HoyNoCirculaInfo("Circulas todos los días", null, null)
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

    val detalleEntreSemana = diaSemana?.let { "Entre semana no circulas los $it." }

    val detalleSabado = when (hologramaDb) {
        "1" -> if (lastDigit != null && lastDigit in listOf('1', '3', '5', '7', '9')) {
            "No circulas los sábados 1 y 3 de cada mes."
        } else {
            "No circulas los sábados 2 y 4 de cada mes."
        }
        "2" -> "No circulas todos los sábados del mes."
        else -> null
    }

    return HoyNoCirculaInfo(
        resumen = "${detalleEntreSemana.orEmpty()} ${detalleSabado.orEmpty()}".trim(),
        detalleEntreSemana = detalleEntreSemana,
        detalleSabado = detalleSabado
    )
}
