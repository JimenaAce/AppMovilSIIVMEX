package com.example.appmovilsiivmex.navigation


import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.appmovilsiivmex.domain.model.Vehicle

val LocalAvatarInitials = compositionLocalOf { "--" }
val LocalUserName = compositionLocalOf { "Usuario" }
val LocalUserEmail = compositionLocalOf { "usuario@correo.com" }

val LocalVehicles = compositionLocalOf<List<Vehicle>> { emptyList() }
val LocalSelectedVehicleId = compositionLocalOf<Int?> { null }
val LocalOnVehicleSelected = staticCompositionLocalOf<(Int) -> Unit> {
    { _ -> }
}
