package com.example.appmovilsiivmex.ui.screens.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLinkClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var startExitAnimation by remember { mutableStateOf(false) }

    // Navegar cuando el login es exitoso
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            startExitAnimation = true
            kotlinx.coroutines.delay(300)
            onLoginSuccess()
        }
    }


    // Animación de opacidad
    val alpha by animateFloatAsState(
        targetValue = if (startExitAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "loginAlpha"
    )

    // Animación de escala (ligero zoom-out)
    val scale by animateFloatAsState(
        targetValue = if (startExitAnimation) 0.95f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "loginScale"
    )


    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(15.dp))
            LoginIllustration(resId = R.drawable.logo_app)
            Spacer(Modifier.height(20.dp))


            TitleAndSubtitle()
            Spacer(Modifier.height(25.dp))

            EmailField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                leading = {
                    Icon(
                        Icons.Outlined.Email,
                        contentDescription = null,
                        tint = ColorGris
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                error = uiState.emailError,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(20.dp))

            PasswordField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visible = uiState.showPassword,
                onToggleVisibility = viewModel::onTogglePasswordVisibility,
                error = uiState.passwordError,
                enabled = !uiState.isLoading
            )

            if (uiState.loginError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.loginError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(8.dp))

            ForgotLink(onLinkClick)
            Spacer(Modifier.height(60.dp))

            LoginButton(
                isLoading = uiState.isLoading,
                //enabled = uiState.isLoginEnabled,
                onClick = viewModel::onLoginClick
            )
            Spacer(Modifier.height(10.dp))
            RegisterRow(onRegisterClick = onRegisterClick)
        }
    }
}


@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String?,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text("Correo electrónico") },
        placeholder = { Text("ejemplo@gmail.com", color = ColorGris)},
        leadingIcon = leading,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        supportingText = error?.let { {Text(it)} }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = { Text("Contraseña") },
        placeholder = { Text("**********", color = ColorGris) },
        leadingIcon = {
            Icon(
                Icons.Outlined.Lock,
                contentDescription = null,
                tint = ColorGris
            )
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Ocultar" else "Mostrar",
                    tint = ColorGris
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        supportingText = error?.let { {Text(it)} }
    )
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    //enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        //enabled = enabled && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAzulOscuro),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "Iniciar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
private fun LoginIllustration(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = "Ilustración de login",
        modifier = Modifier
            .size(200.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Iniciar Sesión",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 32.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        "¡Nos alegra verte de nuevo!",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun ForgotLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "¿Olvidaste tu contraseña? ",
            fontSize = 14.sp,
            color = ColorGris
        )
        Text(
            text = "Restablecer",
            fontSize = 14.sp,
            color = ColorAzulOscuro,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
private fun RegisterRow(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "¿No tienes una cuenta aún? ",
            fontSize = 14.sp,
            color = ColorGris
        )
        Text(
            text = "Regístrate",
            fontSize = 14.sp,
            color = ColorAzulOscuro,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}
