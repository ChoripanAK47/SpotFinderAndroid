package com.example.spotfinder.repository

import com.example.spotfinder.model.UsuarioDao
import com.example.spotfinder.model.Usuario

class UsuarioRepository(private val dao: UsuarioDao) {
    
    suspend fun obtenerUsuarios(): List<Usuario> = dao.obtenerUsuarios()
    
    suspend fun insert(usuario: Usuario) {
        dao.insertUsuario(usuario)
    }

    suspend fun getUsuario(email: String, password: String): Usuario? {
        return dao.getUsuario(email, password)
    }
}