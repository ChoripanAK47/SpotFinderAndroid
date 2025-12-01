package com.example.spotfinder

import android.content.Context
import android.content.SharedPreferences
import com.example.spotfinder.util.SessionManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerTest {

    // Mockeamos todo el sistema de guardado de Android
    private val context: Context = mockk()
    private val prefs: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true) // relaxed = true para no configurar cada void

    @Test
    fun `setLoggedIn guarda true en preferencias`() {
        // Enseñamos al mock cómo comportarse
        every { context.getSharedPreferences("SpotFinderPrefs", Context.MODE_PRIVATE) } returns prefs
        every { prefs.edit() } returns editor
        every { editor.putBoolean("is_logged_in", true) } returns editor
        every { editor.apply() } returns Unit // apply no devuelve nada

        val sessionManager = SessionManager(context)
        sessionManager.setLoggedIn(true)

        // Verificamos que se llamó a guardar
        verify { editor.putBoolean("is_logged_in", true) }
        verify { editor.apply() }
    }

    @Test
    fun `isLoggedIn devuelve valor guardado`() {
        every { context.getSharedPreferences("SpotFinderPrefs", Context.MODE_PRIVATE) } returns prefs
        every { prefs.getBoolean("is_logged_in", false) } returns true

        val sessionManager = SessionManager(context)
        val result = sessionManager.isLoggedIn()

        assertTrue(result)
    }
}