package com.example.appmovilsiivmex.ui.screens.verifycode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris
import com.example.appmovilsiivmex.ui.theme.ColorGrisCajaTexto

@Preview(showBackground = true)
@Composable
fun VerifyCodeScreen(
    viewModel: VerifyCodeViewModel = viewModel(),
    onBack: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { VerifyTopBar(onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Illustration(resId = R.drawable.verifycode_illustration)
            Spacer(Modifier.height(10.dp))
            Title()
            Spacer(Modifier.height(40.dp))
            cajasTexto(
                value = ui.code,
                length = 4,
                onValueChange = viewModel::onCodeChange
            )

            Spacer(Modifier.height(30.dp))
            HelperTexts(
                canResend = ui.resendSeconds == 0,
                secondsLeft = ui.resendSeconds,
                onResend = { viewModel.resendCode() }
            )

            Spacer(Modifier.height(60.dp))
            VerifyButton(
                enabled = ui.code.length == 4 && !ui.isLoading,
                isLoading = ui.isLoading,
                onClick = { viewModel.verify(onSuccess = onVerified) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifyTopBar(onBack: () -> Unit) {
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
            .aspectRatio(1.6f),
        contentScale = ContentScale.FillWidth
    )
}


@Composable
private fun Title() {
    Text(
        "Ingrese su código de verificación",
        color = ColorAzulOscuro,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 32.sp
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HelperTexts(
    canResend: Boolean,
    secondsLeft: Int,
    onResend: () -> Unit
) {
    Text(
        "Enviamos un código de verificación al correo. Por favor revisa la bandeja de entrada",
        color = ColorGris,
        fontSize = 14.sp
    )
    Spacer(Modifier.height(10.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¿Si recibiste el código de verificación?",
            color = ColorGris,
            fontSize = 14.sp
        )

        val resendColor = if (canResend) ColorAzulOscuro else ColorGris
        Text(
            text = if (canResend) "Reenviar" else "Reenviar (${secondsLeft}s)",
            color = resendColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(enabled = canResend) { onResend() }
        )
    }
}

@Composable
private fun VerifyButton(
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
            Text("Verificar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun cajasTexto(
    value: String,
    length: Int,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        onValueChange = { new ->
            val filtered = new.filter { it.isDigit() }.take(length)
            onValueChange(filtered)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color.Transparent),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedTextColor = Color.Transparent,
            unfocusedTextColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            cursorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .size(width = 1.dp, height = 1.dp)
            .alpha(0f)                         
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusRequester.requestFocus()
                keyboard?.show()
            },
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(length) { index ->
            val charOrBlank = value.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .background(ColorGrisCajaTexto, RoundedCornerShape(8.dp))
                    .clickable {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = charOrBlank,
                    color = ColorAzulOscuro,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
