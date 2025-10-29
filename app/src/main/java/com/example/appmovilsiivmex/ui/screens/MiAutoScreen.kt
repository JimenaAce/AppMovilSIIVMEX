package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo
import com.example.appmovilsiivmex.ui.theme.ColorGrisTexto
import com.example.appmovilsiivmex.ui.theme.ColorRosa

@Composable
fun MiAutoScreen(
    navController: NavController
) {
    val fondoApp = Color(0xFFFFFFFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondoApp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ─────────────────────
        // HEADER SUPERIOR (menú, placa, campana, avatar)
        // ─────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Menú hamburguesa
            IconButton(onClick = { /* TODO menú lateral */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                    tint = ColorAzulOscuro
                )
            }

            // Chip con la placa seleccionada
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

            // Notificaciones
            IconButton(onClick = { /* TODO notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.NotificationsNone,
                    contentDescription = "Notificaciones",
                    tint = ColorAzulOscuro
                )
            }

            // Avatar usuario
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
        // IMAGEN DEL AUTO + PLACA + EDITAR
        // ─────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.carrito),
                contentDescription = "Auto del usuario",
                modifier = Modifier
                    .width(220.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Placa grande con ícono de editar
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NVW1118",
                    color = ColorAzulOscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        // Navegar a la pantalla de edición
                        navController.navigate("editar_vehiculo")
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar datos del vehículo",
                        tint = ColorAzulOscuro,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─────────────────────
        // DATOS DEL VEHÍCULO (marca, nombre, año)
        // ─────────────────────
        InfoDatoFila(
            etiqueta = "Marca",
            valor = "Volkswagen"
        )
        InfoDatoFila(
            etiqueta = "Nombre",
            valor = "Vehículo"
        )
        InfoDatoFila(
            etiqueta = "Año",
            valor = "2019",
            mostrarDividerFinal = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ─────────────────────
        // HOLOGRAMA (chips)
        // ─────────────────────
        Text(
            text = "Holograma",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HoloChip("E", activo = false)
            HoloChip("00", activo = false)
            HoloChip("0", activo = true)
            HoloChip("1", activo = false)
            HoloChip("2", activo = false)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ─────────────────────
        // PERIODO DE VERIFICACIÓN
        // ─────────────────────
        Text(
            text = "Periodo de verificación",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorAzulOscuro
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Surface(
                color = ColorRosa,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "7 y 8",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.Top) {
                    Column(modifier = Modifier.widthIn(min = 48.dp)) {
                        Text(
                            text = "1er",
                            color = ColorAzulOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "2do",
                            color = ColorAzulOscuro,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Febrero y Marzo",
                            color = ColorAzulOscuro,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Agosto y Septiembre",
                            color = ColorAzulOscuro,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─────────────────────
        // HOY NO CIRCULA
        // ─────────────────────
        Column {
            Text(
                text = "Hoy no circula",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Circulas todos los días",
                fontSize = 14.sp,
                color = ColorGrisTexto
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// ---------------------------
// Composables auxiliares
// ---------------------------

@Composable
private fun InfoDatoFila(etiqueta: String, valor: String, mostrarDividerFinal: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = etiqueta,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro.copy(alpha = 0.8f)
            )
            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ColorAzulOscuro
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (!mostrarDividerFinal) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HoloChip(texto: String, activo: Boolean) {
    val bg = if (activo) ColorAzulOscuro else ColorChipInactivo
    val fg = if (activo) Color.White else ColorAzulOscuro
    Surface(
        color = bg,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = texto,
            color = fg,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}
