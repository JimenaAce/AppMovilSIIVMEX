package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.repository.NotificationRepository

class MarkNotificationReadUseCase(
    private val repository: NotificationRepository
) {

    suspend operator fun invoke(notificationId: Int): Result<Unit> {
        return repository.markAsRead(notificationId)
    }
}