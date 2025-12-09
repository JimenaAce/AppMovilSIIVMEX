package com.example.appmovilsiivmex.notifications


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

object HoyNoCirculaAlarmScheduler {

    // Horas en las que quieres notificar: 0, 6, 12, 18
    private val HOURS = listOf(0, 6, 12, 18)

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleHoyNoCirculaAlarms(
        context: Context,
        placa: String,
        hologramaDb: String
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // Permiso para alarmas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Abrimos ajustes para que el usuario active el permiso
                openExactAlarmSettings(context)
                return
            }
        }

        // Programamos 0, 6, 12, 18
        for (hour in HOURS) {
            scheduleSingleAlarm(context, placa, hologramaDb, hour)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleSingleAlarm(
        context: Context,
        placa: String,
        hologramaDb: String,
        hour: Int
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val triggerAtMillis = nextTriggerAt(hour)
        val pendingIntent = buildPendingIntent(context, placa, hologramaDb, hour)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun nextTriggerAt(targetHour: Int): Long {
        val now = java.time.ZonedDateTime.now()
        var next = now.withHour(targetHour).withMinute(0).withSecond(0).withNano(0)

        // Si esa hora ya pasó hoy, se agenda para mañana
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }

        return next.toInstant().toEpochMilli()
    }


    private fun buildPendingIntent(
        context: Context,
        placa: String,
        holograma: String,
        hour: Int
    ): PendingIntent {
        val intent = Intent(context, HoyNoCirculaReceiver::class.java).apply {
            putExtra("placa", placa)
            putExtra("holograma", holograma)
            putExtra("hour", hour)
        }

        return PendingIntent.getBroadcast(
            context,
            hour,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
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
