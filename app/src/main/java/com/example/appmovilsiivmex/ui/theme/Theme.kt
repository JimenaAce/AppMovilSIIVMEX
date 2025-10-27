package com.example.appmovilsiivmex.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private val EsquemaColores = lightColorScheme(
    primary = ColorAzulOscuro,
    secondary = ColorVerde,
    tertiary = ColorRosa
)

@Composable
fun AppMovilSIIVMEXTheme (content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaColores,
        typography = MaterialTheme.typography,
        content = content
    )
}
