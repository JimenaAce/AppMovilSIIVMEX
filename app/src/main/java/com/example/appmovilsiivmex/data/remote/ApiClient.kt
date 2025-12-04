package com.example.appmovilsiivmex.data.remote

import com.example.appmovilsiivmex.data.remote.dto.ChangePasswordResponse
import com.example.appmovilsiivmex.data.remote.dto.LoginResponse
import com.example.appmovilsiivmex.data.remote.dto.MarkReadResponse
import com.example.appmovilsiivmex.data.remote.dto.NotificationDto
import com.example.appmovilsiivmex.data.remote.dto.NotificationsResponse
import com.example.appmovilsiivmex.data.remote.dto.RegisterResponse
import com.example.appmovilsiivmex.data.remote.dto.ResendEmailResponse
import com.example.appmovilsiivmex.data.remote.dto.ForgotPasswordResponse
import com.example.appmovilsiivmex.data.remote.dto.ResendEmailResetResponse
import com.example.appmovilsiivmex.data.remote.dto.UnreadCountResponse
import com.example.appmovilsiivmex.data.remote.dto.UserDto
import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionDto
import com.example.appmovilsiivmex.data.remote.dto.VehicleDetectionResponse
import com.example.appmovilsiivmex.data.remote.dto.VehicleDto
import com.example.appmovilsiivmex.data.remote.dto.VehicleRegisterResponse
import com.example.appmovilsiivmex.data.remote.dto.VerifyEmailResetResponse
import com.example.appmovilsiivmex.data.remote.dto.VerifyEmailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://83d01df19734.ngrok-free.app"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = "application/json; charset=utf-8".toMediaType()

    // Login
    suspend fun login(email: String, password: String): Result<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email", "password": "$password"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/login")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val userDto = if (jsonObject.has("user")) {
                            val userObj = jsonObject.getJSONObject("user")
                            UserDto(
                                id = userObj.getInt("id"),
                                email = userObj.getString("email"),
                                nombreCompleto = userObj.getString("nombre_completo")
                            )
                        } else null

                        // Vehiculos
                        val vehiclesList = mutableListOf<VehicleDto>()
                        val vehiclesJsonArray = jsonObject.optJSONArray("vehicles")

                        if (vehiclesJsonArray != null) {
                            for (i in 0 until vehiclesJsonArray.length()) {
                                val vehicleObj = vehiclesJsonArray.getJSONObject(i)
                                val vehicle = VehicleDto(
                                    id = vehicleObj.getInt("id"),
                                    usuario_id = vehicleObj.getInt("usuario_id"),
                                    placa = vehicleObj.getString("placa"),
                                    nombre_vehiculo = vehicleObj.getString("nombre_vehiculo"),
                                    marca = vehicleObj.optString("marca", null),
                                    anio = if (vehicleObj.isNull("anio")) null else vehicleObj.getInt("anio"),
                                    holograma = vehicleObj.getString("holograma"),
                                    entidad_registro = vehicleObj.getString("entidad_registro")
                                )
                                vehiclesList.add(vehicle)
                            }
                        }

                        val loginResponse = LoginResponse(
                            success = jsonObject.getBoolean("success"),
                            message = jsonObject.getString("message"),
                            token = jsonObject.optString("token", ""),
                            user = userDto,
                            vehicles = vehiclesList
                        )
                        Result.success(loginResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al iniciar sesión")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    // Registro de token
    suspend fun registrarToken(usuarioId: Int, token: String, dispositivo: String = "android"): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"usuario_id": $usuarioId, "token": "$token", "dispositivo": "$dispositivo"}"""
                    .toRequestBody(json)
                val req = Request.Builder().url("$BASE_URL/api/registrar-token").post(body).build()
                client.newCall(req).execute().use { it.isSuccessful }
            } catch (e: Exception) {
                false
            }
        }

    // Registro de usuario
    suspend fun registrar(name:String, email: String, password: String): Result<RegisterResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email", "password": "$password", "nombre_completo": "$name"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/register")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val registerResponse = RegisterResponse(
                            success = jsonObject.getBoolean("success"),
                            message = jsonObject.getString("message"),
                            token = jsonObject.optString("token", ""),
                            user = if(jsonObject.has("user")){
                                val userObj = jsonObject.getJSONObject("user")
                                UserDto(
                                    id = userObj.getInt("id"),
                                    email = userObj.getString("email"),
                                    nombreCompleto = userObj.getString("nombre_completo")
                                )
                            }else null
                        )

                        Result.success(registerResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Verificación de correo
    suspend fun verificarCorreo(email: String, code: String): Result<VerifyEmailResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email", "codigo": "$code"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/verify-email")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val verifyEmailResponse = VerifyEmailResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(verifyEmailResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Reenvio de código de verificación
    suspend fun reenviarCorreo(email: String): Result<ResendEmailResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/resend-verification")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val resendEmailResponse = ResendEmailResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(resendEmailResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Reestablecer contraseña
    suspend fun restablecerContrasenia(email: String): Result<ForgotPasswordResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/password/forgot")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val forgotPasswordResponse = ForgotPasswordResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(forgotPasswordResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Reenvio de código de verificación
    suspend fun reenviarCorreoRestablecer(email: String): Result<ResendEmailResetResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/password/resend-verification")

                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val resendEmailResetResponse = ResendEmailResetResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(resendEmailResetResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Verificación de correo
    suspend fun verificarCorreoReestablecer(email: String, code: String): Result<VerifyEmailResetResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email", "codigo": "$code"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/password/reset")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val verifyEmailResetResponse = VerifyEmailResetResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(verifyEmailResetResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Cambiar contraseña
    suspend fun cambiarContrasenia(email: String, nuevaContrasenia: String): Result<ChangePasswordResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = """{"email": "$email", "nueva_contrasenia": "$nuevaContrasenia"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/password/change")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val changePasswordResponse = ChangePasswordResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", "Respuesta desconocida")
                        )

                        Result.success(changePasswordResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Registro de carro
    suspend fun registrarVehiculo(email: String, carName:String, plate: String, brand: String, year: Int?, hologram: String, entidad_registro: String): Result<VehicleRegisterResponse> =
        withContext(Dispatchers.IO) {
            try {

                val yearPart = year?.toString() ?: "null"

                val body = """{"email": "$email", "nombre_vehiculo": "$carName", "placa": "$plate", "marca": "$brand", "anio": $yearPart, "holograma": "$hologram", "entidad_registro": "$entidad_registro"}"""
                    .toRequestBody(json)

                val request = Request.Builder()
                    .url("$BASE_URL/api/vehicles")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val vehicleRegisterResponse = VehicleRegisterResponse(
                            success = jsonObject.getBoolean("success"),
                            message = jsonObject.getString("message"),
                            vehicle = if(jsonObject.has("vehicle")){
                                val vehicleObj = jsonObject.getJSONObject("vehicle")
                                VehicleDto(
                                    id = vehicleObj.getInt("id"),
                                    usuario_id = vehicleObj.getInt("usuario_id"),
                                    nombre_vehiculo = vehicleObj.getString("nombre_vehiculo"),
                                    placa = vehicleObj.getString("placa"),
                                    marca = vehicleObj.optString("marca", "Generica"),
                                    anio = if (vehicleObj.isNull("anio")) null else vehicleObj.getInt("anio"),
                                    holograma = vehicleObj.getString("holograma"),
                                    entidad_registro = vehicleObj.getString("entidad_registro")
                                )
                            }else null
                        )

                        Result.success(vehicleRegisterResponse)

                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString("message", "Error al registrar usuario")
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ============================
    // Detecciones de un vehículo
    // ============================
    suspend fun deteccionesVehiculo(vehicleId: Int): Result<VehicleDetectionResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/api/vehicles/$vehicleId/detections")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val success = jsonObject.optBoolean("success", false)
                        val detectionsJson: JSONArray =
                            jsonObject.optJSONArray("detections") ?: JSONArray()

                        val detections = mutableListOf<VehicleDetectionDto>()

                        for (i in 0 until detectionsJson.length()) {
                            val dObj = detectionsJson.getJSONObject(i)

                            val det = VehicleDetectionDto(
                                id = dObj.getInt("id"),
                                vehiculo_id = dObj.getInt("vehiculo_id"),
                                ubicacion = dObj.optString("ubicacion", null),
                                latitud = if (dObj.isNull("latitud")) null
                                else dObj.getDouble("latitud"),
                                longitud = if (dObj.isNull("longitud")) null
                                else dObj.getDouble("longitud"),
                                fecha_hora = dObj.optString("fecha_hora", null),
                                imagen_base64 = if (dObj.isNull("imagen_base64")) null
                                else dObj.getString("imagen_base64")
                            )

                            detections.add(det)
                        }

                        val result = VehicleDetectionResponse(
                            success = success,
                            detections = detections
                        )

                        Result.success(result)
                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString(
                            "message",
                            "Error al obtener detecciones"
                        )
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Lista de notificaciones
    suspend fun obtenerNotificaciones(usuarioId: Int, limit: Int = 20, offset: Int = 0, onlyUnread: Boolean = false): Result<NotificationsResponse> =
        withContext(Dispatchers.IO) {
            try {
                val onlyUnreadInt = if (onlyUnread) 1 else 0
                val url =
                    "$BASE_URL/api/users/$usuarioId/notifications?limit=$limit&offset=$offset&only_unread=$onlyUnreadInt"

                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val success = jsonObject.optBoolean("success", false)
                        val array = jsonObject.optJSONArray("notifications") ?: JSONArray()

                        val list = mutableListOf<NotificationDto>()
                        for (i in 0 until array.length()) {
                            val obj = array.getJSONObject(i)
                            val notif = NotificationDto(
                                id = obj.getInt("id"),
                                usuario_id = obj.getInt("usuario_id"),
                                vehiculo_id = if (obj.isNull("vehiculo_id")) null
                                else obj.getInt("vehiculo_id"),
                                titulo = obj.getString("titulo"),
                                mensaje = obj.getString("mensaje"),
                                leida = obj.optBoolean("leida", false),
                                fecha_creacion = obj.optString("fecha_creacion", null),
                                fecha_leida = obj.optString("fecha_leida", null)
                            )
                            list.add(notif)
                        }

                        Result.success(
                            NotificationsResponse(
                                success = success,
                                notifications = list
                            )
                        )
                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString(
                            "message",
                            "Error al obtener notificaciones"
                        )
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // Cantidad de notificaciones no leídas
    suspend fun cantidadNotificacionesNoLeidas(usuarioId: Int): Result<UnreadCountResponse> =
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$BASE_URL/api/users/$usuarioId/notifications/unread-count")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody ?: "{}")

                        val result = UnreadCountResponse(
                            success = jsonObject.optBoolean("success", false),
                            unread_count = jsonObject.optInt("unread_count", 0)
                        )

                        Result.success(result)
                    } else {
                        val errorBody = response.body?.string()
                        val errorJson = JSONObject(errorBody ?: "{}")
                        val errorMsg = errorJson.optString(
                            "message",
                            "Error al obtener contador de no leídas"
                        )
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    // Marcar notificación como leída
    suspend fun marcarNotificacionLeida(notifId: Int): Result<MarkReadResponse> =
        withContext(Dispatchers.IO) {
            try {
                // body vacío, pero Flask recibe POST
                val body = "{}".toRequestBody(json)
                val request = Request.Builder()
                    .url("$BASE_URL/api/notifications/$notifId/mark-read")
                    .post(body)
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "{}")

                    return@use if (response.isSuccessful) {
                        val res = MarkReadResponse(
                            success = jsonObject.optBoolean("success", false),
                            message = jsonObject.optString("message", null)
                        )
                        Result.success(res)
                    } else {
                        val errorMsg = jsonObject.optString(
                            "message",
                            "Error al marcar notificación como leída"
                        )
                        Result.failure(Exception(errorMsg))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }



}