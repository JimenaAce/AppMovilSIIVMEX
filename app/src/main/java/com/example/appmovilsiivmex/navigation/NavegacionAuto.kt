package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.appmovilsiivmex.ui.screens.CalendarioHoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.CalendarioVerificacionScreen
import com.example.appmovilsiivmex.ui.screens.HoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.MisVehiculosScreen
import com.example.appmovilsiivmex.ui.screens.PanelScreen
import com.example.appmovilsiivmex.ui.screens.PantallaPlaceholder

// mis pantallas nuevas
import com.example.appmovilsiivmex.ui.screens.InicioScreen
import com.example.appmovilsiivmex.ui.screens.EditarVehiculoScreen
import com.example.appmovilsiivmex.ui.screens.MiAutoConDrawerScreen
import com.example.appmovilsiivmex.ui.screens.MultasconDrawerScreen
import com.example.appmovilsiivmex.ui.screens.NotificacionesconDrawerScreen
import com.example.appmovilsiivmex.ui.screens.forgotpassword.ForgotPasswordScreen
import com.example.appmovilsiivmex.ui.screens.login.LoginScreen
import com.example.appmovilsiivmex.ui.screens.newpassword.CreateNewPasswordScreen
import com.example.appmovilsiivmex.ui.screens.passwordreset.PasswordResetSuccessScreen
import com.example.appmovilsiivmex.ui.screens.register.RegisterScreen
import com.example.appmovilsiivmex.ui.screens.vehicle.VehicleAddScreen
import com.example.appmovilsiivmex.ui.screens.verifycode.VerifyCodeScreen

@Composable
fun NavegacionAuto(
    controladorNavegacion: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = controladorNavegacion,
        // dejamos el que traía el main para no romper el flujo original
        startDestination = "inicio_sesion"
    ) {

        // ─────────────────────
        // INICIO DE SESIÓN
        // ─────────────────────
        composable("inicio_sesion") {
            LoginScreen(
                onLoginSuccess = {
                    controladorNavegacion.navigate("mis_vehiculos") {
                        popUpTo("inicio_sesion") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    controladorNavegacion.navigate("registro"){
                        popUpTo("inicio_sesion"){ inclusive = true }
                    }
                },
                onLinkClick = {
                    controladorNavegacion.navigate("restablecer_contrasenia"){
                        popUpTo("inicio_sesion"){ inclusive = true }
                    }
                }
            )
        }

        // ─────────────────────
        // REGISTRO
        // ─────────────────────
        composable("registro") {
            RegisterScreen(
                onGoToLogin = {
                    controladorNavegacion.navigate("inicio_sesion"){
                        popUpTo("registro"){ inclusive = true }
                    }
                },
                onContinue = {
                    controladorNavegacion.navigate("registro_vehiculo"){
                        popUpTo("registro") { inclusive = true }
                    }
                }
            )
        }
        // ─────────────────────
        // AGREGAR VEHÍCULO
        // ─────────────────────
        composable("registro_vehiculo"){
            VehicleAddScreen(
                onBack = {
                    controladorNavegacion.navigate("registro"){
                        popUpTo("registro_vehiculo"){ inclusive = true }
                    }
                },
                onSubmit = {
                    // Lógica de registro, de momento se regresará al inicio de sesión
                    controladorNavegacion.navigate("inicio_sesion"){
                        popUpTo("registro_vehiculo") { inclusive = true }
                    }
                }
            )
        }
        // ─────────────────────
        // RESTABLECER CONTRASEÑA
        // ─────────────────────
        composable("restablecer_contrasenia"){
            ForgotPasswordScreen(
                onBack = {
                    controladorNavegacion.navigate("inicio_sesion"){
                        popUpTo("restablecer_contrasenia"){ inclusive = true }
                    }
                },
                onSent = {
                    // Agregar demás lógica para la base de datos
                    controladorNavegacion.navigate("codigo_verificacion"){
                        popUpTo("restablecer_contrasenia"){ inclusive = true }
                    }
                }
            )
        }
        // ─────────────────────
        // CÓDIGO DE VERIFICACIÓN
        // ─────────────────────
        composable("codigo_verificacion"){
            VerifyCodeScreen(
                onBack = {
                    controladorNavegacion.navigate("restablecer_contrasenia"){
                        popUpTo("codigo_verificacion"){ inclusive = true }
                    }
                },
                onVerified = {
                    // Agregar la lógica de envio de correo así como el envio de la base de datos
                    controladorNavegacion.navigate("nueva_contrasenia"){
                        popUpTo("codigo_verificacion"){ inclusive = true }
                    }
                }
            )
        }
        // ─────────────────────
        // NUEVA CONTRASENIA
        // ─────────────────────
        composable("nueva_contrasenia"){
            CreateNewPasswordScreen(
                onBack = {
                    controladorNavegacion.navigate("codigo_verificacion"){
                        popUpTo("nueva_contrasenia"){ inclusive = true }
                    }
                },
                onSubmitSuccess = {
                    controladorNavegacion.navigate("contrasenia_reestablecida"){
                        popUpTo("nueva_contrasenia"){ inclusive = true }
                    }
                }
            )
        }
        // ─────────────────────
        // NUEVA CONTRASENIA
        // ─────────────────────
        composable("contrasenia_reestablecida"){
            PasswordResetSuccessScreen (
                onGoToLogin = {
                    controladorNavegacion.navigate("inicio_sesion"){
                        popUpTo("contrasenia_reestablecida"){ inclusive = true }
                    }
                }
            )
        }

        // ─────────────────────
        // MENÚ LATERAL (venían del main)
        // ─────────────────────
        composable("mis_vehiculos") {
            MisVehiculosScreen(controladorNavegacion)
        }
        composable("cal_verificacion") {
            CalendarioVerificacionScreen(controladorNavegacion)
        }
        composable("cal_hoy_no_circula") {
            CalendarioHoyNoCirculaScreen(controladorNavegacion)
        }

        // ─────────────────────
        // BOTTOM / PANEL (venía del main)
        // ─────────────────────
        composable("panel") {
            PanelScreen(controladorNavegacion)
        }
        composable("multas") {
            MultasconDrawerScreen(navController = controladorNavegacion)
        }

        composable("mi_verificacion") {
            PantallaPlaceholder("Verificación")
        }
        composable("hoy_no_circula") {
            HoyNoCirculaScreen()
        }
        composable("ubicacion") {
            PantallaPlaceholder("Ubicación")
        }

        // ─────────────────────
        // RUTAS QUE EN MAIN ERAN PLACEHOLDER
        // PERO YA TENEMOS PANTALLA REAL
        // ─────────────────────
        composable("mi_auto") {
            MiAutoConDrawerScreen(navController = controladorNavegacion)
        }


        composable("editar_vehiculo") {
            EditarVehiculoScreen(
                navController = controladorNavegacion,
                placaInicial = "NVW1118",
                marcaInicial = "Volkswagen",
                nombreInicial = "Vehículo",
                anioInicial = "2019",
                hologramaInicial = "0",
                onSave = { _, _, _, _, _ ->
                    // aquí luego guardamos en BD
                }
            )
        }

        // ─────────────────────
        // MIS RUTAS NUEVAS
        // ─────────────────────
        composable("inicio") {
            InicioScreen(navController = controladorNavegacion)
        }

        composable("notificaciones") {
            NotificacionesconDrawerScreen(navController = controladorNavegacion)
        }

        // ─────────────────────
        // RUTA EXTRA DEL MAIN
        // ─────────────────────
        composable("agregar_vehiculo") {
            PantallaPlaceholder("Agregar vehículo")
        }
    }
}
