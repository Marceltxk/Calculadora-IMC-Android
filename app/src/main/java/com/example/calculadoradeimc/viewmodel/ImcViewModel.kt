package com.example.calculadoradeimc.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculadoradeimc.datasource.AppDatabase
import com.example.calculadoradeimc.datasource.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImcViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).medicaoDao()
    private val _historico = MutableStateFlow<List<HealthData>>(emptyList())
    val historico: StateFlow<List<HealthData>> = _historico

    init {
        carregarHistorico()
    }

    private fun carregarHistorico() {
        viewModelScope.launch {
            _historico.value = dao.listar()
        }
    }

    fun salvar(data: HealthData) {
        viewModelScope.launch {
            dao.inserir(data)
            carregarHistorico()
        }
    }

    fun getLista(): List<HealthData> {
        return _historico.value
    }

    fun getDataAtual(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }
}