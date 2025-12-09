package com.example.appmovilsiivmex.ui.screens.vehicle

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorGris
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun VehicleAddScreen(
    viewModel: VehicleViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSubmit: (VehicleUiState) -> Unit = {}
) {
    val ui by viewModel.uiState.collectAsState()
    val kb = LocalSoftwareKeyboardController.current

    val listState = rememberLazyListState()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            Surface {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    PrimaryButton(
                        text = "Registrar",
                        isLoading = ui.isLoading,
                        onClick = {
                            focusManager.clearFocus()
                            kb?.hide()
                            viewModel.submit(onSuccess = { onSubmit(ui) })
                        }
                    )
                }
            }
        }
    ) { inner ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(inner)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Illustration(resId = R.drawable.addvehicle_illustration)
            }
            item {
                TitleAndSubtitle(
                    title = "Datos del vehículo",
                    subtitle = "Completa la información para continuar"
                )
            }
            item {
                // Placa (con bringIntoView al enfocar)
                FocusableWrapper(bringIntoViewRequester) {
                    PlateInput(
                        value = ui.plate,
                        onValueChange = viewModel::onPlateChange,
                        placeholder = "Placa",
                        imeAction = ImeAction.Next
                    )
                }
            }
            item {
                FilledInput(
                    value = ui.nickname,
                    onValueChange = viewModel::onNicknameChange,
                    placeholder = "Nombre para su vehículo",
                    leading = {
                        Icon(
                            Icons.Outlined.DriveFileRenameOutline,
                            null,
                            tint = ColorGris
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }
            item {
                FilledInput(
                    value = ui.year,
                    onValueChange = viewModel::onYearChange,
                    placeholder = "Año",
                    leading = { Icon(Icons.Outlined.CalendarMonth, null, tint = ColorGris) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
            item {
                FilledInput(
                    value = ui.brand,
                    onValueChange = viewModel::onBrandChange,
                    placeholder = "Marca",
                    leading = { Icon(Icons.Outlined.DirectionsCar, null, tint = ColorGris) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                        kb?.hide()
                    })
                )
            }
            item {
                Text(
                    "Holograma",
                    color = ColorGris,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                HologramChips(
                    options = listOf("E", "00", "0", "1", "2"),
                    selected = ui.hologram,
                    onSelected = viewModel::onHologramChange
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FocusableWrapper(
    bringIntoViewRequester: BringIntoViewRequester,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        var hasFocus by remember { mutableStateOf(false) }
        Box(
            Modifier.onGloballyPositioned { }
        ) {
            CompositionLocalProvider {
                val scope = rememberCoroutineScope()
                Box(
                    Modifier.onFocusChanged { st ->
                        if (st.isFocused && !hasFocus) {
                            hasFocus = true
                            scope.launch {
                                kotlinx.coroutines.delay(120)
                                bringIntoViewRequester.bringIntoView()
                            }
                        } else if (!st.isFocused) {
                            hasFocus = false
                        }
                    }
                ) {
                    content()
                }
            }
        }
    }
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
private fun TitleAndSubtitle(title: String, subtitle: String) {
    Text(
        title,
        color = ColorAzulOscuro,
        fontSize = 32.sp,
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth(),
    )
    Spacer(Modifier.height(6.dp))
    Text(
        subtitle,
        color = ColorGris,
        fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FilledInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        leadingIcon = leading,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        ),
        isError = isError
    )
    if (supportingText != null && isError) {
        Spacer(Modifier.height(4.dp))
        Text(supportingText, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
    }
}

@Composable
private fun PlateInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    imeAction: ImeAction
) {
    var tf by remember(value) {
        mutableStateOf(TextFieldValue(value, selection = TextRange(value.length)))
    }
    OutlinedTextField(
        value = tf,
        onValueChange = { new ->
            val cleaned = new.text.uppercase().filter { it.isLetterOrDigit() || it == '-' }
            val limited = cleaned.take(8)
            tf = new.copy(text = limited, selection = TextRange(limited.length))
            onValueChange(limited)
        },
        label = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.Numbers, null, tint = ColorGris) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Ascii,
            imeAction = imeAction
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ColorAzulOscuro,
            unfocusedBorderColor = ColorGris,
            focusedTextColor = ColorAzulOscuro,
            unfocusedTextColor = ColorGris
        )
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
            Text(text, fontSize = 16.sp, color = Color.White)
        }
    }
}
