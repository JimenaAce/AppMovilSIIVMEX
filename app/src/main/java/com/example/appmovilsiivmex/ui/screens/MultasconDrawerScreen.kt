package com.example.appmovilsiivmex.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.appmovilsiivmex.navigation.MenuHamburguesa
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultasconDrawerScreen(
    navController: NavController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuHamburguesa(
                onOpcionSeleccionada = { ruta ->
                    navController.navigate(ruta)
                    scope.launch { drawerState.close() }
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        MultasScreen(
            navController = navController,
            onMenuClick = {
                scope.launch { drawerState.open() }
            }
        )
    }
}
