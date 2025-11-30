package com.example.appmovilsiivmex.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_preferences")

class SessionManager(private val context: Context) {

    private val TAG = "SessionManager"

    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_NAME_KEY = stringPreferencesKey("user_name")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
    }

    // Función para guardar la sesión
    suspend fun saveSession(userId: Int, email: String, name: String) {

        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "📝 GUARDANDO SESIÓN")
        Log.d(TAG, "User ID: $userId")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "Name: $name")

        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_NAME_KEY] = name
            preferences[IS_LOGGED_IN_KEY] = true
        }
    }

    // Función para verificar si hay sesión activa
    suspend fun isLoggedIn(): Boolean {
        val preferences = context.dataStore.data.first()
        return preferences[IS_LOGGED_IN_KEY] ?: false
    }

    // Función para observar el estado de la sesión
    fun isLoggedInFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[IS_LOGGED_IN_KEY] ?: false
        }
    }

    // Funciones para obtener datos del usuario
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

    // Función para cerrar sesión
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}