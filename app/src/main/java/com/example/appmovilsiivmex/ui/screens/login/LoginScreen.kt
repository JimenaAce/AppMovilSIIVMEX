package com.example.appmovilsiivmex.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onLinkClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar cuando el login sea exitoso
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(8.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(15.dp))
            LoginIllustration(resId = R.drawable.login_illustration)
            Spacer(Modifier.height(32.dp))
            TitleAndSubtitle()
            Spacer(Modifier.height(25.dp))

            EmailField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                error = uiState.emailError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(16.dp))

            PasswordField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visible = uiState.showPassword,
                onToggleVisibility = viewModel::onTogglePasswordVisibility,
                error = uiState.passwordError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(8.dp))
            ForgotLink(onLinkClick)
            Spacer(Modifier.height(24.dp))

            LoginButton(
                isLoading = uiState.isLoading,
                onClick = viewModel::onLoginClick
            )

            Spacer(Modifier.height(8.dp))
            RegisterRow(onRegisterClick = onRegisterClick)
        }

        // Mostrar error si existe
        uiState.loginError?.let { error ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = viewModel::clearError) {
                        Text("OK")
                    }
                }
            ) {
                Text(error)
            }
        }
    }
}

// Sub-composables actualizados
@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        leadingIcon = {
            Icon(
                Icons.Outlined.Email,
                null,
                tint = ColorGris
            )
                      },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        supportingText = error?.let { { Text(it) } }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    error: String?,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Contraseña") },
        leadingIcon = {
            Icon(
                Icons.Outlined.Lock,
                null,
                tint = ColorGris) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (visible) "Ocultar" else "Mostrar",
                    tint = ColorGris
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        enabled = enabled,
        isError = error != null,
        supportingText = error?.let { { Text(it) } }
    )
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAzulOscuro)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                "Iniciar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
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
            .fillMaxWidth(1f)
            .aspectRatio(1.4f),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Iniciar Sesión",
        textAlign = TextAlign.Start,
        color = ColorAzulOscuro,
        fontSize = 34.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
        "Por favor inicia sesión antes de continuar",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Start,
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
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "¿No tienes una cuenta aún? ",
            fontSize = 14.sp
        )

        Text(
            text = "Regístrate",
            fontSize = 13.sp,
            color = ColorAzulOscuro,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}