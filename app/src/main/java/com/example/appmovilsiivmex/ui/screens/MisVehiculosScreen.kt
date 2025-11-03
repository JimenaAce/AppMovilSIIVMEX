package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import com.example.appmovilsiivmex.ui.theme.AppMovilSIIVMEXTheme
import kotlinx.coroutines.launch

/**
 * Pantalla principal que contiene el menú lateral (drawer) y la navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisVehiculosScreen(navController: NavController) {
    val estadoDrawer = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = estadoDrawer,
        drawerContent = {
            MenuHamburguesa(
                onOpcionSeleccionada = { ruta ->
                    navController.navigate(ruta)
                },
                onCerrarSesion = { /* Acción de cerrar sesión */ }
            )
        }
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = { Text("Bienvenido") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { estadoDrawer.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Pantalla principal (contenido temporal)")
            }
        }
    }
}