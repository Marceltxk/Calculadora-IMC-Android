# Uso de LLMs no Projeto

**Integrantes:** [Nome 1] e [Nome 2]  
**LLM usada:** Claude

---

## Fórmulas de cálculo

**Prompt:**
> Crie funções para calcular TMB, Peso Ideal e percentual de gordura corporal baseado em peso, altura, idade e sexo.

**Arquivo gerado:** Calculations.kt

```kotlin
/** Claude (Anthropic) - início
 * Prompt: Criar funções de cálculo TMB, Peso Ideal e Gordura
 */
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
/** Claude (Anthropic) - final */
```

**Modificações:** Nenhuma, funcionou direto

---

## Configuração do Room

**Contexto:** No início tentamos fazer sem Room, só com StateFlow em memória. Depois descobrimos que precisava persistir no disco mesmo.

**Prompt:**
> Como configurar Room Database no Android? Preciso salvar dados de medições que contenham peso, altura, IMC, data.

**Código gerado:**

```kotlin
/** Claude (Anthropic) - início
 * Prompt: Estrutura básica Room Database
 */
@Entity(tableName = "medicoes")
data class HealthData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val peso: Double,
    val altura: Double,
    val imc: Double,
    val data: String
)
/** Claude (Anthropic) - final */
```

**Modificações feitas:**

```kotlin
/** Modificação feita - início
 * Razão: Adicionei mais campos necessários pro projeto
 */
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
/** Modificação feita - final */
```

---

## DAO

**Prompt:**
> Preciso de um DAO simples para inserir e listar medições do banco Room

```kotlin
/** Claude (Anthropic) - início
 * Prompt: DAO básico
 */
@Dao
interface MedicaoDao {
    @Insert
    suspend fun inserir(medicao: HealthData)
    
    @Query("SELECT * FROM medicoes ORDER BY id DESC")
    suspend fun listar(): List<HealthData>
}
/** Claude (Anthropic) - final */
```

**Modificações:** Nenhuma

---

## Problema com Application context

**Erro:** "No value passed for parameter 'application'"

**Prompt:**
> Está dando erro "No value passed for parameter 'application'" no ViewModel. Como passar o Application context?

**Solução:** Usar ViewModelProvider.Factory

```kotlin
/** Modificação feita - início
 * Razão: Adaptei solução da LLM e adicionei @Suppress pro warning
 */
val vm = viewModel<ImcViewModel>(
    factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ImcViewModel(applicationContext as Application) as T
        }
    }
)
/** Modificação feita - final */
```

---

## Navegação

**Prompt:**
> Como fazer navegação simples entre 3 telas sem usar NavHost?

**Código:**
```kotlin
var tela by remember { mutableStateOf("home") }

when(tela) {
    "home" -> Home(...)
    "historico" -> HistoryScreen(...)
    "detalhes" -> DetailScreen(...)
}
```

Funcionou bem assim, não precisei modificar nada.

---

## Tela de histórico com LazyColumn

**Prompt:**
> Crie tela de histórico que mostra lista com LazyColumn

A LLM mostrou a estrutura básica, eu ajustei os campos que queria mostrar:

```kotlin
/** Modificação feita - início
 * Razão: Usei estrutura da LLM mas mudei quais dados exibir
 */
LazyColumn {
    items(lista) { item ->
        Card(onClick = { onItemClick(item) }) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text("Data: ${item.data}")
                Text("IMC: %.1f".format(item.imc))
                Text("TMB: %.0f kcal".format(item.tmb))
                Text("Peso Ideal: %.1f kg".format(item.pesoIdeal))
                Text("Gordura: %.1f%%".format(item.gordura))
            }
        }
    }
}
/** Modificação feita - final */
```

---

## Dificuldades encontradas

**Build muito lento**
- Primeira build com Room demorou uns 3 minutos
- Tivemos que esperar o KSP processar tudo
- Depois ficou normal

**Confusão sobre persistência**
- Achei que StateFlow salvava no disco mas não salva
- Só funciona durante a sessão do app
- Tive que implementar Room depois

**Preview não funciona com Room**
- Tentei manter o @Preview mas deu erro
- Tive que deletar porque Room precisa do contexto real do Android

---

## O que aprendi

- Room salva no disco, StateFlow é só memória
- Operações de banco precisam ser assíncronas
- AndroidViewModel precisa de Application context
- Coroutines facilitam operações em background

---

## Opinião sobre usar LLM

**Ajudou:**
- Explicou Room de forma simples
- Resolveu erros rapidamente
- Mostrou exemplos práticos

**Cuidados:**
- Tem que testar tudo
- Precisa entender o código
- Às vezes sugere coisa mais complexa que o necessário