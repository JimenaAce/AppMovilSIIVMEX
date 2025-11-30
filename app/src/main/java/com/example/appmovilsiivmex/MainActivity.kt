package com.example.appmovilsiivmex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.navigation.BarraNavegacionInferior
import com.example.appmovilsiivmex.navigation.NavegacionAuto
import com.example.appmovilsiivmex.ui.theme.AppMovilSIIVMEXTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            AppMovilSIIVMEXTheme {
                val controladorNavegacion = rememberNavController()
                val backStackEntry = controladorNavegacion.currentBackStackEntryAsState()

                val ruta = backStackEntry.value?.destination?.route?.substringBefore('?')
                val rutaActual = ruta?.substringBefore('/')

                // Lista de rutas sin barra de navegación inferior
                val rutasSinBarraInferior = listOf("inicio_sesion", "registro", "registro_vehiculo", "restablecer_contrasenia", "codigo_verificacion_reestablecer", "codigo_verificacion", "nueva_contrasenia","contrasenia_reestablecida")
                val mostrarBarraInferior = rutaActual?.let { it !in rutasSinBarraInferior } ?: false

                var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
                LaunchedEffect(Unit) {
                    isLoggedIn = sessionManager.isLoggedIn()
                }

                if(isLoggedIn == null){
                    return@AppMovilSIIVMEXTheme
                }

                val startDestination = if (isLoggedIn == true){
                    "mis_vehiculos"
                }else{
                    "inicio_sesion"
                }

                Scaffold(
                    bottomBar = {
                        if(mostrarBarraInferior)
                            BarraNavegacionInferior(controladorNavegacion)
                    }
                ) { paddingValues ->
                    NavegacionAuto(
                        controladorNavegacion = controladorNavegacion,
                        paddingValues = paddingValues,
                        startDestination = startDestination
                    )
                }



            }
        }
    }
}

