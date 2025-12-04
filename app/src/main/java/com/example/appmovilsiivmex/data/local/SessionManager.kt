package com.example.appmovilsiivmex.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.appmovilsiivmex.domain.model.Vehicle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_preferences")

class SessionManager(private val context: Context) {

    private val TAG = "SessionManager"
    private val gson = Gson()

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

        // Nuevas keys para vehículos
        private val VEHICLES_JSON_KEY = stringPreferencesKey("vehicles_json")
        private val SELECTED_VEHICLE_ID_KEY = intPreferencesKey("selected_vehicle_id")
    }

    // ─────────────────────────────────────────────
    // GUARDAR SESIÓN (simple)
    // ─────────────────────────────────────────────
    suspend fun saveSession(userId: Int, email: String, name: String) {
        saveSession(userId, email, name, vehicles = emptyList(), selectedVehicleId = null)
    }

    // ─────────────────────────────────────────────
    // GUARDAR SESIÓN + VEHÍCULOS + VEHÍCULO SELECCIONADO
    // ─────────────────────────────────────────────
    suspend fun saveSession(
        userId: Int,
        email: String,
        name: String,
        vehicles: List<Vehicle>,
        selectedVehicleId: Int?
    ) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "📝 GUARDANDO SESIÓN COMPLETA")
        Log.d(TAG, "User ID: $userId")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "Name: $name")
        Log.d(TAG, "Vehicles count: ${vehicles.size}")
        Log.d(TAG, "Selected vehicle ID: $selectedVehicleId")

        val vehiclesJson = gson.toJson(vehicles)

        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[IS_LOGGED_IN_KEY] = true

            // Guardamos lista de vehículos en JSON
            preferences[VEHICLES_JSON_KEY] = vehiclesJson

            // Guardamos vehículo seleccionado (si hay)
            if (selectedVehicleId != null) {
                preferences[SELECTED_VEHICLE_ID_KEY] = selectedVehicleId
            } else {
                preferences.remove(SELECTED_VEHICLE_ID_KEY)
            }
        }
    }

    // ─────────────────────────────────────────────
    // ESTADO DE SESIÓN
    // ─────────────────────────────────────────────
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[IS_LOGGED_IN_KEY] ?: false
    }

    fun isLoggedInFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    // ─────────────────────────────────────────────
    // DATOS DE USUARIO
    // ─────────────────────────────────────────────
    suspend fun getUserId(): Int? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_ID_KEY]
    }

    suspend fun getUserEmail(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_EMAIL_KEY]
    }

    suspend fun getUserName(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[USER_NAME_KEY]
    }

    // ─────────────────────────────────────────────
    // VEHÍCULOS
    // ─────────────────────────────────────────────
    suspend fun getVehicles(): List<Vehicle> {
        val prefs = context.dataStore.data.first()
        val json = prefs[VEHICLES_JSON_KEY] ?: return emptyList()

        return try {
            val type = object : TypeToken<List<Vehicle>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "Error parseando vehículos desde JSON", e)
            emptyList()
        }
    }

    fun vehiclesFlow(): Flow<List<Vehicle>> {
        return context.dataStore.data.map { prefs ->
            val json = prefs[VEHICLES_JSON_KEY] ?: return@map emptyList<Vehicle>()
            try {
                val type = object : TypeToken<List<Vehicle>>() {}.type
                gson.fromJson<List<Vehicle>>(json, type)
            } catch (e: Exception) {
                Log.e(TAG, "Error parseando vehículos en Flow", e)
                emptyList()
            }
        }
    }

    // ─────────────────────────────────────────────
    // VEHÍCULO SELECCIONADO
    // ─────────────────────────────────────────────
    suspend fun getSelectedVehicleId(): Int? {
        val prefs = context.dataStore.data.first()
        return prefs[SELECTED_VEHICLE_ID_KEY]
    }

    suspend fun saveSelectedVehicleId(vehicleId: Int?) {
        context.dataStore.edit { prefs ->
            if (vehicleId != null) {
                prefs[SELECTED_VEHICLE_ID_KEY] = vehicleId
            } else {
                prefs.remove(SELECTED_VEHICLE_ID_KEY)
            }
        }
    }

    fun selectedVehicleIdFlow(): Flow<Int?> {
        return context.dataStore.data.map { prefs ->
            prefs[SELECTED_VEHICLE_ID_KEY]
        }
    }

    // ─────────────────────────────────────────────
    // CERRAR SESIÓN
    // ─────────────────────────────────────────────
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
