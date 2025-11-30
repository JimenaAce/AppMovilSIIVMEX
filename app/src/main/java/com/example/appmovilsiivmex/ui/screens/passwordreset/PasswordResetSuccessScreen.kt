package com.example.appmovilsiivmex.ui.screens.passwordreset

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris
import kotlin.math.min

@Preview(showBackground = true)
@Composable
fun PasswordResetSuccessScreen(
    onGoToLogin: () -> Unit = {}
) {
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(Modifier.height(120.dp))
                AnimatedSuccessIcon()
                Spacer(Modifier.height(5.dp))

                SuccessTitleAndSubtitle(
                    title = "Contraseña restablecida",
                    subtitle = "Su contraseña ha sido restablecida exitosamente"
                )
                Spacer(Modifier.height(60.dp))

                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = onGoToLogin
                )

            }
        }
    }
}


@Composable
private fun AnimatedSuccessIcon() {
    val entryScale = remember { Animatable(0.4f) }
    val entryAlpha = remember { Animatable(0f) }

    // Animación de entrada (una sola vez)
    LaunchedEffect(Unit) {
        entryAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 220)
        )
        entryScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Pulso + anillo exterior infinito
    val infiniteTransition = rememberInfiniteTransition(label = "successPulse")

    // El radio se expande hacia afuera
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )

    // El alpha comienza fuerte y se difumina
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )

    // Escala suave del círculo central
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {

        // Anillo exterior que se expande hacia afuera
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val maxRadius = min(size.width, size.height) / 2.8f * entryScale.value
            val expandingRadius = maxRadius * pulseRadius

            if (expandingRadius > 0f) {
                drawCircle(
                    color = Color.Green.copy(alpha = ringAlpha),
                    radius = expandingRadius,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY)
                )
            }
        }

        // Círculo principal con el check
        Box(
            modifier = Modifier
                .size(112.dp)
                .graphicsLayer {
                    scaleX = entryScale.value * pulseScale
                    scaleY = entryScale.value * pulseScale
                    alpha = entryAlpha.value
                }
                .clip(CircleShape)
                .background(Color.Green.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Contraseña restablecida",
                tint = ColorAzulOscuro,
                modifier = Modifier.size(62.dp)
            )
        }
    }
}
@Composable
private fun SuccessTitleAndSubtitle(title: String, subtitle: String) {
    Text(
        text = title,
        color = ColorAzulOscuro,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = subtitle,
        fontSize = 16.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAzulOscuro)
    ) {
        Text(
            text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}
