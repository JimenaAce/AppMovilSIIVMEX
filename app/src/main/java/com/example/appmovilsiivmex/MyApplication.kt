package com.example.appmovilsiivmex

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.preference.PreferenceManager
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration   // 👈 import importante


@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 🔹 Configuración OSMDroid (OpenStreetMap)


        val ctx = applicationContext
        Configuration.getInstance().load(
            ctx,
            PreferenceManager.getDefaultSharedPreferences(ctx)
        )
        // User-Agent identificable (no usamos BuildConfig)
        Configuration.getInstance().userAgentValue = packageName



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)

            fun createHeadsUpChannel(id: String, name: String, desc: String) {
                val ch = NotificationChannel(
                    id,
                    name,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = desc
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 400, 200, 400)
                    setShowBadge(true)
                    lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
                }
                nm.createNotificationChannel(ch)
            }

            // nuevos IDs para forzar importancia alta sin desinstalar
            createHeadsUpChannel("canal_deteccion_vehiculo", "Ubicación de vehículo", "Detecciones y ubicaciones")
            createHeadsUpChannel("canal_prueba_v2", "Pruebas (v2)", "Notificaciones de prueba")
        }
    }

}