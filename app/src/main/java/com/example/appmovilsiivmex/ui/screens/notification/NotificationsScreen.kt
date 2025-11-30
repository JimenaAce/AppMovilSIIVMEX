package com.example.appmovilsiivmex.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.appmovilsiivmex.domain.model.Notification
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificacionesScreen(
    navController: NavController,
    userId: Int = 8, // luego lo sacas de tu SessionManag-er
    onMenuClick: () -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ───────── HEADER ─────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
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

            IconButton(onClick = { /* ya estamos aquí */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = ColorAzulOscuro
                )
            }

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
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ───────── CONTENIDO ─────────
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes notificaciones por el momento.",
                        color = ColorAzulOscuro.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.notifications) { notif ->
                        NotificacionItem(
                            notification = notif,
                            onClick = {
                                if (!notif.leida) {
                                    viewModel.markAsRead(notif.id)
                                }
                                // navController.navigate("detalle_notificacion/${notif.id}")
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificacionItem(
    notification: Notification,
    onClick: () -> Unit
) {
    val isUnread = !notification.leida

    // Colores de la tarjeta según estado
    val cardColor = if (isUnread) {
        Color(0xFFE0F2FE)   // azul claro (no leída)
    } else {
        Color(0xFFF3F4F6)   // gris claro (leída)
    }

    Surface(
        color = cardColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Icono circular a la izquierda
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnread) Color(0xFFBFDBFE)  // azul un poco más fuerte
                        else Color(0xFFE5E7EB)           // gris medio
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (isUnread) Color(0xFF1D4ED8) else Color(0xFF6B7280)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Toda la información de texto en una sola columna
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Primera fila: título + "Nuevo" (si aplica)
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.titulo,
                        color = ColorAzulOscuro,
                        fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (isUnread) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Nuevo",
                            color = Color(0xFF1D4ED8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Mensaje (subtítulo)
                Text(
                    text = notification.mensaje,
                    color = ColorAzulOscuro.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Fecha / hora en una línea aparte
                Text(
                    text = formatFechaHora(notification.fechaCreacion),
                    color = ColorAzulOscuro.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Clip
                )
            }
        }
    }
}



// Puedes mejorar luego la lógica para mostrar "Hoy", "Ayer", etc.
fun formatFechaHora(original: String?): String {
    if (original.isNullOrBlank()) return ""

    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = input.parse(original)
        val output = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale("es", "MX"))
        output.format(date!!)
    } catch (e: Exception) {
        original
    }
}
