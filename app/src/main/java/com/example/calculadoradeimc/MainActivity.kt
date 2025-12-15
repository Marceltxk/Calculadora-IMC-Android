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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: ImcViewModel = viewModel()
            var mostrarHistorico by remember { mutableStateOf(false) }

            if (mostrarHistorico) {
                HistoryScreen(onBack = { mostrarHistorico = false }, lista = vm.getLista())
            } else {
                Home(irParaHistorico = { mostrarHistorico = true }, viewModel = vm)
            }
        }
    }
}