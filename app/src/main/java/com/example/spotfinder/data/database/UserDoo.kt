package com.example.spotfinder.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao // Indica que es un Data Access Object
interface UserDao {

    // Inserta un usuario. Si el email ya existe, ABORTA.
    // 'suspend' significa que debe llamarse desde una Coroutine (para no bloquear la UI)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    // Busca un usuario por su email. Devuelve el User o null si no lo encuentra.
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?
}