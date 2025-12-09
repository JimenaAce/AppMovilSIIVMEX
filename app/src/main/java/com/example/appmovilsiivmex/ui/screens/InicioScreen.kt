package com.example.appmovilsiivmex.ui.screens

import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.appmovilsiivmex.navigation.AppHeader
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.notifications.HoyNoCirculaAlarmScheduler
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta
import com.example.appmovilsiivmex.ui.theme.ColorVerde

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InicioScreen(
    navController: NavController
) {
    val fondoApp = Color.White

    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val selectedVehicle =
        vehicles.firstOrNull { it.id == selectedVehicleId } ?: vehicles.firstOrNull()

    val placa = selectedVehicle?.placa
    val hologramaDb = selectedVehicle?.holograma
    val restringidoHoy = tieneRestriccionHoy(placa, hologramaDb)
    val context = LocalContext.current

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

    // Permiso para notificaciones
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // Si el usuario acepta, programamos las alarmas
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
                    // Lanzamos el diálogo de permiso de notificaciones
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    return@LaunchedEffect
                }
            }

            HoyNoCirculaAlarmScheduler.scheduleHoyNoCirculaAlarms(
                context = context,
                placa = placa,
                hologramaDb = hologramaDb
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
                            modifier = Modifier.size(70.dp)
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
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ColorVerde),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verificación vigente",
                                tint = Color.White
                            )
                        }
                    },
                    descripcion = "Verificación vigente",
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f)
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
                            tint = ColorAzulOscuro,
                            modifier = Modifier.size(60.dp)
                        )
                    },
                    descripcion = "Todo al corriente",
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f)
                )

                InfoCard(
                    titulo = "Multas",
                    icono = {
                        Text(
                            text = "0",
                            color = ColorAzulOscuro,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    descripcion = "Sin adeudos",
                    badgeTexto = null,
                    badgeColor = null,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

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
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeTexto,
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
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
