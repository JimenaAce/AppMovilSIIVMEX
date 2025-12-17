package com.example.appmovilsiivmex.ui.screens
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.DayOfWeek
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
fun tieneRestriccionHoy(
    placa: String?,
    hologramaDb: String?
): Boolean {
    if (placa.isNullOrBlank() || hologramaDb.isNullOrBlank()) return false

    val holo = hologramaDb

    // Exento / 0 / 00 → nunca descansan
    if (holo.equals("Exento", ignoreCase = true) ||
        holo == "0" ||
        holo == "00"
    ) return false

    val lastDigit = placa.lastOrNull { it.isDigit() } ?: return false

    val hoy = LocalDate.now()
    val dia = hoy.dayOfWeek

    // --- LUNES A VIERNES ---
    if (dia in DayOfWeek.MONDAY..DayOfWeek.FRIDAY) {

        val diaDescansoEntreSemana = when (lastDigit) {
            '5', '6' -> DayOfWeek.MONDAY
            '7', '8' -> DayOfWeek.TUESDAY
            '3', '4' -> DayOfWeek.WEDNESDAY
            '1', '2' -> DayOfWeek.THURSDAY
            '9', '0' -> DayOfWeek.FRIDAY
            else -> null
        }

        return diaDescansoEntreSemana != null && dia == diaDescansoEntreSemana
    }

    // --- SÁBADO: reglas sabatinas por holograma ---
    if (dia == DayOfWeek.SATURDAY) {
        // Holograma 2 → todos los sábados
        if (holo == "2") return true

        if (holo == "1") {
            // Calcular qué sábado del mes es (1,2,3,4,5)
            val nthSaturday = (hoy.dayOfMonth - 1) / 7 + 1

            val esImpar = lastDigit in listOf('1','3','5','7','9')
            val esPar   = lastDigit in listOf('0','2','4','6','8')

            // Impar → sábados 1 y 3
            if (esImpar && (nthSaturday == 1 || nthSaturday == 3)) {
                return true
            }
            // Par → sábados 2 y 4
            if (esPar && (nthSaturday == 2 || nthSaturday == 4)) {
                return true
            }
        }

        // otros hologramas
        return false
    }

    // DOMINGO: normalmente sin restricción
    return false
}
