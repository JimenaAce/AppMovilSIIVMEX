package com.example.appmovilsiivmex.data.repository


import com.example.appmovilsiivmex.data.remote.ApiClient
import com.example.appmovilsiivmex.data.remote.dto.toDomain
import com.example.appmovilsiivmex.domain.model.Notification
import com.example.appmovilsiivmex.domain.repository.NotificationRepository

class NotificationRepositoryImpl: NotificationRepository{
    override suspend fun getNotifications(userId: Int, limit: Int, offset: Int, onlyUnread: Boolean): Result<List<Notification>> {
        return try {

            val response = ApiClient.obtenerNotificaciones(userId, limit, offset, onlyUnread)

            response.fold(

                onSuccess = { notificationResponse ->

                    if(notificationResponse.success){
                        Result.success(notificationResponse.notifications.map { it.toDomain() })

                    }else{
                        Result.failure(Exception("Error obteniendo notificaciones"))
                    }
                },
                onFailure = { error ->
                    Result.failure(Exception("Error de conexión: ${error.message}"))
                }
            )
        } catch (e: Exception){

            Result.failure(Exception("Error de conexión: ${e.message}"))
        }

    }

    override suspend fun getUnreadCount(userId: Int): Result<Int> {
        return try {

            val response = ApiClient.cantidadNotificacionesNoLeidas(userId)

            response.fold(
                onSuccess = { unreadCountResponse ->
                    if (!unreadCountResponse.success) {
                        return Result.failure(Exception("Error obteniendo no leídas"))
                    }
                    Result.success(unreadCountResponse.unread_count)
                },
                onFailure = { error ->
                    Result.failure(Exception("Error obteniendo no leídas: ${error.message}", error))
                }
            )

        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}", e))
        }
    }


    override suspend fun markAsRead(notificationId: Int): Result<Unit> {

        return try{

            val response = ApiClient.marcarNotificacionLeida(notificationId)

            response.fold(
                onSuccess = { markReadResponse ->

                    if(!markReadResponse.success){
                        return Result.failure(Exception(markReadResponse.message))
                    }

                    Result.success(Unit)
                },
                onFailure = {error ->

                    Result.failure(Exception("Error marcando como leída: ${error.message}", error))
                }
            )

        } catch (e: Exception){
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }


}
