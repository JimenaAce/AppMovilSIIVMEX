package com.example.appmovilsiivmex.domain.repository

import com.example.appmovilsiivmex.domain.model.Multa

interface MultasRepository {
    fun obtenerMultas(): List<Multa>
}