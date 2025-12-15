package com.example.calculadoradeimc.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HealthData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicaoDao(): MedicaoDao

    companion object {
        private var instancia: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "imc_database"
            ).build().also { instancia = it }
        }
    }
}