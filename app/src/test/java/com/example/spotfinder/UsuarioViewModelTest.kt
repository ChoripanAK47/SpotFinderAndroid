package com.example.spotfinder

import com.example.spotfinder.model.Usuario
import com.example.spotfinder.repository.UsuarioRepository
import com.example.spotfinder.viewmodel.LoginState
import com.example.spotfinder.viewmodel.RegisterState
import com.example.spotfinder.viewmodel.UsuarioViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // 1. Mockeamos el Repositorio
    private val repository: UsuarioRepository = mockk(relaxed = true)

    // 2. ViewModel a probar
    private lateinit var viewModel: UsuarioViewModel

    @Test
    fun `login exitoso cambia estado a Success`() = runTest {
        // GIVEN
        val userFake = Usuario(1, "Test", "a@a.com", "123", "M", true)
        coEvery { repository.getUsuario("a@a.com", "123") } returns userFake

        viewModel = UsuarioViewModel(repository)

        // WHEN
        viewModel.login("a@a.com", "123")

        // THEN
        assertTrue(viewModel.loginState.value is LoginState.Success)
    }

    @Test
    fun `login fallido cambia estado a Error`() = runTest {
        // GIVEN
        coEvery { repository.getUsuario(any(), any()) } returns null

        viewModel = UsuarioViewModel(repository)

        // WHEN
        viewModel.login("malo@a.com", "000")

        // THEN
        assertTrue(viewModel.loginState.value is LoginState.Error)
    }

    @Test
    fun `register exitoso llama a guardar`() = runTest {
        viewModel = UsuarioViewModel(repository)

        // WHEN
        viewModel.register("Pepe", "p@p.com", "123", "123")

        // THEN
        assertTrue(viewModel.registerState.value is RegisterState.Success)
        coVerify { repository.insert(any()) }
    }
}