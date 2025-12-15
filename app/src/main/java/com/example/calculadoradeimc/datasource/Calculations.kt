package com.example.calculadoradeimc.datasource

import android.annotation.SuppressLint

object Calculations {

    @SuppressLint("DefaultLocale")
    fun calculateIMC(height: String, weight: String, response: (String, Boolean) -> Unit) {

        if (height.isNotEmpty() && weight.isNotEmpty()) {

            val weightFormatted = weight.replace(",",".").toDoubleOrNull()
            val heightFormatted = height.toDoubleOrNull()

            if(weightFormatted != null && heightFormatted != null) {

                val imc = weightFormatted / (heightFormatted / 100 * heightFormatted / 100)
                val imcFormatted = String.format("%.2f",imc)

                when{
                    imc < 18.5 -> response("IMC: $imcFormatted \n Abaixo do peso",false)
                    imc in 18.5..24.9 -> response("IMC: $imcFormatted \n Peso Normal",false)
                    imc in 25.0..29.9 -> response("IMC: $imcFormatted \n Sobrepeso",false)
                    imc in 30.0..34.9 -> response("IMC: $imcFormatted \n Obesidade (Grau 1)",false)
                    imc in 35.0..39.9 -> response("IMC: $imcFormatted \n Obesidade Severa (Grau 2)",false)
                    else -> response("IMC: $imcFormatted \n Obesidade Mórbida (Grau 3)",false)
                }
            }

        }else {
            response("Preencha todos os campos!",true)
        }

    }

    fun calcularTMB(peso: Double, altura: Double, idade: Int, sexo: String): Double {
        return if (sexo == "M") {
            10 * peso + 6.25 * altura - 5 * idade + 5
        } else {
            10 * peso + 6.25 * altura - 5 * idade - 161
        }
    }

    fun calcularPesoIdeal(altura: Double, sexo: String): Double {
        return if (sexo == "M") {
            50 + 0.91 * (altura - 152.4)
        } else {
            45.5 + 0.91 * (altura - 152.4)
        }
    }

    fun calcularGordura(imc: Double, idade: Int, sexo: String): Double {
        return if (sexo == "M") {
            1.20 * imc + 0.23 * idade - 16.2
        } else {
            1.20 * imc + 0.23 * idade - 5.4
        }
    }


}