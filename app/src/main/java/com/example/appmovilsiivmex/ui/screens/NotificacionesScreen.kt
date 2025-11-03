package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.navigation.NavController
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro

@Composable
fun NotificacionesScreen(
    navController: NavController,
    onMenuClick: () -> Unit = {}
) {
    val fondoApp = Color(0xFFFFFFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ───────── HEADER ─────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // hamburguesa que viene de afuera
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = ColorAzulOscuro
                )
            }

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

            // campana SIN punto porque ya estamos en notificaciones
            IconButton(onClick = { /* ya estamos aquí */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = ColorAzulOscuro
                )
            }

            // avatar
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

        Spacer(modifier = Modifier.height(20.dp))

        // ───────── TÍTULO ─────────
        Text(
            text = "Notificaciones",
            color = ColorAzulOscuro,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ───────── LISTA (estática) ─────────
        NotificacionItem(
            titulo = "Hoy no circula",
            descripcion = "No circulas hoy",
            fecha = "23/04/2025"
        )
        Spacer(modifier = Modifier.height(12.dp))
        NotificacionItem(
            titulo = "Refrendo",
            descripcion = "Tu refrendo vence en 3 días",
            fecha = "22/04/2025"
        )
        Spacer(modifier = Modifier.height(12.dp))
        NotificacionItem(
            titulo = "Verificación",
            descripcion = "Recuerda verificar en agosto",
            fecha = "20/04/2025"
        )
        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun NotificacionItem(
    titulo: String,
    descripcion: String,
    fecha: String
) {
    Surface(
        color = Color(0xFFF3F3F3),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // circulito gris
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD3D3D3))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = titulo,
                    color = ColorAzulOscuro,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = descripcion,
                    color = ColorAzulOscuro.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Text(
                text = fecha,
                color = ColorAzulOscuro.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}
