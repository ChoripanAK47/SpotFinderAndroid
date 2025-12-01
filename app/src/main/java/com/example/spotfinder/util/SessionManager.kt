package com.example.spotfinder.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Clase para gestionar la sesión del usuario usando SharedPreferences.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("SpotFinderPrefs", Context.MODE_PRIVATE)

    companion object {
        const val IS_LOGGED_IN = "is_logged_in"
        const val KEY_TOKEN = "auth_token"
    }

    /**
     * Guarda el estado de la sesión.
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    /**
     * Comprueba si el usuario tiene una sesión activa.
     * @return `true` si la sesión está iniciada, `false` en caso contrario.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }

    // --- Token handling ---
    fun setToken(token: String?) {
        val editor = prefs.edit()
        if (token == null) {
            editor.remove(KEY_TOKEN)
        } else {
            editor.putString(KEY_TOKEN, token)
        }
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(KEY_TOKEN)
        editor.putBoolean(IS_LOGGED_IN, false)
        editor.apply()
    }
}