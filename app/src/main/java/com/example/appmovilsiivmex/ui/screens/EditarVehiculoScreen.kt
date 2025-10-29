package com.example.appmovilsiivmex.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.ui.theme.ColorAzulOscuro
import com.example.appmovilsiivmex.ui.theme.ColorChipInactivo

@Composable
fun EditarVehiculoScreen(
    navController: NavController,
    placaInicial: String,
    marcaInicial: String,
    nombreInicial: String,
    anioInicial: String,
    hologramaInicial: String,
    onSave: (placa: String, marca: String, nombre: String, anio: String, holograma: String) -> Unit
) {
    val fondoApp = Color(0xFFF9FBF8)

    // Estado editable local
    var placa by remember { mutableStateOf(placaInicial) }
    var marca by remember { mutableStateOf(marcaInicial) }
    var nombre by remember { mutableStateOf(nombreInicial) }
    var anio by remember { mutableStateOf(anioInicial) }
    var holograma by remember { mutableStateOf(hologramaInicial) } // "E", "00", "0", "1", "2"

    Scaffold(
        containerColor = fondoApp,
        topBar = {
            // Barra superior sin flecha, título centrado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(fondoApp)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Editar Vehículo",
                    color = ColorAzulOscuro,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    ) { innerPadding ->

        // Contenido scrolleable.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoApp)
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = innerPadding.calculateTopPadding() + 8.dp,
                    bottom = innerPadding.calculateBottomPadding() + 120.dp // colchón extra grande abajo
                )
        ) {

            // ───────── Imagen + placa grande ─────────
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.carrito),
                    contentDescription = "Auto del usuario",
                    modifier = Modifier
                        .width(220.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = placa,
                    color = ColorAzulOscuro,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ───────── Campos editables ─────────
            CampoEditable(
                label = "Placa",
                valor = placa,
                onValorChange = { placa = it },
                keyboardType = KeyboardType.Ascii
            )

            CampoEditable(
                label = "Marca",
                valor = marca,
                onValorChange = { marca = it },
                keyboardType = KeyboardType.Text
            )

            CampoEditable(
                label = "Nombre",
                valor = nombre,
                onValorChange = { nombre = it },
                keyboardType = KeyboardType.Text
            )

            CampoEditable(
                label = "Año",
                valor = anio,
                onValorChange = { nuevo ->
                    if (nuevo.all { it.isDigit() } && nuevo.length <= 4) {
                        anio = nuevo
                    }
                },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ───────── Holograma seleccionable ─────────
            Text(
                text = "Holograma",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorAzulOscuro
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("E", "00", "0", "1", "2").forEach { opcion ->
                    HoloChipSelectable(
                        texto = opcion,
                        activo = (opcion == holograma),
                        onClick = { holograma = opcion }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ───────── Botón "Eliminar vehículo" ─────────
            BotonAccionLleno(
                texto = "Eliminar vehículo",
                fondo = ColorAzulOscuro,
                textoColor = Color.White,
                onClick = {
                    // Por ahora solo regresamos a la pantalla anterior.
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ───────── Botones Guardar / Cancelar ─────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Guardar
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    BotonAccionLleno(
                        texto = "Guardar",
                        fondo = ColorAzulOscuro,
                        textoColor = Color.White,
                        onClick = {
                            onSave(placa, marca, nombre, anio, holograma)
                            navController.popBackStack()
                        }
                    )
                }

                // Cancelar
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    BotonAccionLleno(
                        texto = "Cancelar",
                        fondo = Color(0xFFE0E0E0),
                        textoColor = ColorAzulOscuro,
                        onClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

// ---------- Subcomposables ----------

@Composable
private fun CampoEditable(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6F6F6F)
        )

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(6.dp),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = ColorAzulOscuro,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFFE0E0E0),
                unfocusedIndicatorColor = Color(0xFFE0E0E0),
                disabledIndicatorColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = ColorAzulOscuro
            )
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun HoloChipSelectable(
    texto: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    val bg = if (activo) ColorAzulOscuro else ColorChipInactivo
    val fg = if (activo) Color.White else ColorAzulOscuro

    Surface(
        color = bg,
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .clickable { onClick() }
    ) {
        Text(
            text = texto,
            color = fg,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BotonAccionLleno(
    texto: String,
    fondo: Color,
    textoColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = fondo,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = texto,
                color = textoColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}