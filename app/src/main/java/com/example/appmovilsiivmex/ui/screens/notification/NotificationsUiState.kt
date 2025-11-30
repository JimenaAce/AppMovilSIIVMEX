package com.example.appmovilsiivmex.ui.screens.notification

import com.example.appmovilsiivmex.domain.model.Notification


data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),

    // Errores
    val error: String? = null
)