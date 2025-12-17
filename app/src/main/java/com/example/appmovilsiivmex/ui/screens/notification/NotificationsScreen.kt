package com.example.appmovilsiivmex.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.domain.model.Notification
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NotificacionesScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val userId = uiState.userId

    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    Scaffold(
        topBar = { TopBar(onBack)}
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .statusBarsPadding()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            Spacer(modifier = Modifier.height(20.dp))

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

                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp), // compensar el tamaño del icono
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Notificaciones",
                    color = ColorAzulOscuro,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(40.dp) // tamaño típico del botón
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = ColorAzulOscuro
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
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
