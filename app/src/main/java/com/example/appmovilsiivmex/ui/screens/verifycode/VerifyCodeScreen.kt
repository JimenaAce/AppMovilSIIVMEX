package com.example.appmovilsiivmex.ui.screens.verifycode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris
import com.example.appmovilsiivmex.ui.theme.ColorGrisCajaTexto

@Composable
fun VerifyCodeScreen(
    viewModel: VerifyCodeViewModel = hiltViewModel(),
    email: String,
    onBack: () -> Unit = {},
    onVerified: () -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()

    // Navegar cuando el código de verificación sea correcto
    LaunchedEffect(ui.verifyCodeSuccess) {
        if(ui.verifyCodeSuccess) onVerified()
    }

    Scaffold(
        topBar = { VerifyTopBar(onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(40.dp))
            TitleAndSubtitle()
            Spacer(Modifier.height(50.dp))

            CajasTexto(
                value = ui.code,
                onValueChange = viewModel::onCodeChange
            )
            if (ui.verifyCodeError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = ui.verifyCodeError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(30.dp))

            HelperTexts(
                canResend = ui.resendSeconds == 0,
                secondsLeft = ui.resendSeconds,
                onResend = { viewModel.resendCode(email = email) }
            )
            Spacer(Modifier.height(120.dp))

            VerifyButton(
                enabled = ui.code.length == 4 && !ui.isLoading,
                isLoading = ui.isLoading,
                onClick = { viewModel.verify(email = email) }
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
            .fillMaxWidth()
            .aspectRatio(1.6f),
        contentScale = ContentScale.FillWidth
    )
}


@Composable
private fun TitleAndSubtitle() {
    Text(
        "Verificación de código",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        "Introduzca el código que le hemos enviado por correo electrónico.",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HelperTexts(
    canResend: Boolean,
    secondsLeft: Int,
    onResend: () -> Unit
) {

    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val formatted = "%02d:%02d".format(minutes, seconds)

    Text(
        "¿Si recibiste el código de verificación?",
        color = ColorGris,
        fontSize = 14.sp
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.Center
    ) {
        val resendColor = if (canResend) ColorAzulOscuro else ColorGris
        Text(
            text = if (canResend) "Reenviar" else "Reenviar (${formatted} s)",
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
private fun CajasTexto(
    value: String,
    onValueChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    TextField(
        value = value,
        onValueChange = { new ->
            val filtered = new.filter { it.isDigit() }.take(4)
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
        repeat(4) { index ->
            val charOrBlank = value.getOrNull(index)?.toString() ?: ""
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
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
