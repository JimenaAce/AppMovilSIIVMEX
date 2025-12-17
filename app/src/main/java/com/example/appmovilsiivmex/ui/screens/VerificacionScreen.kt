package com.example.appmovilsiivmex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicleLastVerificationMap
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.notifications.VerificacionAlarmScheduler
import com.example.appmovilsiivmex.ui.screens.editvehicle.EditVehicleViewModel
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VerificacionScreen(
    onBack: () -> Unit,
    openCalendar: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    val editVm: EditVehicleViewModel = hiltViewModel()

    // Vehículo seleccionado
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val selectedVehicle =
        vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    val vehicleId = selectedVehicle?.id
    val placa = selectedVehicle?.placa
    val holograma = selectedVehicle?.holograma

    // Fecha de última verificación desde el CompositionLocal
    val lastVerifMap = LocalVehicleLastVerificationMap.current
    val ultimaVerificacion: LocalDate? = vehicleId
        ?.let { id -> lastVerifMap[id]?.let { LocalDate.parse(it) } }

    val grupo = obtenerGrupoPorPlaca(placa)
    val estado = calcularEstadoVerificacion(ultimaVerificacion, placa)

    // Mostrar botón si:
    // - está vencida (diasVencida != null)   O
    // - estamos en periodo y aún no verificó ese periodo
    val hoy = LocalDate.now()
    val periodoHoy = periodoActualPorPlaca(placa, hoy)
    val yaVerificoPeriodo = yaVerificoEnEstePeriodo(placa, ultimaVerificacion, hoy)

    val mostrarBoton = (
            vehicleId != null && ((estado.diasVencida != null) ||
                    (periodoHoy != null && !yaVerificoPeriodo))
            )

    // Dialog hologramas
    var showDialog by remember { mutableStateOf(false) }
    var hologramaSeleccionado by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Atrás")
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Verificación",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorAzulOscuro
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = openCalendar) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendario")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta grande (meses / días)
            Surface(
                color = ColorFondoTarjeta,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val vigente = (ultimaVerificacion != null && estado.diasVencida == null)

                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight(0.8f)
                            .background(
                                when {
                                    ultimaVerificacion == null -> Color(0xFFFFA000) // ámbar
                                    vigente -> Color(0xFF4CAF50)                    // verde
                                    else -> Color(0xFFE53935)                       // rojo
                                },
                                RoundedCornerShape(8.dp)
                            )
                    )

                    Spacer(Modifier.width(16.dp))

                    // Prioridad:
                    // vencida -> días restantes -> meses
                    val numeroGrande = when {
                        ultimaVerificacion == null -> "-"
                        estado.diasVencida != null -> estado.diasVencida.toString()
                        estado.diasRestantes != null -> estado.diasRestantes.toString()
                        estado.mesesRestantes != null -> estado.mesesRestantes.toString()
                        else -> "-"
                    }

                    val etiqueta = when {
                        ultimaVerificacion == null -> "Ingresa tu última verificación"
                        estado.diasVencida != null -> "días de atraso"
                        estado.diasRestantes != null -> "días para verificar"
                        estado.mesesRestantes != null -> "meses para verificar"
                        else -> "sin información"
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = numeroGrande,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorAzulOscuro
                        )
                        Text(
                            text = etiqueta,
                            fontSize = 14.sp,
                            color = ColorAzulOscuro
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = estado.textoResumen,
                color = ColorAzulOscuro.copy(alpha = 0.85f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(14.dp))

            // Botón "Ya realicé la verificación"
            if (mostrarBoton) {
                Button(
                    onClick = {
                        hologramaSeleccionado = null
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Ya realicé la verificación")
                }
                Spacer(Modifier.height(18.dp))
            }

            // Holograma actual
            Text("Holograma", fontWeight = FontWeight.SemiBold, color = ColorGrisTexto)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("E", "00", "0", "1", "2").forEach { value ->
                    val activo = when {
                        holograma.equals("Exento", true) && value == "E" -> true
                        holograma == value -> true
                        else -> false
                    }

                    Surface(
                        color = if (activo) ColorAzulOscuro else ColorChipInactivo,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = value,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (activo) Color.White else Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Periodo de verificación
            Text(
                "Periodo de verificación",
                fontWeight = FontWeight.SemiBold,
                color = ColorGrisTexto
            )
            Spacer(Modifier.height(12.dp))

            if (grupo != null) {
                Row(verticalAlignment = Alignment.Top) {
                    Surface(color = grupo.color, shape = RoundedCornerShape(8.dp)) {
                        Text(
                            text = grupo.label,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.widthIn(min = 48.dp)) {
                                Text("1er", color = ColorAzulOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("2do", color = ColorAzulOscuro, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(grupo.primerPeriodo, color = ColorAzulOscuro, fontSize = 14.sp)
                                Text(grupo.segundoPeriodo, color = ColorAzulOscuro, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog de holograma (seleccionable)
    if (showDialog && vehicleId != null && placa != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Qué holograma te dieron?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Selecciona el holograma obtenido en esta verificación.")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf("E", "00", "0", "1", "2").forEach { opt ->
                            val selected = hologramaSeleccionado == opt
                            Surface(
                                color = if (selected) ColorAzulOscuro else ColorChipInactivo,
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = opt,
                                    color = if (selected) Color.White else Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .clickable { hologramaSeleccionado = opt }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = hologramaSeleccionado != null,
                    onClick = {
                        val opt = hologramaSeleccionado ?: return@TextButton
                        val dateIso = LocalDate.now().withDayOfMonth(1).toString()
                        val holoUi = if (opt == "E") "Exento" else opt

                        scope.launch {
                            // Guardar fecha local (DataStore)
                            sessionManager.saveVehicleLastVerificationDate(
                                vehicleId = vehicleId,
                                dateIso = dateIso
                            )

                            // Actualizar holograma en BACKEND + actualizar sesión
                            editVm.updateHologramFromVerification(
                                vehicleId = vehicleId,
                                hologramaUi = holoUi
                            )

                            //  Reprogramar alarmas con la fecha nueva
                            VerificacionAlarmScheduler.scheduleVerificacionAlarms(
                                context = context,
                                vehicleId = vehicleId,
                                placa = placa,
                                ultimaVerificacion = LocalDate.parse(dateIso)
                            )
                        }

                        showDialog = false
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
