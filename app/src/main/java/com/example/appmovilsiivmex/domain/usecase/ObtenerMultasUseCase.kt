package com.example.appmovilsiivmex.domain.usecase;

import com.example.appmovilsiivmex.domain.model.Multa
import com.example.appmovilsiivmex.domain.repository.MultasRepository

import java.util.List;

class ObtenerMultasUseCase(
        private val repository: MultasRepository
) {
    operator fun invoke(): kotlin.collections.List<Multa>

    {
        return repository.obtenerMultas()
    }
}