package com.example.appmovilsiivmex.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.appmovilsiivmex.ui.screens.obtenerProximoLimiteVerificacion
import java.time.LocalDate
import java.time.ZoneId

object VerificacionAlarmScheduler {

    private const val HOUR_NOTIFY = 9
    private const val MIN_NOTIFY = 0

    // 7 días antes, 1 día antes, el mismo día del límite
    private val REMINDERS_DAYS_BEFORE = listOf(7L, 1L, 0L)

    // Si está vencida: notificar diario a las 09:00
    private const val OVERDUE_TYPE = "OVERDUE_DAILY"

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleVerificacionAlarms(
        context: Context,
        vehicleId: Int,
        placa: String,
        ultimaVerificacion: LocalDate?
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                openExactAlarmSettings(context)
                return
            }
        }

        // Evitar duplicados: cancelamos por VEHICLE ID
        cancelAllForVehicle(context, vehicleId)

        val limite = obtenerProximoLimiteVerificacion(ultimaVerificacion, placa) ?: return
        val hoy = LocalDate.now()

        // Si ya venció → agenda diario
        if (hoy.isAfter(limite)) {
            scheduleOverdueDaily(context, vehicleId, placa)
            return
        }

        // Agenda 7d, 1d, 0d
        for (d in REMINDERS_DAYS_BEFORE) {
            val triggerDate = limite.minusDays(d)
            scheduleOnDate(context, vehicleId, placa, triggerDate, "BEFORE_$d")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleOnDate(
        context: Context,
        vehicleId: Int,
        placa: String,
        triggerDate: LocalDate,
        type: String
    ) {
        val zone = ZoneId.systemDefault()
        val triggerMillis = triggerDate
            .atTime(HOUR_NOTIFY, MIN_NOTIFY)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()

        if (triggerMillis <= System.currentTimeMillis()) return

        scheduleExact(context, vehicleId, placa, triggerMillis, type)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleOverdueDaily(context: Context, vehicleId: Int, placa: String) {
        val now = java.time.ZonedDateTime.now()
        var next = now.withHour(HOUR_NOTIFY).withMinute(MIN_NOTIFY).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)

        scheduleExact(context, vehicleId, placa, next.toInstant().toEpochMilli(), OVERDUE_TYPE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleExact(
        context: Context,
        vehicleId: Int,
        placa: String,
        triggerAtMillis: Long,
        type: String
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pi = buildPendingIntent(context, vehicleId, placa, type)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pi
        )
    }

    private fun buildPendingIntent(
        context: Context,
        vehicleId: Int,
        placa: String,
        type: String
    ): PendingIntent {
        val intent = Intent(context, VerificacionReceiver::class.java).apply {
            putExtra("vehicleId", vehicleId)
            putExtra("placa", placa)
            putExtra("type", type)
        }

        // requestCode estable por vehículo + tipo
        val requestCode = (vehicleId * 31 + type.hashCode())

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelAllForVehicle(context: Context, vehicleId: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val types = listOf("BEFORE_7", "BEFORE_1", "BEFORE_0", OVERDUE_TYPE)

        for (type in types) {
            val requestCode = (vehicleId * 31 + type.hashCode())
            val intent = Intent(context, VerificacionReceiver::class.java)
            val pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pi != null) {
                alarmManager.cancel(pi)
                pi.cancel()
            }
        }
    }

    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
