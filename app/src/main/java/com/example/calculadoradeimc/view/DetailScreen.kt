package com.example.calculadoradeimc.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoradeimc.datasource.HealthData
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculadoradeimc.ui.theme.Blue
import com.example.calculadoradeimc.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(data: HealthData, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue,
                    titleContentColor = White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            Text("Data: ${data.data}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text("Peso: ${data.peso} kg")
            Text("Altura: ${data.altura} cm")
            Text("Idade: ${data.idade} anos")
            Text("Sexo: ${data.sexo}")
            Spacer(modifier = Modifier.height(15.dp))
            Text("IMC: %.2f".format(data.imc), fontSize = 20.sp)
            Text("Classificação: ${data.classificacao}")
            Spacer(modifier = Modifier.height(10.dp))
            Text("TMB: %.0f kcal".format(data.tmb))
            Text("Peso Ideal: %.1f kg".format(data.pesoIdeal))
            Text("Gordura Corporal: %.1f%%".format(data.gordura))
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = onBack) {
                Text("Voltar")
            }
        }
    }
}

@Preview
@Composable
private fun DetailPreview() {
    val exemplo = HealthData(70.0, 170.0, 25, "M", 24.2, "Normal", 1650.0, 68.5, 18.5, "15/12/2025")
    DetailScreen(data = exemplo, onBack = {})
}