package com.example.appmovilsiivmex.ui.screens.vehicle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VehicleAddScreen(
    viewModel: VehicleViewModel = hiltViewModel(),
    email: String,
    onBack: () -> Unit = {},
    onSubmit: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navegar cuando el registro sea exitoso
    LaunchedEffect(uiState.vehicleRegisterSuccess) {
        if(uiState.vehicleRegisterSuccess) onSubmit()
    }

    Scaffold(
        topBar = { CarTopBar(onBack) }
    ){ innerPadding ->

        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            TitleAndSubtitle()
            Spacer(Modifier.height(20.dp))

            PlateFilledInput(
                value = uiState.plate,
                onValueChange = viewModel::onPlateChange,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                error = uiState.plateError,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            FilledInput(
                value = uiState.carName,
                onValueChange = viewModel::onCarNameChange,
                label = "Nombre del vehículo",
                placeholder = "",
                leading = {Icon(Icons.Outlined.DriveFileRenameOutline, null, tint = ColorGris)},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                error = null,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            FilledInput(
                value = uiState.year,
                onValueChange = viewModel::onYearChange,
                label = "Año",
                placeholder = "YYYY",
                leading = {Icon(Icons.Outlined.CalendarMonth, null, tint = ColorGris)},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                error = null,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(12.dp))

            FilledInput(
                value = uiState.brand,
                onValueChange = viewModel::onBrandChange,
                label = "Marca",
                placeholder = "",
                leading = {Icon(Icons.Outlined.DirectionsCar, null, tint = ColorGris)},
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                error = null,
                enabled = !uiState.isLoading
            )
            Spacer(Modifier.height(20.dp))

            Text(
                "Holograma",
                color = ColorGris,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(15.dp))

            HologramChips(
                options = listOf("E", "00", "0", "1", "2"),
                selected = uiState.hologram,
                onSelected = viewModel::onHologramChange
            )
            Spacer(Modifier.height(70.dp))

            PrimaryButton(
                isLoading = uiState.isLoading,
                enabled = true,
                onClick = { viewModel.onRegisterVehicle(email)}
            )

        }

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarTopBar(onBack: () -> Unit) {
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
            .aspectRatio(2.8f),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun TitleAndSubtitle() {
    Text(
        "Agregar Vehículo",
        textAlign = TextAlign.Center,
        color = ColorAzulOscuro,
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        "Ingresa la información de tu vehículo y empieza a mantenerlo al día",
        fontSize = 14.sp,
        color = ColorGris,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PlateFilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String?,
    enabled: Boolean = true
) {
    var placa by remember (value){
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    OutlinedTextField(
        value = placa,
        onValueChange = { new ->
            val cleaned = new.text.uppercase().filter { it.isLetterOrDigit() || it == '-' }
            val limited = cleaned.take(8)
            placa = new.copy(text = limited, selection = TextRange(limited.length))
            onValueChange(limited)
        },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text("Placa") },
        placeholder = { Text("ABC123", color = ColorGris)},
        leadingIcon = { Icon(Icons.Outlined.Numbers, null, tint = ColorGris) },
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
private fun HologramChips(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { opt ->
            CircleChip(
                label = opt,
                selected = selected == opt,
                onClick = { onSelected(opt) }
            )
        }
    }
}

@Composable
private fun CircleChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) ColorAzulOscuro else Color(0xFFE8EAED)
    val fg = if (selected) Color.White else Color.Black

    Surface(
        color = bg,
        shape = CircleShape,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        modifier = Modifier
            .size(42.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = fg,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1
            )
        }
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
            Text("Registrar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
