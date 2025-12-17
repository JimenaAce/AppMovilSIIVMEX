package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta

data class OpcionDrawer(
    val icono: @Composable () -> Unit,
    val texto: String,
    val ruta: String
)

@Composable
fun MenuHamburguesa(
    onOpcionSeleccionada: (String) -> Unit,
    onCerrarSesion: () -> Unit,
    currentRoute: String? = null,
    animationProgress: Float = 1f
) {

    val userName = LocalUserName.current
    val userEmail = LocalUserEmail.current

    val opciones = listOf(
        OpcionDrawer(
            icono = { Icon(Icons.Default.Add, contentDescription = "Agregar vehículo") },
            texto = "Agregar vehículo",
            ruta = "agregar_vehiculo"
        ),
        /*OpcionDrawer(
            icono = { Icon(Icons.Default.DirectionsCar, contentDescription = "Mis vehículos") },
            texto = "Mis vehículos",
            ruta = "mis_vehiculos"
        ),
        
         */
        OpcionDrawer(
            icono = { Icon(Icons.Default.CalendarMonth, contentDescription = "Verificación") },
            texto = "Verificación",
            ruta = "cal_verificacion"
        ),
        OpcionDrawer(
            icono = { Icon(Icons.Default.Block, contentDescription = "Hoy no circula") },
            texto = "Hoy no circula",
            ruta = "cal_hoy_no_circula"
        )
    )

    ModalDrawerSheet(
        drawerContainerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 280.dp, max = 320.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(
                topEnd = 24.dp,
                bottomEnd = 24.dp
            ),
            color = ColorFondoTarjeta,
            tonalElevation = 4.dp,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxHeight()
                .graphicsLayer {
                    // scale suave de entrada/salida
                    scaleX = animationProgress
                    scaleY = animationProgress
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // HANDLE superior
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(ColorAzulOscuro.copy(alpha = 0.2f))
                    )
                }

                // HEADER usuario
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ColorAzulOscuro),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName
                                    .split(" ")
                                    .filter { it.isNotBlank() }
                                    .take(2)
                                    .joinToString("") { it.first().uppercase() },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userName,
                                fontWeight = FontWeight.SemiBold,
                                color = ColorAzulOscuro,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (userEmail.isNotBlank()) {
                                Text(
                                    text = userEmail,
                                    color = ColorAzulOscuro.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = ColorAzulOscuro.copy(alpha = 0.08f)
                )

                // Título sección
                Text(
                    text = "Menú",
                    modifier = Modifier
                        .padding(start = 24.dp, top = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorAzulOscuro.copy(alpha = 0.6f)
                )

                // LISTA limpia de opciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    opciones.forEach { opcion ->
                        val selected = currentRoute == opcion.ruta

                        DrawerMenuItem(
                            icon = opcion.icono,
                            text = opcion.texto,
                            selected = selected,
                            onClick = { onOpcionSeleccionada(opcion.ruta) }
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = ColorAzulOscuro.copy(alpha = 0.08f)
                )

                Text(
                    text = "Cuenta",
                    modifier = Modifier
                        .padding(start = 24.dp, top = 12.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorAzulOscuro.copy(alpha = 0.6f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    DrawerMenuItem(
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        text = "Cerrar sesión",
                        selected = false,
                        onClick = onCerrarSesion,
                        customTextColor = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: @Composable () -> Unit,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    customTextColor: Color? = null
) {
    val baseColor = customTextColor ?: ColorAzulOscuro
    val background = if (selected && customTextColor == null) {
        baseColor.copy(alpha = 0.08f)
    } else {
        Color.Transparent
    }

    val textColor = customTextColor
        ?: if (selected) {
            baseColor
        } else {
            baseColor.copy(alpha = 0.9f)
        }

    Surface(
        color = background,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides textColor) {
                icon()
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected && customTextColor == null)
                    FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
