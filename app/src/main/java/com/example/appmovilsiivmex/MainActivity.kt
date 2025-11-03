package com.example.appmovilsiivmex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.appmovilsiivmex.navigation.BarraNavegacionInferior
import com.example.appmovilsiivmex.navigation.NavegacionAuto
import com.example.appmovilsiivmex.ui.theme.AppMovilSIIVMEXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppMovilSIIVMEXTheme {
                val controladorNavegacion = rememberNavController()
                val backStackEntry = controladorNavegacion.currentBackStackEntryAsState()
                val rutaActual = backStackEntry.value?.destination?.route

                // Lista de rutas sin barra de navegación inferior
                val rutasSinBarraInferior = listOf("inicio_sesion", "registro", "registro_vehiculo", "restablecer_contrasenia", "codigo_verificacion", "nueva_contrasenia","contrasenia_reestablecida")

                Scaffold(
                    bottomBar = {
                        if(rutaActual !in rutasSinBarraInferior)
                            BarraNavegacionInferior(controladorNavegacion)
                    }
                ) { paddingValues ->
                    NavegacionAuto(
                        controladorNavegacion = controladorNavegacion,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

// Función para vista previa de una pantalla
@Composable
fun GreetingPreview() {
    AppMovilSIIVMEXTheme {
        Greeting("Android")
    }
}