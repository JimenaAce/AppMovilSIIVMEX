package com.example.appmovilsiivmex.data.remote.dto

import com.example.appmovilsiivmex.domain.model.Notification
import com.example.appmovilsiivmex.domain.model.User
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.example.appmovilsiivmex.domain.model.VehicleDetection

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email,
        nombreCompleto = nombreCompleto
    )
}

fun VehicleDto.toDomain(): Vehicle{
    return Vehicle(
        id = id,
        usuario_id = usuario_id,
        nombre_vehiculo = nombre_vehiculo,
        placa = placa,
        marca = marca,
        anio = anio,
        holograma = holograma,
        entidad_registro = entidad_registro
    )
}

fun VehicleDetectionDto.toDomain(): VehicleDetection{

    return VehicleDetection(
        id = id,
        vehicleId = vehiculo_id,
        ubicacion = ubicacion,
        lat = latitud,
        lng = longitud,
        fechaHora = fecha_hora,
        imagenBase64 = imagen_base64
    )
}

fun NotificationDto.toDomain(): Notification{

    return Notification(
        id = id,
        usuarioId = usuario_id,
        vehiculoId = vehiculo_id,
        titulo = titulo,
        mensaje = mensaje,
        leida = leida,
        fechaCreacion = fecha_creacion,
        fechaLeida = fecha_leida
    )
}
