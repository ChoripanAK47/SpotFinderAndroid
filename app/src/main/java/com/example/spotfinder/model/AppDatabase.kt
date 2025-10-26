package com.example.spotfinder.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.spotfinder.model.UsuarioDao
import com.example.spotfinder.model.SpotDao

// Aumentamos la versión por el cambio en la estructura
@Database(entities = [Usuario::class, Spot::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Funciones abstractas para obtener los DAOs
    abstract fun usuarioDao(): UsuarioDao
    abstract fun spotDao(): SpotDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spotfinder_database"
                )
                // Solución simple para desarrollo: destruye y recrea la DB si el schema cambia
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}