package com.example.appmovilsiivmex.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

// ---------------------------------------------------------------------
// Modelo para mostrar el grupo y periodos en la UI
// ---------------------------------------------------------------------
data class GrupoVerificacion(
    val label: String,
    val color: Color,
    val primerPeriodo: String,
    val segundoPeriodo: String
)

fun obtenerGrupoPorPlaca(placa: String?): GrupoVerificacion? {
    val lastDigit = placa?.lastOrNull { it.isDigit() } ?: return null
    return when (lastDigit) {
        '5','6' -> GrupoVerificacion("5 y 6", Color(0xFFFFF176), "Enero y Febrero", "Julio y Agosto")
        '7','8' -> GrupoVerificacion("7 y 8", Color(0xFFF48FB1), "Febrero y Marzo", "Agosto y Septiembre")
        '3','4' -> GrupoVerificacion("3 y 4", Color(0xFFE57373), "Marzo y Abril", "Septiembre y Octubre")
        '1','2' -> GrupoVerificacion("1 y 2", Color(0xFF81C784), "Abril y Mayo", "Octubre y Noviembre")
        '9','0' -> GrupoVerificacion("9 y 0", Color(0xFF64B5F6), "Mayo y Junio", "Noviembre y Diciembre")
        else -> null
    }
}

// ---------------------------------------------------------------------
// Rangos numéricos de meses por grupo
// ---------------------------------------------------------------------
private data class RangoVerificacionMeses(
    val primerPeriodo: IntRange,
    val segundoPeriodo: IntRange
)

private fun obtenerRangosMesesPorPlaca(placa: String?): RangoVerificacionMeses? {
    val lastDigit = placa?.lastOrNull { it.isDigit() } ?: return null
    return when (lastDigit) {
        '5','6' -> RangoVerificacionMeses(1..2, 7..8)
        '7','8' -> RangoVerificacionMeses(2..3, 8..9)
        '3','4' -> RangoVerificacionMeses(3..4, 9..10)
        '1','2' -> RangoVerificacionMeses(4..5, 10..11)
        '9','0' -> RangoVerificacionMeses(5..6, 11..12)
        else -> null
    }
}

// ---------------------------------------------------------------------
// Estado de verificación (UI)
// ---------------------------------------------------------------------
data class EstadoVerificacion(
    val mesesRestantes: Long?,     // 2+ meses
    val diasRestantes: Long?,      // últimos tramos: mismo mes o a 1 mes
    val diasVencida: Long?,        // atraso
    val textoResumen: String
)

@RequiresApi(Build.VERSION_CODES.O)
private fun endOfMonth(year: Int, month: Int): LocalDate =
    YearMonth.of(year, month).atEndOfMonth()

/**
 * Helper UI:
 * - meses == 0 -> mostrar días restantes al límite (NO "0 meses")
 * - meses == 1 -> mostrar días restantes al límite
 * - meses >= 2 -> mostrar meses
 */
@RequiresApi(Build.VERSION_CODES.O)
private fun buildEstadoVigente(hoy: LocalDate, limite: LocalDate): EstadoVerificacion {
    val meses = ChronoUnit.MONTHS.between(
        hoy.withDayOfMonth(1),
        limite.withDayOfMonth(1)
    ).coerceAtLeast(0)

    return when (meses) {
        0L -> {
            val dias = ChronoUnit.DAYS.between(hoy, limite).coerceAtLeast(0)
            EstadoVerificacion(
                mesesRestantes = null,
                diasRestantes = dias,
                diasVencida = null,
                textoResumen = when {
                    dias == 0L -> "Hoy es el último día para verificar"
                    else -> "Te faltan $dias día(s) para verificar"
                }
            )
        }
        1L -> {
            val dias = ChronoUnit.DAYS.between(hoy, limite).coerceAtLeast(1)
            EstadoVerificacion(
                mesesRestantes = null,
                diasRestantes = dias,
                diasVencida = null,
                textoResumen = "Te faltan $dias día(s) para verificar"
            )
        }
        else -> EstadoVerificacion(
            mesesRestantes = meses,
            diasRestantes = null,
            diasVencida = null,
            textoResumen = "Te faltan $meses mes(es) para verificar"
        )
    }
}

/**
 * Para notificaciones:
 * Devuelve la fecha límite del siguiente periodo de verificación.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun obtenerProximoLimiteVerificacion(
    ultimaVerificacion: LocalDate?,
    placa: String?
): LocalDate? {

    if (ultimaVerificacion == null) return null

    val rangos = obtenerRangosMesesPorPlaca(placa)
    if (rangos == null) {
        // fallback: 6 meses desde la última verificación
        return ultimaVerificacion.plusMonths(6)
    }

    val year = ultimaVerificacion.year
    val month = ultimaVerificacion.monthValue
    val firstRange = rangos.primerPeriodo
    val secondRange = rangos.segundoPeriodo

    val verifiedPeriod = if (month <= secondRange.first - 1) 1 else 2

    return when (verifiedPeriod) {
        1 -> endOfMonth(year, secondRange.last)
        else -> endOfMonth(year + 1, firstRange.last)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calcularEstadoVerificacion(
    ultimaVerificacion: LocalDate?,
    placa: String?
): EstadoVerificacion {

    val hoy = LocalDate.now()

    if (ultimaVerificacion == null) {
        return EstadoVerificacion(
            mesesRestantes = null,
            diasRestantes = null,
            diasVencida = null,
            textoResumen = "Ingresa tu última verificación"
        )
    }

    val proximoLimite = obtenerProximoLimiteVerificacion(ultimaVerificacion, placa)
        ?: return EstadoVerificacion(
            mesesRestantes = null,
            diasRestantes = null,
            diasVencida = null,
            textoResumen = "Ingresa tu última verificación"
        )

    return if (!hoy.isAfter(proximoLimite)) {
        buildEstadoVigente(hoy, proximoLimite)
    } else {
        val dias = ChronoUnit.DAYS.between(proximoLimite, hoy).coerceAtLeast(1)
        EstadoVerificacion(
            mesesRestantes = null,
            diasRestantes = null,
            diasVencida = dias,
            textoResumen = "Verificación vencida hace $dias día(s)"
        )
    }
}

// ---------------------------------------------------------------------
// Helpers para mostrar botón "Ya realicé la verificación"
// ---------------------------------------------------------------------

@RequiresApi(Build.VERSION_CODES.O)
fun periodoActualPorPlaca(placa: String?, hoy: LocalDate = LocalDate.now()): Int? {
    val rangos = obtenerRangosMesesPorPlaca(placa) ?: return null
    val m = hoy.monthValue
    return when (m) {
        in rangos.primerPeriodo -> 1
        in rangos.segundoPeriodo -> 2
        else -> null
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun yaVerificoEnEstePeriodo(
    placa: String?,
    ultimaVerificacion: LocalDate?,
    hoy: LocalDate = LocalDate.now()
): Boolean {
    if (ultimaVerificacion == null) return false
    val rangos = obtenerRangosMesesPorPlaca(placa) ?: return false

    val periodoHoy = periodoActualPorPlaca(placa, hoy) ?: return false
    if (ultimaVerificacion.year != hoy.year) return false

    return when (periodoHoy) {
        1 -> ultimaVerificacion.monthValue in rangos.primerPeriodo
        2 -> ultimaVerificacion.monthValue in rangos.segundoPeriodo
        else -> false
    }
}
