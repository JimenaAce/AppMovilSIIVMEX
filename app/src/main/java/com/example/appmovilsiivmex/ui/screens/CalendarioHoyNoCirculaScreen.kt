package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarioHoyNoCirculaScreen (navController: NavController) {
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
                Text("Calendario de Hoy no Circula")
            }
        }
    }
}