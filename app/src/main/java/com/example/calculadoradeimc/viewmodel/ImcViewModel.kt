package com.example.calculadoradeimc.viewmodel

import androidx.lifecycle.ViewModel
import com.example.calculadoradeimc.datasource.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImcViewModel : ViewModel() {
    private val _historico = MutableStateFlow<List<HealthData>>(emptyList())
    val historico: StateFlow<List<HealthData>> = _historico

    fun salvar(data: HealthData) {
        _historico.value = _historico.value + data
    }

    fun getDataAtual(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }

    fun getLista(): List<HealthData> {
        return _historico.value
    }
}