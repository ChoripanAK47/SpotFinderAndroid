package com.example.spotfinder

import com.example.spotfinder.model.Spot
import com.example.spotfinder.model.SpotDao
import com.example.spotfinder.repository.SpotRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SpotRepositoryTest {

    // 1. Solo mockeamos el DAO aquí arriba (EL REPOSITORIO NO)
    private val spotDao: SpotDao = mockk(relaxed = true)

    @Test
    fun `insert llama al dao`() = runTest {
        // Creamos el repositorio AQUÍ ADENTRO
        val repository = SpotRepository(spotDao)
        val spot = Spot(nombreSpot = "A", descripcionSpot = "B", comunaSpot = "C", ubicacionSpot = "", imageUrl = "")

        repository.insert(spot)

        coVerify { spotDao.insertSpot(spot) }
    }

    @Test
    fun `delete llama al dao`() = runTest {
        // Creamos el repositorio AQUÍ ADENTRO
        val repository = SpotRepository(spotDao)
        val spot = Spot(nombreSpot = "A", descripcionSpot = "B", comunaSpot = "C", ubicacionSpot = "", imageUrl = "")

        repository.delete(spot)

        coVerify { spotDao.delete(spot) }
    }

    @Test
    fun `getAllSpots devuelve lo que diga el dao`() = runTest {
        // 1. PRIMERO preparamos los datos
        val lista = listOf(Spot(nombreSpot = "Test", descripcionSpot = "", comunaSpot = "", ubicacionSpot = "", imageUrl = ""))
        coEvery { spotDao.getAllSpots() } returns flowOf(lista)

        // 2. DESPUÉS creamos el repositorio (para que lea los datos que acabamos de preparar)
        val repository = SpotRepository(spotDao)

        // 3. Probamos
        var resultado: List<Spot>? = null
        repository.allSpots.collect {
            resultado = it
        }

        assertEquals(1, resultado?.size)
    }
}