package com.example.appmovilsiivmex.ui.screens.multas

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmovilsiivmex.data.repository.MultasRepositoryImpl
import com.example.appmovilsiivmex.domain.model.Multa
import com.example.appmovilsiivmex.domain.usecase.ObtenerMultasUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MultasViewModel : ViewModel() {

    private val useCase = ObtenerMultasUseCase(
        MultasRepositoryImpl()
    )

    var multas by mutableStateOf<List<Multa>>(emptyList())
        private set

    var cargando by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    init {
        cargarMultas()
    }

    private fun cargarMultas() {
        viewModelScope.launch {
            try {
                val resultado = withContext(Dispatchers.IO) {
                    useCase()
                }
                multas = resultado
            } catch (e: Exception) {
                error = "Error al cargar multas"
            } finally {
                cargando = false
            }
        }
    }
}
