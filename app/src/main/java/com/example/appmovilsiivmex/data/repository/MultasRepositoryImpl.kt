package com.example.appmovilsiivmex.data.repository;

import com.example.appmovilsiivmex.data.remote.MultasService
import com.example.appmovilsiivmex.domain.model.Multa
import com.example.appmovilsiivmex.domain.repository.MultasRepository

class MultasRepositoryImpl(
        private val service: MultasService = MultasService
) : MultasRepository {

    override fun obtenerMultas(): List<Multa> {
        return service.obtenerMultas()
    }
}