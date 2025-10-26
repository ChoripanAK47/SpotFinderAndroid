package com.example.spotfinder.model // Asegúrate que el paquete sea correcto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// @Entity define una tabla llamada "users"
// indices asegura que el email sea único para evitar duplicados
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["email"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) // El ID se crea solo y es único
    val id: Int = 0,
    val nombre: String,
    val email: String,
    // Guardaremos la contraseña directamente (NO SEGURO PARA PRODUCCIÓN)
    val contrasena: String,
    val genero: String,
    val aceptaTerminos: Boolean
)