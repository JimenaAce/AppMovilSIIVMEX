package com.example.appmovilsiivmex.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.appmovilsiivmex.ui.screens.AddVehicleScreen
import com.example.appmovilsiivmex.ui.screens.CalendarioHoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.CalendarioVerificacionScreen
import com.example.appmovilsiivmex.ui.screens.HoyNoCirculaScreen
import com.example.appmovilsiivmex.ui.screens.MisVehiculosScreen
import com.example.appmovilsiivmex.ui.screens.PanelScreen
import com.example.appmovilsiivmex.ui.screens.PantallaPlaceholder
import com.example.appmovilsiivmex.ui.screens.InicioScreen
import com.example.appmovilsiivmex.ui.screens.MiAutoScreen
import com.example.appmovilsiivmex.ui.screens.MultasconDrawerScreen
import com.example.appmovilsiivmex.ui.screens.editvehicle.EditVehicleScreen
import com.example.appmovilsiivmex.ui.screens.forgotpassword.ForgotPasswordFlowViewModel
import com.example.appmovilsiivmex.ui.screens.forgotpassword.ForgotPasswordScreen
import com.example.appmovilsiivmex.ui.screens.login.LoginScreen
import com.example.appmovilsiivmex.ui.screens.map.MapScreen
import com.example.appmovilsiivmex.ui.screens.newpassword.CreateNewPasswordScreen
import com.example.appmovilsiivmex.ui.screens.notification.NotificacionesScreen
import com.example.appmovilsiivmex.ui.screens.passwordreset.PasswordResetSuccessScreen
import com.example.appmovilsiivmex.ui.screens.register.RegisterScreen
import com.example.appmovilsiivmex.ui.screens.register.RegistroFlowViewModel
import com.example.appmovilsiivmex.ui.screens.vehicle.VehicleAddScreen
import com.example.appmovilsiivmex.ui.screens.verifycode.VerifyCodeScreen
import com.example.appmovilsiivmex.ui.screens.verifycodereset.VerifyCodeResetScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavegacionAuto(
    controladorNavegacion: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String = "inicio_sesion"
) {


    NavHost(
        navController = controladorNavegacion,
        startDestination = startDestination
    ) {



        // ─────────────────────
        // INICIO DE SESIÓN
        // ─────────────────────
        composable("inicio_sesion") {
            LoginScreen(
                onLoginSuccess = {
                    controladorNavegacion.navigate("inicio") {
                        popUpTo("inicio_sesion") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    controladorNavegacion.navigate("registro"){
                        //popUpTo("inicio_sesion"){ inclusive = true }
                    }
                },
                onLinkClick = {
                    controladorNavegacion.navigate("reestablecer_flow"){
                        //popUpTo("inicio_sesion"){ inclusive = true }
                    }
                }
            )
        }

        // ─────────────────────
        // FLUJO DE REGISTRO
        // ─────────────────────
        navigation(
            startDestination = "registro",
            route = "registro_flow"
        ) {

            // REGISTRO
            composable("registro") { entry ->
                // backStackEntry del gráfico "registro_flow"
                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("registro_flow")
                }
                val viewModel = hiltViewModel<RegistroFlowViewModel>(parentEntry)

                RegisterScreen(
                    onGoToLogin = {
                        controladorNavegacion.navigate("inicio_sesion") {
                            popUpTo("registro_flow") { inclusive = true }
                        }
                    },
                    onContinue = { email ->
                        viewModel.updateEmail(email)
                        controladorNavegacion.navigate("codigo_verificacion")
                    }
                )
            }

            // CÓDIGO DE VERIFICACIÓN
            composable("codigo_verificacion") { entry ->
                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("registro_flow")
                }
                val registroFlowViewModel = hiltViewModel<RegistroFlowViewModel>(parentEntry)

                VerifyCodeScreen(
                    email = registroFlowViewModel.email.orEmpty(),
                    onBack = { controladorNavegacion.popBackStack() },
                    onVerified = {
                        controladorNavegacion.navigate("registro_vehiculo")
                    }
                )
            }

            // REGISTRO VEHÍCULO
            composable("registro_vehiculo") { entry ->
                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("registro_flow")
                }
                val registroFlowViewModel = hiltViewModel<RegistroFlowViewModel>(parentEntry)

                VehicleAddScreen(
                    email = registroFlowViewModel.email.orEmpty(),
                    updateSession = false,
                    onBack = {
                        controladorNavegacion.popBackStack()
                    },
                    onSubmit = {
                        controladorNavegacion.navigate("inicio_sesion") {
                            popUpTo("registro_flow") { inclusive = true }
                        }
                    }
                )
            }
        }


        // ─────────────────────
        // FLUJO RESTABLECER CONTRASEÑA
        // ─────────────────────
        navigation(
            startDestination = "restablecer_contrasenia",
            route = "reestablecer_flow"
        ){

            // ─────────────────────
            // RESTABLECER CONTRASEÑA
            // ─────────────────────
            composable("restablecer_contrasenia"){ entry ->

                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("reestablecer_flow")
                }
                val forgotPasswordFlowViewModel = hiltViewModel<ForgotPasswordFlowViewModel>(parentEntry)

                ForgotPasswordScreen(
                    onBack = {
                        //controladorNavegacion.navigate("inicio_sesion"){
                        //popUpTo("restablecer_contrasenia"){ inclusive = true }
                        //}
                        controladorNavegacion.navigate("inicio_sesion") {
                            popUpTo("inicio_sesion") { inclusive = true }
                        }
                    },
                    onSent = { email ->
                        forgotPasswordFlowViewModel.updateEmail(email)
                        controladorNavegacion.navigate("codigo_verificacion_reestablecer")
                    }
                )
            }


            // ─────────────────────
            // CÓDIGO DE VERIFICACIÓN PARA REESTABLECER CONTRASEÑA
            // ─────────────────────
            composable("codigo_verificacion_reestablecer") { entry ->
                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("reestablecer_flow")
                }
                val forgotPasswordFlowViewModel = hiltViewModel<ForgotPasswordFlowViewModel>(parentEntry)

                VerifyCodeResetScreen(
                    email = forgotPasswordFlowViewModel.email.orEmpty(),
                    onBack = {

                        controladorNavegacion.navigate("restablecer_contrasenia") {
                            popUpTo("reestablecer_flow") { inclusive = true }
                        }
                             },
                    onVerified = { controladorNavegacion.navigate("nueva_contrasenia")}
                )
            }

            // ─────────────────────
            // NUEVA CONTRASENIA
            // ─────────────────────
            composable("nueva_contrasenia"){ entry ->

                val parentEntry = remember(entry) {
                    controladorNavegacion.getBackStackEntry("reestablecer_flow")
                }
                val forgotPasswordFlowViewModel = hiltViewModel<ForgotPasswordFlowViewModel>(parentEntry)

                CreateNewPasswordScreen(
                    email = forgotPasswordFlowViewModel.email.orEmpty(),
                    onBack = {

                        controladorNavegacion.popBackStack()
                        /*
                        controladorNavegacion.navigate("codigo_verificacion"){
                            popUpTo("nueva_contrasenia"){ inclusive = true }
                        }

                         */
                    },
                    onSubmitSuccess = {

                        controladorNavegacion.navigate("contrasenia_reestablecida") {
                            popUpTo("reestablecer_flow") { inclusive = true }
                        }
                    }
                )
            }

            // ─────────────────────
            // CONTRASEÑA REESTABLECIDA
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
        }

        // ─────────────────────
        // INICIO
        // ─────────────────────
        composable("inicio") {
            InicioScreen(navController = controladorNavegacion)
        }

        // ─────────────────────
        // MENÚ LATERAL
        // ─────────────────────

        // AGREGAR VEHÍCULO
        composable("agregar_vehiculo") {
            AddVehicleScreen(
                email = LocalUserEmail.current,
                onBack = { controladorNavegacion.popBackStack() },
                onSuccess = {
                    controladorNavegacion.popBackStack()
                }
            )
        }

        composable("mis_vehiculos") {
            MisVehiculosScreen(controladorNavegacion)
        }
        composable("cal_verificacion") {
            CalendarioVerificacionScreen(onBack = { controladorNavegacion.popBackStack() })
        }
        composable("cal_hoy_no_circula") {
            CalendarioHoyNoCirculaScreen(onBack = { controladorNavegacion.popBackStack() })
        }

        // ─────────────────────
        // NAVEGACIÓN INFERIOR
        // ─────────────────────
        composable("panel") {
            PanelScreen(controladorNavegacion)
        }
        composable("multas") {
            MultasconDrawerScreen(navController = controladorNavegacion)
        }
        composable("ubicacion") {
            MapScreen()
        }
        composable("mi_auto") {
            MiAutoScreen(controladorNavegacion)
        }


        // ─────────────────────
        // EDITAR VEHÍCULO
        // ─────────────────────
        composable("editar_vehiculo") {
            EditVehicleScreen(
                onBack = { controladorNavegacion.popBackStack() },
                onSubmit = {
                    controladorNavegacion.popBackStack()
                }
            )
        }

        // ─────────────────────
        // Notificaciones
        // ─────────────────────
        composable("notificaciones") {
            NotificacionesScreen(
                onBack = { controladorNavegacion.popBackStack() }
            )
        }
        composable("mi_verificacion") {
            PantallaPlaceholder("Verificación")
        }
        composable("hoy_no_circula") {
            HoyNoCirculaScreen(
                onBack = { controladorNavegacion.popBackStack() },
                openCalendar = { controladorNavegacion.navigate("cal_hoy_no_circula")}
            )
        }

    }
}
