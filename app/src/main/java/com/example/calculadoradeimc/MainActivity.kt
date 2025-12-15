package com.example.calculadoradeimc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.calculadoradeimc.view.Home
import com.example.calculadoradeimc.view.HistoryScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculadoradeimc.viewmodel.ImcViewModel
import com.example.calculadoradeimc.datasource.HealthData
import com.example.calculadoradeimc.view.DetailScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.app.Application

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm = viewModel<ImcViewModel>(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ImcViewModel(applicationContext as Application) as T
                    }
                }
            )
            var tela by remember { mutableStateOf("home") }
            var selecionado by remember { mutableStateOf<HealthData?>(null) }

            when(tela) {
                "home" -> Home(irParaHistorico = { tela = "historico" }, viewModel = vm)
                "historico" -> HistoryScreen(
                    onBack = { tela = "home" },
                    lista = vm.getLista(),
                    onItemClick = { item ->
                        selecionado = item
                        tela = "detalhes"
                    }
                )
                "detalhes" -> selecionado?.let {
                    DetailScreen(data = it, onBack = { tela = "historico" })
                }
            }
        }
    }
}