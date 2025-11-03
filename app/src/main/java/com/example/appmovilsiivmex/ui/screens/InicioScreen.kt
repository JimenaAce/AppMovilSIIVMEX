package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorFondoTarjeta
import com.example.appmovilsiivmex.ui.theme.ColorVerde

@Composable
fun InicioScreen(
    navController: NavController
) {

    // color de fondo general tipo "rosita muy clarito"
    val fondoApp = Color(0xFFFFFCFF)
    // control de notis
    val hayNotificaciones = true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ─────────────────────
        // FILA SUPERIOR (menú, placa, campana, avatar)
        // ─────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // Selector de placa
            Surface(
                shape = RoundedCornerShape(50),
                color = Color(0xFFF5F5F5)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NVW1118",
                        color = ColorAzulOscuro,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Cambiar placa",
                        tint = ColorAzulOscuro
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Campana con badge y navegación
            IconButton(
                onClick = {
                    navController.navigate("notificaciones")
                }
            ) {
                Box {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = "Notificaciones",
                        tint = ColorAzulOscuro
                    )
                    if (hayNotificaciones) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1A2E47))
                        )
                    }
                }
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFE0B2)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JC",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorAzulOscuro
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─────────────────────
        // SALUDO
        // ─────────────────────
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "¡Hola!",
                color = ColorAzulOscuro,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Qué gusto tenerte aquí.",
                color = ColorAzulOscuro,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────
        // GRID DE TARJETAS
        // ─────────────────────

        // Fila 1: Hoy no circula / Verificación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                titulo = "Hoy no circula",
                icono = {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Auto",
                        tint = ColorAzulOscuro,
                        modifier = Modifier.size(40.dp)
                    )
                },
                descripcion = "Puedes circular sin restricciones",
                badgeTexto = null,
                badgeColor = null,
                modifier = Modifier.weight(1f)
            )

            InfoCard(
                titulo = "Verificación",
                icono = {
                    // círculo verde con palomita blanca
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ColorVerde),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Verificación vigente",
                            tint = Color.White
                        )
                    }
                },
                descripcion = "Verificación vigente",
                badgeTexto = null,
                badgeColor = null,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fila 2: Tenencia / Multas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                titulo = "Tenencia",
                icono = {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Tenencia",
                        tint = ColorAzulOscuro,
                        modifier = Modifier.size(40.dp)
                    )
                },
                descripcion = "Todo al corriente",
                badgeTexto = null,
                badgeColor = null,
                modifier = Modifier.weight(1f)
            )

            InfoCard(
                titulo = "Multas",
                icono = {
                    Text(
                        text = "0",
                        color = ColorAzulOscuro,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                descripcion = "Sin adeudos",
                badgeTexto = null,
                badgeColor = null,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

/**
 * Tarjeta reusable para las cajitas tipo dashboard.
 */
@Composable
private fun InfoCard(
    titulo: String,
    icono: @Composable () -> Unit,
    descripcion: String,
    badgeTexto: String?,
    badgeColor: Color?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ColorFondoTarjeta,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.dp,
        modifier = modifier
            .height(160.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFE5E5E5),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = titulo,
                color = ColorAzulOscuro,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                icono()
            }

            if (badgeTexto != null && badgeColor != null) {
                Surface(
                    color = badgeColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = badgeTexto,
                        color = Color.Black,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 4.dp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = descripcion,
                color = ColorAzulOscuro.copy(alpha = 0.75f),
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
        }
    }
}
