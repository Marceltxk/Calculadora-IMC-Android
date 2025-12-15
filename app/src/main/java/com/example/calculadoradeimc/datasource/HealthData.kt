package com.example.calculadoradeimc.datasource

data class HealthData(
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