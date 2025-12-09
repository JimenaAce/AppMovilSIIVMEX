package com.example.appmovilsiivmex

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.navigation.BarraNavegacionInferior
import com.example.appmovilsiivmex.navigation.LocalAvatarInitials
import com.example.appmovilsiivmex.navigation.LocalOnVehicleSelected
import com.example.appmovilsiivmex.navigation.NavegacionAuto
import com.example.appmovilsiivmex.navigation.LocalOpenDrawer
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalUserEmail
import com.example.appmovilsiivmex.navigation.LocalUserName
import com.example.appmovilsiivmex.navigation.LocalVehicles
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import com.example.appmovilsiivmex.ui.theme.AppMovilSIIVMEXTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppMovilSIIVMEXTheme {
                val controladorNavegacion = rememberNavController()
                val backStackEntry by controladorNavegacion.currentBackStackEntryAsState()

                val ruta = backStackEntry?.destination?.route?.substringBefore('?')
                val rutaActual = ruta?.substringBefore('/')

                val rutasSinBarraInferior = listOf(
                    "inicio_sesion",
                    "registro",
                    "registro_vehiculo",
                    "restablecer_contrasenia",
                    "codigo_verificacion_reestablecer",
                    "codigo_verificacion",
                    "nueva_contrasenia",
                    "contrasenia_reestablecida",
                    "notificaciones",
                    "agregar_vehiculo",
                    "editar_vehiculo",
                    "cal_hoy_no_circula",
                    "cal_verificacion",
                    "hoy_no_circula"
                )

                val mostrarBarraInferior = rutaActual?.let { it !in rutasSinBarraInferior } ?: false

                var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
                var userName by remember { mutableStateOf<String?>(null) }
                var userEmail by remember { mutableStateOf<String?>(null) }
                var avatarInitials by remember { mutableStateOf("--") }
                val vehicles by remember { sessionManager.vehiclesFlow() }.collectAsState(initial = emptyList())
                val selectedVehicleId by remember { sessionManager.selectedVehicleIdFlow() }.collectAsState(initial = null)


                LaunchedEffect(Unit) {
                    isLoggedIn = sessionManager.isLoggedIn()
                    userName = sessionManager.getUserName()
                    userEmail = sessionManager.getUserEmail()
                    avatarInitials = obtenerIniciales(userName)
                }

                // Solo recarga desde SessionManager cuando haga falta
                LaunchedEffect(rutaActual) {
                    val esRutaPrivada = rutaActual != null && rutaActual !in rutasSinBarraInferior

                    val faltaInfoUsuario = userName.isNullOrBlank() || userEmail.isNullOrBlank()

                    if (esRutaPrivada && faltaInfoUsuario) {
                        val logged = sessionManager.isLoggedIn()
                        isLoggedIn = logged
                        if (logged) {
                            userName = sessionManager.getUserName()
                            userEmail = sessionManager.getUserEmail()
                            avatarInitials = obtenerIniciales(userName)
                        }
                    }


                }


                if (isLoggedIn == null) return@AppMovilSIIVMEXTheme

                val startDestination = if (isLoggedIn == true) "inicio" else "inicio_sesion"

                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val onVehicleSelected: (Int) -> Unit = { vehicleId ->
                    scope.launch {
                        sessionManager.saveSelectedVehicleId(vehicleId)
                    }
                }

                val openDrawer: () -> Unit = {
                    scope.launch { drawerState.open() }
                }

                // Progreso para animar el menú (scale, etc.)
                val animationProgress by animateFloatAsState(
                    targetValue = if (drawerState.targetValue == DrawerValue.Open) 1f else 0.95f,
                    label = "drawerAnimationProgress"
                )

                CompositionLocalProvider(
                    LocalOpenDrawer provides openDrawer,
                    LocalAvatarInitials provides avatarInitials,
                    LocalUserName provides (userName ?: "Usuario"),
                    LocalUserEmail provides (userEmail ?: "usuario@correo.com"),
                    LocalVehicles provides vehicles,
                    LocalSelectedVehicleId provides selectedVehicleId,
                    LocalOnVehicleSelected provides onVehicleSelected
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        scrimColor = Color.Transparent,
                        drawerContent = {
                            MenuHamburguesa(
                                onOpcionSeleccionada = { rutaMenu ->
                                    controladorNavegacion.navigate(rutaMenu) {
                                        launchSingleTop = true
                                    }
                                    scope.launch { drawerState.close() }
                                },
                                onCerrarSesion = {
                                    scope.launch { drawerState.close() }
                                    isLoggedIn = false
                                    userName = null
                                    userEmail = null

                                    controladorNavegacion.navigate("inicio_sesion") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                ,
                                currentRoute = rutaActual,
                                animationProgress = animationProgress
                            )
                        }
                    ) {
                        Box {
                            // Contenido principal
                            Scaffold(
                                bottomBar = {
                                    if (mostrarBarraInferior) {
                                        BarraNavegacionInferior(controladorNavegacion)
                                    }
                                }
                            ) { paddingValues ->
                                NavegacionAuto(
                                    controladorNavegacion = controladorNavegacion,
                                    paddingValues = paddingValues,
                                    startDestination = startDestination
                                )
                            }

                            // ───── SCRIM CON GRADIENTE PERSONALIZADO ─────
                            val shouldShowScrim =
                                drawerState.targetValue == DrawerValue.Open ||
                                        drawerState.currentValue == DrawerValue.Open

                            val scrimAlpha by animateFloatAsState(
                                targetValue = if (shouldShowScrim) 1f else 0f,
                                label = "scrimAlpha"
                            )

                            if (scrimAlpha > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .graphicsLayer { alpha = scrimAlpha }
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color(0xA6000000), // más oscuro arriba
                                                    Color(0x66000000)  // más claro abajo
                                                )
                                            )
                                        )
                                        .clickable {
                                            scope.launch { drawerState.close() }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun obtenerIniciales(nombre: String?): String {
        if (nombre.isNullOrBlank()) return "--"

        return nombre
            .trim()
            .split(" ")
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
            .take(2)
    }

}

