package com.example.appmovilsiivmex.notifications


import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.appmovilsiivmex.MainActivity
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.screens.tieneRestriccionHoy

class HoyNoCirculaReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        val placa = intent.getStringExtra("placa") ?: return
        val holograma = intent.getStringExtra("holograma") ?: return
        val hour = intent.getIntExtra("hour", -1)

        val restringido = tieneRestriccionHoy(
            placa = placa,
            hologramaDb = holograma
        )

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        }
        val tapPending = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (restringido) {
            val iconRes = R.drawable.logo_app
            val titulo = "Hoy no circulas"
            val mensaje = "Tu vehículo con placa $placa NO puede circular el día de hoy."

            // Verificar permiso de notificaciones en Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val hasPerm = ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (!hasPerm) {

                } else {
                    val notif = NotificationCompat.Builder(context, "canal_hoy_no_circula")
                        .setSmallIcon(iconRes)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(tapPending)
                        .build()

                    NotificationManagerCompat.from(context).notify(9001, notif)
                }
            } else {
                val notif = NotificationCompat.Builder(context, "canal_hoy_no_circula")
                    .setSmallIcon(iconRes)
                    .setContentTitle(titulo)
                    .setContentText(mensaje)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(tapPending)
                    .build()

                NotificationManagerCompat.from(context).notify(9001, notif)
            }
        }


        if (hour != -1) {
            HoyNoCirculaAlarmScheduler.scheduleSingleAlarm(
                context = context,
                placa = placa,
                hologramaDb = holograma,
                hour = hour
            )
        }
    }
}

