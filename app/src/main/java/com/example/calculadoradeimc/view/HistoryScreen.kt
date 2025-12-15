package com.example.calculadoradeimc.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.calculadoradeimc.ui.theme.Blue
import com.example.calculadoradeimc.ui.theme.White
import androidx.compose.ui.tooling.preview.Preview
import com.example.calculadoradeimc.datasource.HealthData
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit, lista: List<HealthData>, onItemClick: (HealthData) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue,
                    titleContentColor = White
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
            if (lista.isEmpty()) {
                Text("Nenhum registro ainda")
            } else {
                LazyColumn {
                    items(lista) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            onClick = { onItemClick(item) }
                        ) {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Text("Data: ${item.data}")
                                Text("IMC: %.1f".format(item.imc))
                                Text("Classificação: ${item.classificacao}")
                                Text("TMB: %.0f kcal".format(item.tmb))
                                Text("Peso Ideal: %.1f kg".format(item.pesoIdeal))
                                Text("Gordura: %.1f%%".format(item.gordura))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onBack) {
                Text("Voltar")
            }
        }
    }
}

@Preview
@Composable
private fun HistoryPreview() {
    HistoryScreen(onBack = {}, lista = emptyList(), onItemClick = {})
}