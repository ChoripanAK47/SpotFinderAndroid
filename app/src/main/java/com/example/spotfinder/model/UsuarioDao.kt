package com.example.spotfinder.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.spotfinder.model.Usuario

@Dao // Indica que es un Data Access Object
interface UsuarioDao {

    // Inserta un usuario. Si el email ya existe, ABORTA.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUsuario(user: Usuario)

    // Busca un usuario por su email.
    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun getUsuarioByEmail(email: String): Usuario?

    // Busca un usuario por email y contraseña.
    @Query("SELECT * FROM usuarios WHERE email = :email AND contrasena = :password LIMIT 1")
    suspend fun getUsuario(email: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY id DESC")
    suspend fun obtenerUsuarios(): List<Usuario>
    @Update
    suspend fun update(usuario: Usuario)
}