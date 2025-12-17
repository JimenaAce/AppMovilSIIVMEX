package com.example.appmovilsiivmex.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro

@Composable
fun AppHeader(
    navController: NavController,
    modifier: Modifier = Modifier,
    showMenu: Boolean = false,
    showNotificationDot: Boolean = false,
) {
    val openDrawer = LocalOpenDrawer.current
    val avatarInitials = LocalAvatarInitials.current
    val vehicles = LocalVehicles.current
    val selectedVehicleId = LocalSelectedVehicleId.current
    val onVehicleSelected = LocalOnVehicleSelected.current

    var expanded by remember { mutableStateOf(false) }

    val plates = remember(vehicles) {
        vehicles.map { it.placa }
    }

    var selectedPlate by remember(vehicles, selectedVehicleId) {
        val fromSelectedId = vehicles.firstOrNull { it.id == selectedVehicleId }?.placa
        mutableStateOf(fromSelectedId ?: plates.firstOrNull().orEmpty())
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // lado izquierdo (menú)
        Box(
            modifier = Modifier.width(88.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (showMenu) {
                IconButton(
                    onClick = { openDrawer() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = ColorAzulOscuro
                    )
                }
            }
        }

        // centro: selector de placas
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFF5F5F5),
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { expanded = true }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedPlate,
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

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    plates.forEachIndexed { index, plate ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = plate,
                                    color = ColorAzulOscuro,
                                    fontSize = 14.sp,
                                    fontWeight = if (plate == selectedPlate)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = ColorAzulOscuro
                                )
                            },
                            onClick = {
                                selectedPlate = plate
                                expanded = false

                                val selectedVehicle = vehicles.firstOrNull { it.placa == plate }
                                if (selectedVehicle != null) {
                                    onVehicleSelected(selectedVehicle.id)
                                }
                            }
                        )
                        if (index != plates.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                thickness = DividerDefaults.Thickness,
                                color = Color(0xFFE0E6EE)
                            )
                        }
                    }
                }
            }
        }

        // derecha: campana + avatar (igual que ya lo tenías)
        Box(
            modifier = Modifier.width(88.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate("notificaciones") },
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = "Notificaciones",
                            tint = ColorAzulOscuro
                        )
                        if (showNotificationDot) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1A2E47))
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFE0B2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarInitials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorAzulOscuro
                    )
                }
            }
        }
    }
}
