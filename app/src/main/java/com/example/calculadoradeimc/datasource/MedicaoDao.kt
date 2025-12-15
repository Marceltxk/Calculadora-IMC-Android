package com.example.calculadoradeimc.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicaoDao {
    @Insert
    suspend fun inserir(medicao: HealthData)

    @Query("SELECT * FROM medicoes ORDER BY id DESC")
    suspend fun listar(): List<HealthData>
}