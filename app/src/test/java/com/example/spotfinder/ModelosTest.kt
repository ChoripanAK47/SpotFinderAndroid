package com.example.spotfinder

import com.example.spotfinder.model.Spot
import com.example.spotfinder.model.Usuario
import com.example.spotfinder.view.LoginScreen
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelosTest {

    @Test
    fun `probar modelo Usuario`() {
        val usuario = Usuario(
            id = 1,
            nombre = "Test",
            email = "test@mail.com",
            contrasena = "123",
            genero = "M",
            aceptaTerminos = true
        )
        // Al acceder a las propiedades, cubrimos el código generado por Kotlin
        assertEquals("Test", usuario.nombre)
        assertEquals("test@mail.com", usuario.email)
        assertEquals(true, usuario.aceptaTerminos)
    }

    @Test
    fun `probar modelo Spot`() {
        val spot = Spot(
            idSpot = 5,
            nombreSpot = "Plaza",
            descripcionSpot = "Bonita",
            comunaSpot = "Centro",
            ubicacionSpot = "10,10",
            imageUrl = "http://img.com"
        )
        assertEquals("Plaza", spot.nombreSpot)
        assertEquals(5, spot.idSpot)
    }

    @Test
    fun `probar LoginRequest`() { // Cambiado el nombre del test
        // AQUÍ ESTABA EL ERROR: Usamos LoginRequest, no LoginScreen
        val request = com.example.spotfinder.model.LoginRequest("a@a.com", "123")

        assertEquals("a@a.com", request.email)
    }
}