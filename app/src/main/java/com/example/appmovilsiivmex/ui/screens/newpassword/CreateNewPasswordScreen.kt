package com.example.appmovilsiivmex.ui.screens.newpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Composable
fun CreateNewPasswordScreen(
    viewModel: CreateNewPasswordViewModel = hiltViewModel(),
    email: String,
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar cuando el cambio de contraseña sea exitoso
    LaunchedEffect(uiState.changePasswordSuccess) {
        if (uiState.changePasswordSuccess) onSubmitSuccess()
    }

    Scaffold(
        topBar = { CreateTopBar(onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(20.dp))
            Illustration(resId = R.drawable.changepassword)
            Spacer(Modifier.height(10.dp))

            TitleAndSubtitle()
            Spacer(Modifier.height(24.dp))

            PasswordFilledInput(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visible = uiState.showPassword1,
                onToggleVisibility = viewModel::onTogglePasswordVisibility1,
                placeholder = "Contraseña",
                error = uiState.passwordError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(20.dp))

            PasswordFilledInput(
                value = uiState.confirm,
                onValueChange = viewModel::onConfirmChange,
                visible = uiState.showPassword2,
                onToggleVisibility = viewModel::onTogglePasswordVisibility2,
                placeholder = "Confirmar contraseña",
                error = uiState.confirmError,
                enabled = !uiState.isLoading
            )

            Spacer(Modifier.height(22.dp))
            PrimaryButton(
                isLoading = uiState.isLoading,
                enabled =  uiState.isChangeEnabled,
                onClick = { viewModel.onSubmitClick(email = email)}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = ColorAzulOscuro)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = ColorAzulOscuro
        )
    )
}

@Composable
private fun Illustration(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .size(200.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Cambiar contraseña",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        "Establezca una nueva contraseña para su cuenta.",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}


@Composable
private fun PasswordFilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    placeholder: String,
    error: String?,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        label = { Text(placeholder) },
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
private fun PrimaryButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
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
            Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

