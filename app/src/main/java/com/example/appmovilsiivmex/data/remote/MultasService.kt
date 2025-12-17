package com.example.appmovilsiivmex.data.remote

import okhttp3.OkHttpClient
import com.example.appmovilsiivmex.domain.model.Multa
import okhttp3.Request
import org.json.JSONArray

object MultasService {

    private val client = OkHttpClient()

    private const val URL =
        "http://TU_IP_LOCAL:5000/api/multas"

    fun obtenerMultas(): List<Multa> {

        val request = Request.Builder()
            .url(URL)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Error HTTP ${response.code}")
            }

            val body = response.body?.string() ?: return emptyList()
            val jsonArray = JSONArray(body)

            val multas = mutableListOf<Multa>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                multas.add(
                    Multa(
                        folio = obj.getString("folio"),
                        fecha = obj.getString("fecha"),
                        motivo = obj.getString("motivo"),
                        entidad = obj.getString("entidad")
                    )
                )
            }

            return multas
        }
    }
}