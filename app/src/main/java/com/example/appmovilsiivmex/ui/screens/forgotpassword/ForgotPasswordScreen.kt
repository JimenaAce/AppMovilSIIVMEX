package com.example.appmovilsiivmex.ui.screens.forgotpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onSent: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar cuando el correo sea válido
    LaunchedEffect(uiState.forgotPasswordSuccess) {
        if (uiState.forgotPasswordSuccess) onSent(uiState.email)
    }

    Scaffold(
        topBar = { ForgotTopBar(onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Illustration(resId = R.drawable.forgotpassword)
            Spacer(Modifier.height(10.dp))

            TitleAndSubtitle()
            Spacer(Modifier.height(40.dp))

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
            if (uiState.forgotPasswordError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.forgotPasswordError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(25.dp))

            SendButton(
                isLoading = uiState.isLoading,
                //enabled = ui.email.isNotBlank() && ui.emailError == null,
                onClick = viewModel::onSendEmail
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = ColorAzulOscuro
                )
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
        "Reestablecer contraseña",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(5.dp))
    Text(
        "Ingresa tu correo electrónico para reestablecer tu contraseña",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
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
private fun SendButton(
    isLoading: Boolean,
    //enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        //enabled = enabled,
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
            Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
