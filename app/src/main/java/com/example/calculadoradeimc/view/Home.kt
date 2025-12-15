package com.example.calculadoradeimc.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadoradeimc.datasource.Calculations
import com.example.calculadoradeimc.viewmodel.ImcViewModel
import com.example.calculadoradeimc.datasource.HealthData
import com.example.calculadoradeimc.ui.theme.Blue
import com.example.calculadoradeimc.ui.theme.Red
import com.example.calculadoradeimc.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(irParaHistorico: () -> Unit, viewModel: ImcViewModel) {

    var height by remember {mutableStateOf("")}
    var weight by remember {mutableStateOf("")}
    var idade by remember {mutableStateOf("")}
    var sexo by remember {mutableStateOf("M")}
    var resultMessage by remember {mutableStateOf("")}
    var textFieldError by remember {mutableStateOf(false)}


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Calculadora de IMC")
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Blue,
                    titleContentColor = White
                )
            )
        }
    ) {paddingValues ->

        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(
                color = White
            ).verticalScroll(rememberScrollState())
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "Altura (cm)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(20.dp,100.dp,0.dp,0.dp)
                )

                Text(
                    text = "Peso (kg)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(0.dp,100.dp,20.dp,0.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                OutlinedTextField(
                    value = height,
                    onValueChange = {
                        if (it.length <= 3) {
                            height = it
                        }
                    },
                    label = {
                        Text(text = "Ex: 165")
                    },
                    modifier = Modifier.fillMaxWidth(0.4f).padding(20.dp,0.dp,0.dp,20.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        errorContainerColor = White,
                        focusedLabelColor = Blue,
                        focusedIndicatorColor = Blue,
                        cursorColor = Blue,
                    ),
                    isError = textFieldError
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        if (it.length <= 7) {
                            weight = it
                        }
                    },
                    label = {
                        Text(text = "Ex: 70.50")
                    },
                    modifier = Modifier.fillMaxWidth(0.7f).padding(20.dp,0.dp,20.dp,20.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        errorContainerColor = White,
                        focusedLabelColor = Blue,
                        focusedIndicatorColor = Blue,
                        cursorColor = Blue,
                    ),
                    isError = textFieldError
                )
            }

            OutlinedTextField(
                value = idade,
                onValueChange = { if (it.length <= 3) idade = it },
                label = { Text(text = "Idade") },
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = White,
                    focusedContainerColor = White,
                    focusedLabelColor = Blue,
                    focusedIndicatorColor = Blue,
                    cursorColor = Blue
                )
            )

            Row(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Sexo: ", fontSize = 18.sp)
                Button(onClick = { sexo = "M" }, colors = ButtonDefaults.buttonColors(
                    containerColor = if(sexo == "M") Blue else White
                )) {
                    Text("M", color = if(sexo == "M") White else Blue)
                }
                Button(onClick = { sexo = "F" }, colors = ButtonDefaults.buttonColors(
                    containerColor = if(sexo == "F") Blue else White
                )) {
                    Text("F", color = if(sexo == "F") White else Blue)
                }
            }

            Button(
                onClick = {
                    if (height.isEmpty() || weight.isEmpty() || idade.isEmpty()) {
                        resultMessage = "Preencha todos os campos!"
                        textFieldError = true
                    } else {
                        textFieldError = false
                        val h = height.toDouble()
                        val w = weight.replace(",",".").toDouble()
                        val i = idade.toInt()

                        val imc = w / (h/100 * h/100)
                        val tmb = Calculations.calcularTMB(w, h, i, sexo)
                        val pi = Calculations.calcularPesoIdeal(h, sexo)
                        val gd = Calculations.calcularGordura(imc, i, sexo)

                        val cls = when {
                            imc < 18.5 -> "Abaixo do peso"
                            imc < 25.0 -> "Normal"
                            imc < 30.0 -> "Sobrepeso"
                            else -> "Obesidade"
                        }

                        viewModel.salvar(HealthData(w, h, i, sexo, imc, cls, tmb, pi, gd, viewModel.getDataAtual()))
                        resultMessage = "IMC: %.1f\nTMB: %.0f kcal\nPeso Ideal: %.1f kg\nGordura: %.1f%%".format(imc, tmb, pi, gd)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                modifier = Modifier.fillMaxWidth().height(150.dp).padding(50.dp)
            ) {
                Text(
                    text = "CALCULAR",
                    fontSize = 18.sp,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = resultMessage,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Blue,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            )

            Button(
                onClick = irParaHistorico,
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Ver Histórico", color = White)
            }
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    Home(irParaHistorico = {}, viewModel = ImcViewModel())
}