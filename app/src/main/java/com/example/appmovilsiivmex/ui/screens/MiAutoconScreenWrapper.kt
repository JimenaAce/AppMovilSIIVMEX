package com.example.appmovilsiivmex.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiAutoConDrawerScreen(
    navController: NavController
) {
    // igualito que en PanelScreen
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuHamburguesa(
                onOpcionSeleccionada = { ruta ->
                    // navega a la ruta y cierra el drawer
                    navController.navigate(ruta)
                    scope.launch { drawerState.close() }
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        // aquí usamos la pantalla de mi auto
        MiAutoScreen(
            navController = navController,
            onMenuClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}
