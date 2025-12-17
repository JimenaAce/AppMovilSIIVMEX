package com.example.appmovilsiivmex.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.ui.screens.calcularEstadoVerificacion
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class VerificacionReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        val vehicleId = intent.getIntExtra("vehicleId", -1)
        if (vehicleId == -1) return

        val placa = intent.getStringExtra("placa") ?: return
        val type = intent.getStringExtra("type") ?: "UNKNOWN"

        val sessionManager = SessionManager(context)
        val dateIso: String? = runBlocking {
            sessionManager.getVehicleLastVerificationDate(vehicleId)
        }

        val ultimaVerificacion = dateIso?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        if (ultimaVerificacion == null) return

        val estado = calcularEstadoVerificacion(
            ultimaVerificacion = ultimaVerificacion,
            placa = placa
        )

        // Permiso Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPerm) return
        }

        val canalId = "canal_verificacion"
        val iconRes = R.drawable.logo_app

        val titulo =
            if (estado.diasVencida != null) "Verificación vencida"
            else "Recordatorio de verificación"

        val mensaje = "Placa $placa: ${estado.textoResumen}"

        val notif = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(iconRes)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notifId = 9100 + (vehicleId % 1000)
        NotificationManagerCompat.from(context).notify(notifId, notif)

        // Reprograma usando el dato real (si ya se volvió vencida, agenda diario)
        VerificacionAlarmScheduler.scheduleVerificacionAlarms(
            context = context,
            vehicleId = vehicleId,
            placa = placa,
            ultimaVerificacion = ultimaVerificacion
        )
    }
}
