package com.example.appmovilsiivmex.data.remote.dto

import com.example.appmovilsiivmex.domain.model.User
import com.example.appmovilsiivmex.domain.model.Vehicle

data class LoginResult(
    val user: User,
    val vehicles: List<Vehicle>
)
