package com.example.appmovilsiivmex.ui.screens.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onGoToLogin: () -> Unit = {},
    onContinue: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var acceptedTerms by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.registerSuccess) {
        if(uiState.registerSuccess) onContinue(uiState.email)
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(75.dp))
            TitleAndSubtitle()
            Spacer(Modifier.height(20.dp))

            FilledInput(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = "Nombre Completo",
                placeholder = "",
                leading = { Icon(Icons.Outlined.Person, contentDescription = null, tint = ColorGris) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                error = null,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            FilledInput(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                placeholder = "ejemplo@gmail.com",
                label = "Correo electrónico",
                leading = { Icon(Icons.Outlined.Email, contentDescription = null, tint = ColorGris) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                error = uiState.emailError,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            PasswordFilledInput(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                visible = uiState.showPassword1,
                onToggleVisibility = viewModel::onTogglePasswordVisibility1,
                placeholder = "Contraseña",
                error = uiState.passwordError,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            PasswordFilledInput(
                value = uiState.confirm,
                onValueChange = viewModel::onConfirmChange,
                visible = uiState.showPassword2,
                onToggleVisibility = viewModel::onTogglePasswordVisibility2,
                placeholder = "Confirmar contraseña",
                error = uiState.confirmError,
                enabled = !uiState.isLoading
            )
            TermsAndConditionsRow(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                onClickTerms = { showTermsDialog = true }
            )
            if (uiState.registerError != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = uiState.registerError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(24.dp))

            PrimaryButton(
                isLoading = uiState.isLoading,
                enabled = acceptedTerms && uiState.isRegisterEnabled,
                onClick = viewModel::onRegisterClick
            )
            Spacer(Modifier.height(16.dp))

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

            if (showTermsDialog) {
                AlertDialog(
                    onDismissRequest = { showTermsDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showTermsDialog = false }) {
                            Text("Cerrar")
                        }
                    },
                    title = { Text("Términos y condiciones") },
                    text = {
                        Box(
                            modifier = Modifier
                                .heightIn(max = 400.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "El presente documento establece los términos y condiciones bajo los cuales se permite el uso del sistema desarrollado para la detección de placas vehiculares, búsqueda de información pública asociada y notificación de ubicaciones registradas. Al hacer uso de esta aplicación, el usuario acepta y se compromete a cumplir lo siguiente:\n" +
                                        "1. Del propósito del sistema\n" +
                                        "El sistema ha sido desarrollado con fines académicos y de investigación, como parte de un proyecto escolar. Su uso está destinado exclusivamente a propósitos personales, de prueba o demostración, sin fines comerciales ni de difusión masiva.\n" +
                                        "2. De la privacidad de la información\n" +
                                        "Las placas vehiculares detectadas no serán compartidas con terceros fuera del entorno académico en el que se desarrolla el sistema.\n" +
                                        "La información asociada a la placa (multas, verificaciones, entre otros) es obtenida únicamente de fuentes públicas disponibles en línea.\n" +
                                        "El sistema permite la redistribución de datos sensibles.\n" +
                                        "3. Del uso del correo electrónico\n" +
                                        "El correo electrónico proporcionado por el usuario será utilizado únicamente para fines de autenticación (inicio de sesión) y recuperación de contraseña.\n" +
                                        "No se enviará contenido publicitario no solicitado.\n" +
                                        "El correo electrónico no será compartido con terceros ni utilizado para fines fuera de los mencionados.\n" +
                                        "4. De la responsabilidad del usuario\n" +
                                        "El usuario se compromete a utilizar el sistema de manera ética y conforme a las leyes de protección de datos personales aplicables.\n" +
                                        "Queda prohibido el uso del sistema para monitorear, rastrear o identificar a personas sin su consentimiento.\n" +
                                        "El usuario reconoce que la información proporcionada por el sistema proviene de fuentes externas, por lo que su veracidad y actualidad no pueden garantizarse al 100%.\n" +
                                        "5. De la limitación de responsabilidad\n" +
                                        "Dado que el sistema es un prototipo en desarrollo, los resultados obtenidos pueden contener errores o limitaciones técnicas.\n" +
                                        "Los desarrolladores no se hacen responsables por el mal uso del sistema ni por decisiones tomadas con base en la información entregada.\n" +
                                        "6. De las condiciones de acceso\n" +
                                        "El acceso y uso del sistema implica la aceptación plena de estos términos y condiciones.\n" +
                                        "Estos términos pueden ser modificados en futuras versiones del sistema, con el previo aviso dentro de la aplicación o documentación del proyecto."
                            )
                        }
                    }
                )

            }

        }
    }
}

@Composable
private fun FilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
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
        label = { Text(label) },
        placeholder = { Text(placeholder, color = ColorGris)},
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
private fun TermsAndConditionsRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClickTerms: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = "Acepto los ",
            fontSize = 14.sp,
            color = ColorAzulOscuro
        )
        Text(
            text = "términos y condiciones",
            fontSize = 14.sp,
            color = ColorAzulOscuro,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClickTerms() }
        )
    }
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

@Composable
private fun RegisterIllustration(resId: Int) {
    Image(
        painter = painterResource(resId),
        contentDescription = "Ilustración de registro",
        modifier = Modifier
            .size(0.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Crear cuenta",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        "¡Empieza hoy mismo y lleva el control de tus vehículos!",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}
