package com.example.calculadoradeimc.datasource

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicoes")
data class HealthData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val peso: Double,
    val altura: Double,
    val idade: Int,
    val sexo: String,
    val imc: Double,
    val classificacao: String,
    val tmb: Double,
    val pesoIdeal: Double,
    val gordura: Double,
    val data: String
)