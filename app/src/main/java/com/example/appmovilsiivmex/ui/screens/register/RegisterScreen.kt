package com.example.appmovilsiivmex.ui.screens.register

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
import androidx.compose.material.icons.outlined.Person
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
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onGoToLogin: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(8.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Illustration(resId = R.drawable.register_illustration)

            Spacer(Modifier.height(24.dp))
            TitleAndSubtitle()

            Spacer(Modifier.height(20.dp))
            FilledInput(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                placeholder = "Nombre completo",
                leading = { Icon(Icons.Outlined.Person, contentDescription = null, tint = ColorGris) },
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))
            FilledInput(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "Correo electrónico",
                leading = { Icon(Icons.Outlined.Email, contentDescription = null, tint = ColorGris) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.emailError != null,
                supportingText = uiState.emailError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(12.dp))
            PasswordFilledInput(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visible = uiState.showPassword,
                onToggleVisibility = viewModel::onTogglePasswordVisibility,
                placeholder = "Contraseña",
                isError = uiState.passwordError != null,
                supportingText = uiState.passwordError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(24.dp))
            PrimaryButton(
                text = "Continuar",
                isLoading = uiState.isLoading,
                onClick = {
                    viewModel.onRegisterClick()
                    onContinue()
                }
            )

            Spacer(Modifier.height(16.dp))

            // ¿Ya tienes una cuenta? Iniciar Sesión
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Ya tienes una cuenta? ", fontSize = 14.sp, color = ColorGris)
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 14.sp,
                    color = ColorAzulOscuro,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onGoToLogin() }
                )
            }
        }
    }
}

/* ---------- Subcomposables con estilo "filled" (fondo gris, sin borde) ---------- */

@Composable
private fun FilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = { Text(placeholder) },
        leadingIcon = leading,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        enabled = enabled,
        isError = isError,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        )
    )
    if (supportingText != null && isError) {
        Spacer(Modifier.height(4.dp))
        Text(text = supportingText, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
}

@Composable
private fun PasswordFilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    placeholder: String,
    isError: Boolean = false,
    supportingText: String? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = { Text(placeholder) },
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
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        )
    )
    if (supportingText != null && isError) {
        Spacer(Modifier.height(4.dp))
        Text(text = supportingText, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ColorAzulOscuro)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun Illustration(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = "Ilustración",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Registrarse",
        textAlign = TextAlign.Start,
        color = ColorAzulOscuro,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "Regístrate para poder iniciar sesión",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}
