package com.example.appmovilsiivmex.domain.repository

import com.example.appmovilsiivmex.domain.model.Notification

interface NotificationRepository {

    suspend fun getNotifications(userId: Int, limit: Int = 20, offset: Int = 0, onlyUnread: Boolean = false): Result<List<Notification>>
    suspend fun getUnreadCount(userId: Int): Result<Int>
    suspend fun markAsRead(notificationId: Int): Result<Unit>
}