package com.example.appmovilsiivmex.domain.usecase

import com.example.appmovilsiivmex.domain.model.Notification
import com.example.appmovilsiivmex.domain.repository.NotificationRepository

class GetNotificationsUseCase(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(userId: Int, onlyUnread: Boolean = false): Result<List<Notification>> {
        return repository.getNotifications(userId = userId, onlyUnread = false)
    }
}