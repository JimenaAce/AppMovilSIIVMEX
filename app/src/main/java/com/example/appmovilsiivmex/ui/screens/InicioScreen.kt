package com.example.appmovilsiivmex.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.NumberPicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.navigation.AppHeader
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicleLastVerificationMap
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.notifications.HoyNoCirculaAlarmScheduler
import com.example.appmovilsiivmex.notifications.VerificacionAlarmScheduler
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.core.net.toUri
import com.example.appmovilsiivmex.notifications.TenenciaAlarmScheduler


@RequiresApi(Build.VERSION_CODES.O)
private data class TenenciaUi(
    val cardText: String,
    val badgeText: String,
    val badgeColor: Color,
    val badgeTextColor: Color,
    val dialogTitle: String,
    val dialogMessage: String,
    val iconTint: Color
)

@RequiresApi(Build.VERSION_CODES.O)
private fun buildTenenciaUi(
    yearActual: Int,
    hoy: LocalDate,
    pagada: Boolean
): TenenciaUi {
    val limite = LocalDate.of(yearActual, 3, 31)

    val verde = Color(0xFF2E7D32)
    val ambar = Color(0xFFFFA000)
    val rojo = Color(0xFFE53935)

    if (pagada) {
        return TenenciaUi(
            cardText = "Pagada ($yearActual)",
            badgeText = "PAGADA",
            badgeColor = Color(0xFFE8F5E9),
            badgeTextColor = verde,
            dialogTitle = "Tenencia / Refrendo",
            dialogMessage = "Marcaste como pagado el refrendo de $yearActual.",
            iconTint = verde
        )
    }

    val diff = ChronoUnit.DAYS.between(hoy, limite) // >0 faltan, 0 hoy, <0 venció

    return when {
        diff > 0 -> {
            val urgente = diff <= 15
            TenenciaUi(
                cardText = "Faltan $diff días",
                badgeText = if (urgente) "URGENTE" else "VIGENTE",
                badgeColor = if (urgente) Color(0xFFFFF3E0) else Color(0xFFE3F2FD),
                badgeTextColor = if (urgente) Color(0xFFFF8F00) else Color(0xFF1565C0),
                dialogTitle = "Tenencia / Refrendo",
                dialogMessage =
                    "Tu fecha límite para pagar el refrendo es el $limite.\n\n" +
                            "Si pagas después, pueden aplicar tenencia y recargos.",
                iconTint = if (urgente) ambar else verde
            )
        }

        diff == 0L -> {
            TenenciaUi(
                cardText = "Vence hoy",
                badgeText = "HOY",
                badgeColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                dialogTitle = "Tenencia / Refrendo",
                dialogMessage = "Hoy ($limite) es el último día para pagar el refrendo y conservar el subsidio.",
                iconTint = rojo
            )
        }

        else -> {
            val vencidaHace = -diff
            TenenciaUi(
                cardText = "Venció hace $vencidaHace días",
                badgeText = "VENCIDA",
                badgeColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                dialogTitle = "Tenencia / Refrendo",
                dialogMessage =
                    "El plazo para conservar el subsidio venció el $limite.\n\n" +
                            "Si no pagaste a tiempo, pueden aplicar tenencia y recargos.",
                iconTint = rojo
            )
        }
    }
}

@Composable
private fun TenenciaBadge(text: String, bg: Color, fg: Color) {
    Surface(
        color = bg,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ============================
// InicioScreen
// ============================
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InicioScreen(
    navController: NavController
) {
    val fondoApp = Color.White
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Vehículo seleccionado
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val selectedVehicle =
        vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    val placa = selectedVehicle?.placa
    val hologramaDb = selectedVehicle?.holograma
    val restringidoHoy = tieneRestriccionHoy(placa, hologramaDb)

    // ============================
    // VERIFICACIÓN: estado + fecha
    // ============================
    val lastVerifMap = LocalVehicleLastVerificationMap.current
    val ultimaVerificacion = selectedVehicle?.id
        ?.let { id -> lastVerifMap[id]?.let { LocalDate.parse(it) } }

    val estadoVerif = calcularEstadoVerificacion(ultimaVerificacion, placa)

    val verifIconTint = when {
        estadoVerif.diasVencida != null -> Color(0xFFD32F2F)   // rojo
        ultimaVerificacion == null      -> Color(0xFFFFA000)   // ámbar
        else                            -> Color(0xFF2E7D32)   // verde
    }

    // Control para mostrar el diálogo de selección de mes / año
    var mostrarDialogoVerif by remember { mutableStateOf(false) }

    val descripcionHoy = when {
        selectedVehicle == null ->
            "Registra un vehículo para ver tu estado"
        hologramaDb.isNullOrBlank() ->
            "Sin información de holograma"
        !restringidoHoy ->
            "Puedes circular sin restricciones"
        else ->
            "No circulas hoy"
    }

    // ============================
    // TENENCIA / REFRENDO
    // ============================
    val yearActual = LocalDate.now().year
    val hoy = LocalDate.now()

    var mostrarDialogoTenencia by remember { mutableStateOf(false) }
    var tenenciaPagada by remember { mutableStateOf(false) }
    var tenenciaDismissed by remember { mutableStateOf(false) }

    LaunchedEffect(selectedVehicle?.id, yearActual) {
        val vId = selectedVehicle?.id ?: return@LaunchedEffect
        tenenciaPagada = sessionManager.isTenenciaPagada(vId, yearActual)
        tenenciaDismissed = sessionManager.isTenenciaAlertDismissed(vId, yearActual)
    }

    val tenenciaUi = remember(tenenciaPagada, yearActual, hoy) {
        buildTenenciaUi(yearActual = yearActual, hoy = hoy, pagada = tenenciaPagada)
    }

    // Mostrar el alert para la tenencia desde el inicio
    LaunchedEffect(selectedVehicle?.id, tenenciaPagada, tenenciaDismissed) {
        if (selectedVehicle != null && !tenenciaPagada && !tenenciaDismissed) {
            mostrarDialogoTenencia = true
        }
    }

    // ============================
    // PERMISO + ALARMAS HOY NO CIRCULA
    // ============================
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted && placa != null && hologramaDb != null) {
                HoyNoCirculaAlarmScheduler.scheduleHoyNoCirculaAlarms(
                    context = context,
                    placa = placa,
                    hologramaDb = hologramaDb
                )
            }
        }

    LaunchedEffect(placa, hologramaDb) {
        if (placa != null && hologramaDb != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasPermission) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@LaunchedEffect
                }
            }

            HoyNoCirculaAlarmScheduler.scheduleHoyNoCirculaAlarms(
                context = context,
                placa = placa,
                hologramaDb = hologramaDb
            )

            VerificacionAlarmScheduler.scheduleVerificacionAlarms(
                context = context,
                vehicleId = selectedVehicle.id,
                placa = placa,
                ultimaVerificacion = ultimaVerificacion
            )

            TenenciaAlarmScheduler.scheduleTenenciaAlarms(
                context = context,
                vehicleId = selectedVehicle.id,
                placa = placa,
                entidadRegistro = selectedVehicle.entidad_registro,
                year = LocalDate.now().year
            )


        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        AppHeader(
            navController = navController,
            showMenu = true,
            showNotificationDot = false
        )

        Spacer(modifier = Modifier.height(35.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¡Hola!",
                color = ColorAzulOscuro,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Qué gusto tenerte aquí.",
                color = ColorAzulOscuro,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {

            // Fila 1: Hoy no circula / Verificación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    titulo = "Hoy no circula",
                    icono = {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Auto",
                            tint = if (restringidoHoy) Color(0xFFE53935) else ColorAzulOscuro,
                            modifier = Modifier.size(60.dp)
                        )
                    },
                    descripcion = descripcionHoy,
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("hoy_no_circula") }
                )

                InfoCard(
                    titulo = "Verificación",
                    icono = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Estado de verificación",
                            tint = verifIconTint,
                            modifier = Modifier.size(50.dp)
                        )
                    },
                    descripcion = estadoVerif.textoResumen,
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (ultimaVerificacion == null) {
                            mostrarDialogoVerif = true
                        } else {
                            navController.navigate("verificacion")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila 2: Tenencia / Multas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    titulo = "Tenencia",
                    icono = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = "Tenencia",
                            tint = tenenciaUi.iconTint,
                            modifier = Modifier.size(60.dp)
                        )
                    },
                    descripcion = tenenciaUi.cardText,
                    badgeTexto = tenenciaUi.badgeText,
                    badgeColor = tenenciaUi.badgeColor,
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarDialogoTenencia = true }
                )

                InfoCard(
                    titulo = "Multas",
                    icono = {
                        Text(
                            text = if (placa.equals("MNJ421A")) "0" else "5",
                            color = if (placa.equals("MNJ421A")) ColorAzulOscuro else Color.Red,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    descripcion = if (placa.equals("MNJ421A")) "Sin adeudos" else "Tienes adeudos pendientes",
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate("multas") }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // ============================
    // Diálogo selección Mes / Año (Verificación)
    // ============================
    if (mostrarDialogoVerif && selectedVehicle != null) {
        DialogSeleccionMesAnio(
            onCancel = { mostrarDialogoVerif = false },
            onConfirm = { year, month ->
                val date = LocalDate.of(year, month, 1)
                mostrarDialogoVerif = false

                scope.launch {
                    sessionManager.saveVehicleLastVerificationDate(
                        vehicleId = selectedVehicle.id,
                        dateIso = date.toString()
                    )
                }

                navController.navigate("verificacion")
            }
        )
    }

    // ============================
    // AlertDialog Tenencia
    // ============================
    if (mostrarDialogoTenencia && selectedVehicle != null) {

        val urlPortal = when (selectedVehicle.entidad_registro.trim().uppercase()) {
            "EDOMEX", "ESTADO DE MÉXICO", "ESTADO DE MEXICO" ->
                "https://edomex.gob.mx/PagoDeTenencia2025"
            else ->
                "https://data.finanzas.cdmx.gob.mx/formato_lc/vehicular/40_5"
        }

        AlertDialog(
            onDismissRequest = { mostrarDialogoTenencia = false },
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = tenenciaUi.iconTint.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = tenenciaUi.iconTint,
                            modifier = Modifier.padding(10.dp).size(22.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tenenciaUi.dialogTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(6.dp))
                        TenenciaBadge(
                            text = tenenciaUi.badgeText,
                            bg = tenenciaUi.badgeColor,
                            fg = tenenciaUi.badgeTextColor
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = tenenciaUi.dialogMessage,
                        fontSize = 14.sp,
                        color = Color(0xFF4A4A4A),
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    Text(
                        text = if (tenenciaPagada)
                            "Si fue un error, puedes dejarlo como pendiente."
                        else
                            "¿Ya pagaste el refrendo de este año?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            sessionManager.setTenenciaPagada(selectedVehicle.id, yearActual, true)
                            sessionManager.setTenenciaAlertDismissed(selectedVehicle.id, yearActual, true)
                        }
                        tenenciaPagada = true
                        mostrarDialogoTenencia = false
                    }
                ) {
                    Text("Sí, ya pagué")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, urlPortal.toUri())
                        context.startActivity(intent)
                    }) {
                        Text("Ir al portal")
                    }

                    TextButton(onClick = {
                        scope.launch {
                            sessionManager.setTenenciaAlertDismissed(selectedVehicle.id, yearActual, true)
                        }
                        tenenciaDismissed = true
                        mostrarDialogoTenencia = false
                    }) {
                        Text("Ahora no")
                    }
                }
            }
        )
    }
}

// ---------------------------------------------------------------------
// Card reusable
// ---------------------------------------------------------------------
@Composable
private fun InfoCard(
    titulo: String,
    icono: @Composable () -> Unit,
    descripcion: String,
    badgeTexto: String?,
    badgeColor: Color?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        color = ColorFondoTarjeta,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 0.dp,
        modifier = modifier
            .height(170.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5E5),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = titulo,
                color = ColorAzulOscuro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                icono()
            }

            if (badgeTexto != null && badgeColor != null) {
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = badgeTexto,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = descripcion,
                color = ColorAzulOscuro.copy(alpha = 0.8f),
                fontSize = 14.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ---------------------------------------------------------------------
// Diálogo simple de selección Mes / Año
// ---------------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DialogSeleccionMesAnio(
    onCancel: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    val meses = listOf(
        "ene.", "feb.", "mar.", "abr.", "may.", "jun.",
        "jul.", "ago.", "sep.", "oct.", "nov.", "dic."
    )

    var mesIndex by remember { mutableIntStateOf(LocalDate.now().monthValue - 1) }

    val currentYear = LocalDate.now().year
    val years = (currentYear - 5..currentYear + 10).toList()
    var yearIndex by remember { mutableIntStateOf(years.indexOf(currentYear)) }

    AlertDialog(
        onDismissRequest = onCancel,
        modifier = Modifier.fillMaxWidth(0.9f),
        title = {
            Text(
                text = "Fecha de última verificación",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Indica el mes y año en que verificaste por última vez tu vehículo.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InlineWheelPicker(
                        label = "Mes",
                        items = meses,
                        selectedIndex = mesIndex,
                        onSelectedChange = { mesIndex = it }
                    )

                    InlineWheelPicker(
                        label = "Año",
                        items = years.map { it.toString() },
                        selectedIndex = yearIndex,
                        onSelectedChange = { yearIndex = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(years[yearIndex], mesIndex + 1) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun InlineWheelPicker(
    label: String,
    items: List<String>,
    selectedIndex: Int,
    onSelectedChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = Color(0xFF8A8A8A)
        )

        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF7F7F7),
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        NumberPicker(context).apply {
                            minValue = 0
                            maxValue = (items.size - 1).coerceAtLeast(0)
                            displayedValues = items.toTypedArray()
                            value = selectedIndex.coerceIn(0, maxValue)

                            wrapSelectorWheel = true
                            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

                            setOnValueChangedListener { _, _, newVal ->
                                onSelectedChange(newVal)
                            }
                        }
                    },
                    update = { picker ->
                        val clamped = selectedIndex.coerceIn(picker.minValue, picker.maxValue)
                        if (picker.value != clamped) picker.value = clamped
                        if (picker.displayedValues?.size != items.size) {
                            picker.displayedValues = items.toTypedArray()
                        }
                    }
                )
            }
        }
    }
}
