package com.example.appmovilsiivmex.ui.screens.forgotpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSent: () -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { ForgotTopBar(onBack) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(15.dp))
            Illustration(resId = R.drawable.forgotpassword_illustration)
            Spacer(Modifier.height(14.dp))
            TitleAndSubtitle(
                title = "Restablecer contraseña",
                subtitle = "Por favor ingresa tu correo electrónico para recibir el código de verificación"
            )
            Spacer(Modifier.height(30.dp))
            EmailField(
                value = ui.email,
                onValueChange = viewModel::onEmailChange,
                error = ui.emailError
            )
            Spacer(Modifier.height(40.dp))
            SendButton(
                isLoading = ui.isLoading,
                enabled = ui.email.isNotBlank() && ui.emailError == null,
                onClick = { viewModel.sendReset(onSuccess = onSent) }
            )
            Spacer(Modifier.height(12.dp))
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
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = ColorAzulOscuro
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            navigationIconContentColor = ColorAzulOscuro,
            titleContentColor = ColorAzulOscuro
        )
    )
}


@Composable
private fun Illustration(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.4f),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle(title: String, subtitle: String) {
    Text(
        title,
        textAlign = TextAlign.Start,
        color = ColorAzulOscuro,
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Text(
        subtitle,
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Correo electrónico") },
        leadingIcon = { Icon(Icons.Outlined.Email, null, tint = ColorGris) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = error != null,
        supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) } },
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
private fun SendButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
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
