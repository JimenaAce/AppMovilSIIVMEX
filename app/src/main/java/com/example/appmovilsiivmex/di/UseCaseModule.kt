package com.example.appmovilsiivmex.di

import com.example.appmovilsiivmex.domain.repository.AuthRepository
import com.example.appmovilsiivmex.domain.repository.NotificationRepository
import com.example.appmovilsiivmex.domain.repository.VehicleRepository
import com.example.appmovilsiivmex.domain.usecase.ChangePasswordUseCase
import com.example.appmovilsiivmex.domain.usecase.GetNotificationsUseCase
import com.example.appmovilsiivmex.domain.usecase.LoginUseCase
import com.example.appmovilsiivmex.domain.usecase.MarkNotificationReadUseCase
import com.example.appmovilsiivmex.domain.usecase.RegisterUseCase
import com.example.appmovilsiivmex.domain.usecase.ForgotPasswordUseCase
import com.example.appmovilsiivmex.domain.usecase.ResendEmailResetUseCase
import com.example.appmovilsiivmex.domain.usecase.ResendEmailUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidateEmailUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidatePasswordUseCase
import com.example.appmovilsiivmex.domain.usecase.ValidatePlateUseCase
import com.example.appmovilsiivmex.domain.usecase.VehicleDetectionsUseCase
import com.example.appmovilsiivmex.domain.usecase.VehicleRegisterUseCase
import com.example.appmovilsiivmex.domain.usecase.VerifyEmailResetUseCase
import com.example.appmovilsiivmex.domain.usecase.VerifyEmailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    // USUARIOS
    @Provides
    @ViewModelScoped
    fun provideLoginUseCase(
        repository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideRegisterUseCase(
        repository: AuthRepository
    ): RegisterUseCase {
        return RegisterUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideVerifyEmailUseCase(
        repository: AuthRepository
    ): VerifyEmailUseCase {
        return VerifyEmailUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideResendEmailUseCase(
        repository: AuthRepository
    ): ResendEmailUseCase {
        return ResendEmailUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideForgotPasswordUseCase(
        repository: AuthRepository
    ): ForgotPasswordUseCase {
        return ForgotPasswordUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideVerifyEmailResetUseCase(
        repository: AuthRepository
    ): VerifyEmailResetUseCase {
        return VerifyEmailResetUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideResendEmailResetUseCase(
        repository: AuthRepository
    ): ResendEmailResetUseCase {
        return ResendEmailResetUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideChangePasswordUseCase(
        repository: AuthRepository
    ): ChangePasswordUseCase {
        return ChangePasswordUseCase(repository)
    }


    // VEHÍCULOS
    @Provides
    @ViewModelScoped
    fun provideVehicleRegisterUseCase(
        repository: VehicleRepository
    ): VehicleRegisterUseCase {
        return VehicleRegisterUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideVehicleDetectionUseCase(
        repository: VehicleRepository
    ): VehicleDetectionsUseCase{
        return VehicleDetectionsUseCase(repository)
    }

    // NOTIFICACIONES
    @Provides
    @ViewModelScoped
    fun provideGetNotificationsUseCase(
        repository: NotificationRepository
    ): GetNotificationsUseCase{
        return GetNotificationsUseCase(repository)
    }

    @Provides
    @ViewModelScoped
    fun provideMarkNotificationReadUseCase(
        repository: NotificationRepository
    ): MarkNotificationReadUseCase{
        return MarkNotificationReadUseCase(repository)
    }

    // VALIDACIONES
    @Provides
    @ViewModelScoped
    fun provideValidateEmailUseCase(): ValidateEmailUseCase {
        return ValidateEmailUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideValidatePasswordUseCase(): ValidatePasswordUseCase{
        return ValidatePasswordUseCase()
    }

    @Provides
    @ViewModelScoped
    fun provideValidatePlateUseCase(): ValidatePlateUseCase{
        return ValidatePlateUseCase()
    }

}