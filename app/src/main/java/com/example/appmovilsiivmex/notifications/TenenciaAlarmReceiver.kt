package com.example.appmovilsiivmex.notifications


import android.app.PendingIntent
import android.os.Build
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.appmovilsiivmex.MainActivity
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.data.local.SessionManager
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class TenenciaReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        val vehicleId = intent.getIntExtra("vehicleId", -1)
        if (vehicleId == -1) return

        val placa = intent.getStringExtra("placa") ?: return
        val entidad = intent.getStringExtra("entidad") ?: "UNKNOWN"
        intent.getStringExtra("type") ?: "UNKNOWN"
        val year = intent.getIntExtra("year", LocalDate.now().year)

        val sessionManager = SessionManager(context)

        // Si el usuario ya marcó pagado, no notifiques
        val pagada = runBlocking {
            sessionManager.isTenenciaPagada(vehicleId, year)
        }
        if (pagada) return

        // Permiso Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPerm = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!hasPerm) return
        }

        val limite = LocalDate.of(year, 3, 31)
        val hoy = LocalDate.now()

        val diasRestantes = ChronoUnit.DAYS.between(hoy, limite)

        val canalId = "canal_tenencia"
        val iconRes = R.drawable.logo_app

        val titulo = when {
            hoy.isAfter(limite) -> "Tenencia / Refrendo vencido"
            diasRestantes == 0L -> "¡Hoy vence el refrendo!"
            diasRestantes == 1L -> "Mañana vence el refrendo"
            else -> "Recordatorio de refrendo"
        }

        val mensaje = when {
            hoy.isAfter(limite) ->
                "Placa $placa: el plazo terminó el $limite. Podrían aplicar tenencia y recargos."
            diasRestantes == 0L ->
                "Placa $placa: hoy ($limite) es el último día para pagar el refrendo."
            diasRestantes == 1L ->
                "Placa $placa: mañana ($limite) es el último día para pagar el refrendo."
            else ->
                "Placa $placa: faltan $diasRestantes días para el límite ($limite)."
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        }
        val tapPending = PendingIntent.getActivity(
            context,
            0,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notif = NotificationCompat.Builder(context, canalId)
            .setSmallIcon(iconRes)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tapPending)
            .build()

        val notifId = 9200 + (vehicleId % 1000)
        NotificationManagerCompat.from(context).notify(notifId, notif)

        // Reprograma (como Verificación)
        TenenciaAlarmScheduler.scheduleTenenciaAlarms(
            context = context,
            vehicleId = vehicleId,
            placa = placa,
            entidadRegistro = entidad,
            year = year
        )
    }
}
