package com.example.appmovilsiivmex.ui.screens.vehicle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.appcompat.content.res.AppCompatResources
import com.example.appmovilsiivmex.R
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.ScaleBarOverlay
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider

@Composable
fun VehicleMapScreen() {
    val context = LocalContext.current

    val mapView = remember {
        MapView(context).apply {

            // ==== TILE SOURCE (Positron) ====
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

            // ==== CONFIGURACIÓN DE MÉXICO ====
            val mexicoCenter = GeoPoint(23.5, -102.0) // leve ajuste al centro

            // 👉 Zoom inicial un poco más alejado (se ve mejor todo el país)
            controller.setZoom(4.2)
            controller.setCenter(mexicoCenter)

            // 👉 Permitir alejar un poco más si quieres ver TODO el país cómodo
            setMinZoomLevel(3.8)   // puedes probar 3.6 o 4.0 también
            setMaxZoomLevel(19.0)

            // 👉 Bounding box un poco más grande para que no corte puntas
            val mexicoBounds = BoundingBox(
                34.0,   // norte (más arriba)
                -84.0,  // este  (más a la derecha)
                13.0,   // sur   (más abajo)
                -120.0  // oeste (más a la izquierda)
            )
            setScrollableAreaLimitDouble(mexicoBounds)

            // ==== OVERLAYS ====
            val compass = CompassOverlay(
                context,
                InternalCompassOrientationProvider(context),
                this
            ).apply { enableCompass() }
            overlays.add(compass)

            val scaleBar = ScaleBarOverlay(this).apply {
                setCentred(true)
                setScaleBarOffset(30, 20)
            }
            overlays.add(scaleBar)

            // ==== MARCADOR (ejemplo CDMX) ====
            val markerPoint = GeoPoint(19.4326, -99.1332) // CDMX
            val marker = Marker(this).apply {
                position = markerPoint
                icon = AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_vehicle_marker
                )
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Unidad 123"
                snippet = "Actualizado hace 2 min"
            }
            overlays.add(marker)

            invalidate()
        }
    }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { mapView }
        )
    }
}

