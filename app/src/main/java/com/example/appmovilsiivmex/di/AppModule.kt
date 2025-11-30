package com.example.appmovilsiivmex.di

import android.content.Context
import com.example.appmovilsiivmex.data.local.SessionManager
import com.example.appmovilsiivmex.data.repository.AuthRepositoryImpl
import com.example.appmovilsiivmex.data.repository.NotificationRepositoryImpl
import com.example.appmovilsiivmex.data.repository.VehicleRepositoryImpl
import com.example.appmovilsiivmex.domain.repository.AuthRepository
import com.example.appmovilsiivmex.domain.repository.NotificationRepository
import com.example.appmovilsiivmex.domain.repository.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // SessionManager
    @Provides
    @Singleton
    fun provideSessionManager(
        @ApplicationContext context: Context
    ): SessionManager{
        return SessionManager(context)
    }

    // AuthRepository
    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepositoryImpl()
    }

    // VehicleRepository
    @Provides
    @Singleton
    fun provideVehicleRepository(): VehicleRepository {
        return VehicleRepositoryImpl()
    }

    // NotificationRepository
    @Provides
    @Singleton
    fun provideNotificationRepository(): NotificationRepository {
        return NotificationRepositoryImpl()
    }
}