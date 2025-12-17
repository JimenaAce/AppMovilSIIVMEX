package com.example.appmovilsiivmex.ui.screens.map

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.domain.model.VehicleDetection
import com.example.appmovilsiivmex.navigation.LocalOnVehicleSelected
import com.example.appmovilsiivmex.navigation.LocalSelectedVehicleId
import com.example.appmovilsiivmex.navigation.LocalVehicles
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.mapState.collectAsState()

    val vehicles = LocalVehicles.current
    val globalSelectedVehicleId = LocalSelectedVehicleId.current
    val onVehicleSelectedGlobal = LocalOnVehicleSelected.current


    // Vehículo seleccionado a nivel de esta pantalla,
    // inicializado con el global (o el primero de la lista)
    var selectedVehicleId by remember(globalSelectedVehicleId, vehicles) {
        mutableStateOf(
            globalSelectedVehicleId ?: vehicles.firstOrNull()?.id
        )
    }

    // Para pintar el texto del selector
    val selectedVehicle = remember(vehicles, selectedVehicleId) {
        vehicles.firstOrNull { it.id == selectedVehicleId }
    }

    var selectorExpanded by remember { mutableStateOf(false) }

    var selectedDetection by remember { mutableStateOf<VehicleDetection?>(null) }
    var pendingDetection by remember { mutableStateOf<VehicleDetection?>(null) }
    var clickTrigger by remember { mutableIntStateOf(0) }

    // Cuando cambie el vehículo seleccionado en esta pantalla → cargamos detecciones
    LaunchedEffect(selectedVehicleId) {
        selectedVehicleId?.let { viewModel.loadDetections(it) }
    }

    val mapView = remember {
        MapView(context).apply {
            val googleLikeTileSource = XYTileSource(
                "CartoDBPositron",
                0, 19, 256, ".png", arrayOf(
                    "https://a.basemaps.cartocdn.com/light_all/",
                    "https://b.basemaps.cartocdn.com/light_all/",
                    "https://c.basemaps.cartocdn.com/light_all/",
                    "https://d.basemaps.cartocdn.com/light_all/"
                )
            )
            setTileSource(googleLikeTileSource)

            setMultiTouchControls(true)
            setTilesScaledToDpi(true)

            val mexicoCenter = GeoPoint(23.5, -102.0)
            controller.setZoom(4.2)
            controller.setCenter(mexicoCenter)

            minZoomLevel = 3.8
            maxZoomLevel = 19.0

            val mexicoBounds = BoundingBox(
                34.0, -84.0,
                13.0, -120.0
            )
            setScrollableAreaLimitDouble(mexicoBounds)
        }
    }

    LaunchedEffect(clickTrigger) {
        val det = pendingDetection ?: return@LaunchedEffect
        val lat = det.lat
        val lng = det.lng
        if (lat != null && lng != null) {
            val centerLat = lat - 0.05
            val centerPoint = GeoPoint(centerLat, lng)

            mapView.controller.apply {
                setZoom(12.8)
                animateTo(centerPoint)
            }
        }
        delay(500)
        selectedDetection = det
    }

    Box(Modifier.fillMaxSize()) {

        // Mapa
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView },
            update = { map ->
                updateDetectionMarkers(
                    mapView = map,
                    detections = state.detections,
                    onMarkerClick = { det ->
                        pendingDetection = det
                        selectedDetection = null
                        clickTrigger++
                    }
                )
            }
        )

        // ──────────────────────────────────────
        // Selector de vehículo (estilo AppHeader)
        // ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            if (vehicles.isNotEmpty() && selectedVehicle != null) {
                Box {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color(0xFFF5F5F5),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable { selectorExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedVehicle.placa,
                                color = Color(0xFF1A2E47),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar vehículo",
                                tint = Color(0xFF1A2E47)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = selectorExpanded,
                        onDismissRequest = { selectorExpanded = false },
                        modifier = Modifier
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        vehicles.forEachIndexed { index, vehicle ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = vehicle.placa,
                                        color = Color(0xFF1A2E47),
                                        fontSize = 14.sp,
                                        fontWeight = if (vehicle.id == selectedVehicleId)
                                            FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color(0xFF1A2E47)
                                    )
                                },
                                onClick = {
                                    selectedVehicleId = vehicle.id
                                    selectorExpanded = false
                                    onVehicleSelectedGlobal(vehicle.id)
                                    // (si quieres, aquí podrías guardar en SessionManager
                                    // usando algún callback global más adelante)
                                }
                            )
                            if (index != vehicles.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    thickness = DividerDefaults.Thickness,
                                    color = Color(0xFFE0E6EE)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Card inferior con info de la detección
        AnimatedVisibility(
            visible = selectedDetection != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 3 })
        ) {
            selectedDetection?.let { det ->
                DetectionBottomCard(
                    detection = det,
                    onClose = {
                        selectedDetection = null
                        pendingDetection = null
                    }
                )
            }
        }
    }
}

// ──────────────────────────────────────
// Helpers y composables auxiliares
// ──────────────────────────────────────

fun formatFechaHora(original: String?): String {
    if (original.isNullOrBlank()) return ""

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        val date = inputFormat.parse(original)
        val outputFormat = SimpleDateFormat("dd MMM yyyy · hh:mm a", Locale("es", "MX"))
        outputFormat.format(date!!)
    } catch (e: Exception) {
        original
    }
}

private fun updateDetectionMarkers(
    mapView: MapView,
    detections: List<VehicleDetection>,
    onMarkerClick: (VehicleDetection) -> Unit
) {
    val toRemove = mapView.overlays.filterIsInstance<Marker>()
    mapView.overlays.removeAll(toRemove)

    detections.forEach { det ->
        val lat = det.lat
        val lng = det.lng
        if (lat != null && lng != null) {
            val point = GeoPoint(lat, lng)
            val marker = Marker(mapView).apply {
                position = point
                icon = AppCompatResources.getDrawable(
                    mapView.context,
                    R.drawable.ic_vehicle_marker
                )
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = det.ubicacion ?: "Detección ${det.id}"
                snippet = det.fechaHora ?: ""

                setOnMarkerClickListener { _, _ ->
                    onMarkerClick(det)
                    true
                }
            }
            mapView.overlays.add(marker)
        }
    }

    mapView.invalidate()
}

@Composable
fun DetectionBottomCard(
    detection: VehicleDetection,
    onClose: () -> Unit
) {
    var showFullImage by remember { mutableStateOf(false) }

    if (showFullImage && !detection.imagenBase64.isNullOrBlank()) {
        FullScreenDetectionViewer(
            base64 = detection.imagenBase64,
            onDismiss = { showFullImage = false },
            title = detection.ubicacion ?: "Detección",
            subtitle = formatFechaHora(detection.fechaHora)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .padding(bottom = 130.dp)
                .fillMaxWidth(0.78f),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top
            ) {
                DetectionImageFromBase64(
                    base64 = detection.imagenBase64,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                        .clickable { showFullImage = true },
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = detection.ubicacion ?: "WebCam · Huawei",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6B7280)
                        )
                    }
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = detection.ubicacion ?: "Ubicación desconocida",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color(0xFF111827),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    )
                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = formatFechaHora(detection.fechaHora),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Lat: ${detection.lat ?: "-"} • Lng: ${detection.lng ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF111827)
                            ),
                            contentPadding = PaddingValues(vertical = 6.dp)
                        ) {
                            Text("Cerrar", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FullScreenDetectionViewer(
    base64: String,
    onDismiss: () -> Unit,
    title: String? = null,
    subtitle: String? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC020617),
                            Color(0xE6000000)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onDismiss)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (!title.isNullOrBlank()) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White
                            )
                        }
                        if (!subtitle.isNullOrBlank()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFE5E7EB)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color(0x660F172A),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .aspectRatio(4f / 3f, matchHeightConstraintsFirst = false),
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp,
                        color = Color(0xFF020617)
                    ) {
                        ZoomableDetectionImageFromBase64(
                            base64 = base64,
                            modifier = Modifier.fillMaxSize(),
                            maxScale = 4f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoomableDetectionImageFromBase64(
    base64: String?,
    modifier: Modifier = Modifier,
    maxScale: Float = 4f
) {
    if (base64.isNullOrBlank()) {
        Box(
            modifier = modifier
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color(0xFF64748B)
            )
        }
        return
    }

    val imageBitmap by remember(base64) {
        mutableStateOf(
            try {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bmp?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        )
    }

    if (imageBitmap == null) {
        Box(
            modifier = modifier
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
                tint = Color(0xFF64748B)
            )
        }
        return
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .combinedClickable(
                onClick = { /* no-op */ },
                onDoubleClick = {
                    scale = 1f
                    offset = Offset.Zero
                }
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(1f, maxScale)
                    val newOffset = if (newScale > 1f) offset + pan else Offset.Zero
                    scale = newScale
                    offset = newOffset
                }
            }
    ) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun DetectionImageFromBase64(
    base64: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    if (base64.isNullOrBlank()) {
        Box(
            modifier = modifier
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = Color(0xFF64748B)
            )
        }
        return
    }

    val imageBitmap by remember(base64) {
        mutableStateOf(
            try {
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bmp?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        )
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier
                .background(Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
                tint = Color(0xFF64748B)
            )
        }
    }
}
