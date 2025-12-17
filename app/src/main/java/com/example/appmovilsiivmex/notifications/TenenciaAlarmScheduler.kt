package com.example.appmovilsiivmex.notifications


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.ZoneId

object TenenciaAlarmScheduler {

    private const val HOUR_NOTIFY = 9
    private const val MIN_NOTIFY = 0

    // 15, 7, 1, 0 días antes del 31 de marzo
    private val REMINDERS_DAYS_BEFORE = listOf(15L, 7L, 1L, 0L)

    // Si ya venció: notificar diario a las 09:00
    private const val OVERDUE_TYPE = "OVERDUE_DAILY"

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleTenenciaAlarms(
        context: Context,
        vehicleId: Int,
        placa: String,
        entidadRegistro: String,
        year: Int = LocalDate.now().year
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                openExactAlarmSettings(context)
                return
            }
        }

        // Evitar duplicados por VEHICLE ID
        cancelAllForVehicle(context, vehicleId, year)

        val limite = LocalDate.of(year, 3, 31)
        val hoy = LocalDate.now()

        // Si ya venció → agenda diario
        if (hoy.isAfter(limite)) {
            scheduleOverdueDaily(context, vehicleId, placa, entidadRegistro, year)
            return
        }

        // Agenda 15d, 7d, 1d, 0d
        for (d in REMINDERS_DAYS_BEFORE) {
            val triggerDate = limite.minusDays(d)
            scheduleOnDate(context, vehicleId, placa, entidadRegistro, year, triggerDate, "BEFORE_$d")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleOnDate(
        context: Context,
        vehicleId: Int,
        placa: String,
        entidadRegistro: String,
        year: Int,
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

        scheduleExact(context, vehicleId, placa, entidadRegistro, year, triggerMillis, type)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleOverdueDaily(
        context: Context,
        vehicleId: Int,
        placa: String,
        entidadRegistro: String,
        year: Int
    ) {
        val now = java.time.ZonedDateTime.now()
        var next = now.withHour(HOUR_NOTIFY).withMinute(MIN_NOTIFY).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)

        scheduleExact(context, vehicleId, placa, entidadRegistro, year, next.toInstant().toEpochMilli(), OVERDUE_TYPE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleExact(
        context: Context,
        vehicleId: Int,
        placa: String,
        entidadRegistro: String,
        year: Int,
        triggerAtMillis: Long,
        type: String
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pi = buildPendingIntent(context, vehicleId, placa, entidadRegistro, year, type)

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
        entidadRegistro: String,
        year: Int,
        type: String
    ): PendingIntent {
        val intent = Intent(context, TenenciaReceiver::class.java).apply {
            putExtra("vehicleId", vehicleId)
            putExtra("placa", placa)
            putExtra("entidad", entidadRegistro)
            putExtra("year", year)
            putExtra("type", type)
        }

        // requestCode estable por vehículo + año + tipo
        val requestCode = (vehicleId * 31 + year * 7 + type.hashCode())

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelAllForVehicle(context: Context, vehicleId: Int, year: Int) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val types = REMINDERS_DAYS_BEFORE.map { "BEFORE_$it" } + OVERDUE_TYPE

        for (type in types) {
            val requestCode = (vehicleId * 31 + year * 7 + type.hashCode())
            val intent = Intent(context, TenenciaReceiver::class.java)
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
