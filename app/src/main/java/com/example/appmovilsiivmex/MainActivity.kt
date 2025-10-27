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

                Scaffold(
                    bottomBar = {
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppMovilSIIVMEXTheme {
        Greeting("Android")
    }
}