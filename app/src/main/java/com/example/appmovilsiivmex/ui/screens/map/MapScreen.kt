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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.appmovilsiivmex.R
import com.example.appmovilsiivmex.domain.model.VehicleDetection
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

    val selectedVehicleId = state.selectedVehicleId


    var selectedDetection by remember { mutableStateOf<VehicleDetection?>(null) }
    var pendingDetection by remember { mutableStateOf<VehicleDetection?>(null) }

    // Trigger para poder volver a hacer zoom aunque sea el mismo marcador
    var clickTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(selectedVehicleId) {
        selectedVehicleId?.let { id ->
            viewModel.loadDetections(id)
        }
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

fun formatFechaHora(original: String?): String {
    if (original.isNullOrBlank()) return ""

    return try {
        // Formato que llega del backend
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

        // Parseamos la fecha original
        val date = inputFormat.parse(original)

        // Formato bonito que quieres mostrar
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

    // Imagen a pantalla completa
    if (showFullImage && !detection.imagenBase64.isNullOrBlank()) {
        FullScreenDetectionViewer(
            base64 = detection.imagenBase64,
            onDismiss = { showFullImage = false },
            title = detection.ubicacion ?: "Detección",
            subtitle = formatFechaHora(detection.fechaHora)
        )
    }

    // Card flotante
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

                // Imagen
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

                    // Chip con estado / cámara
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

                    // Título
                    Text(
                        text = detection.ubicacion ?: "Ubicación desconocida",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color(0xFF111827),
                            fontSize = MaterialTheme.typography.titleMedium.fontSize
                        )
                    )
                    Spacer(Modifier.height(4.dp))

                    // Fecha / hora
                    Text(
                        text = formatFechaHora(detection.fechaHora),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(Modifier.height(6.dp))

                    // Coordenadas
                    Text(
                        text = "Lat: ${detection.lat ?: "-"} • Lng: ${detection.lng ?: "-"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(Modifier.height(10.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        Alignment.CenterVertically
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
            // Cerrar tocando fondo
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(onClick = onDismiss),
            )

            // Contenedor principal de la imagen + info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Top bar
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

                // Imagen centrada
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)
@Composable
fun ZoomableDetectionImageFromBase64(
    base64: String?,
    modifier: Modifier = Modifier,
    maxScale: Float = 4f
) {
    if (base64.isNullOrBlank()) {
        // fallback igual que antes
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

    // Decodificar la imagen una vez
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

    // Estado de zoom y desplazamiento
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .combinedClickable(
                onClick = { /* no-op: ya cierras con el botón X afuera */ },
                onDoubleClick = {
                    // reset de zoom y posición
                    scale = 1f
                    offset = Offset.Zero
                }
            )
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    // actualizar zoom
                    val newScale = (scale * zoom).coerceIn(1f, maxScale)
                    // ajustar pan solo cuando hay zoom
                    val newOffset =
                        if (newScale > 1f) offset + pan else Offset.Zero

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
