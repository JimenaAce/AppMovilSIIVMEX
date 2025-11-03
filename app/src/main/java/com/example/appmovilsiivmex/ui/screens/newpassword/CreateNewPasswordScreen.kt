package com.example.appmovilsiivmex.ui.screens.newpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Preview(showBackground = true)
@Composable
fun CreateNewPasswordScreen(
    vm: CreateNewPasswordViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSubmitSuccess: () -> Unit = {}
) {
    val ui by vm.uiState.collectAsState()

    Scaffold(
        topBar = { CreateTopBar(onBack) }
    ) { inner ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(inner)
                .padding(8.dp)
                .padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Illustration(resId = R.drawable.reserpassword_illustration)
            Spacer(Modifier.height(20.dp))

            TitleAndSubtitle(
                title = "Crear nueva contraseña",
                subtitle = "Tu nueva contraseña debe ser diferente a la anterior contraseña"
            )

            Spacer(Modifier.height(24.dp))

            PasswordField(
                value = ui.password,
                onValueChange = vm::onPasswordChange,
                placeholder = "Contraseña",
                visible = ui.showPassword1,
                onToggleVisibility = vm::togglePassword1,
                isError = ui.passwordError != null,
                supporting = ui.passwordError
            )

            Spacer(Modifier.height(20.dp))

            PasswordField(
                value = ui.confirm,
                onValueChange = vm::onConfirmChange,
                placeholder = "Confirmar contraseña",
                visible = ui.showPassword2,
                onToggleVisibility = vm::togglePassword2,
                isError = ui.confirmError != null,
                supporting = ui.confirmError
            )

            Spacer(Modifier.height(22.dp))

            PrimaryButton(
                text = "Enviar",
                enabled = ui.isFormValid && !ui.isLoading,
                isLoading = ui.isLoading
            ) {
                vm.submit(onSuccess = onSubmitSuccess)
            }
        }
    }
}

/* ======= SUB-COMPONENTES ======= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Volver", tint = ColorAzulOscuro)
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
            .fillMaxWidth()
            .aspectRatio(1.4f),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle(title: String, subtitle: String) {
    Text(
        title,
        color = ColorAzulOscuro,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Start
    )
    Spacer(Modifier.height(6.dp))
    Text(subtitle, color = ColorGris, fontSize = 14.sp)
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean,
    supporting: String?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = ColorGris) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Ocultar" else "Mostrar",
                    tint = ColorGris
                )
            }
        },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = isError,
        supportingText = supporting?.let { { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        shape = MaterialTheme.shapes.medium,
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
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = ColorAzulOscuro)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

