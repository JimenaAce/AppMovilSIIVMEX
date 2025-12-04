package com.example.appmovilsiivmex.ui.screens.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.domain.usecase.GetNotificationsUseCase
import com.example.appmovilsiivmex.domain.usecase.MarkNotificationReadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationReadUseCase: MarkNotificationReadUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init {

        viewModelScope.launch {
            try {
                var userId = sessionManager.getUserId()

                if (userId == null) {
                    userId = 0
                }

                _uiState.value = _uiState.value.copy(
                    userId = userId
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error obteniendo vehículo seleccionado"
                )
            }
        }

    }

    fun loadNotifications(userId: Int) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {

                Log.d("NOTIFICACIONES", "El resultado userID es: $userId")

                val result = getNotificationsUseCase(userId = userId)

                Log.d("NOTIFICACIONES", "El resultado es: $result")

                result.fold(

                    onSuccess = { notifications ->

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                notifications = notifications
                            )
                        }

                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = error.message
                            )
                        }

                    }
                )

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error de conexión. Intenta nuevamente"
                    )
                }
            }
        }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            try {

                markNotificationReadUseCase(notificationId)

                val updated = _uiState.value.notifications.map {
                    if (it.id == notificationId) it.copy(leida = true) else it
                }
                _uiState.value = _uiState.value.copy(notifications = updated)
            } catch (e: Exception) {

            }
        }
    }
}
