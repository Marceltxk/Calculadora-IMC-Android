# Documentação Técnica - Calculadora de IMC

**Disciplina:** Programação para Dispositivos Móveis  
**Instituição:** Universidade Federal de Uberlândia  
**Data:** 15/12/2025

**Integrantes:**
- Marcel Teixeira Chiarelo
- Gisele Leite Iasbeck

---

## 1. Fórmulas Utilizadas

### 1.1 IMC (Índice de Massa Corporal)

O IMC é calculado pela divisão do peso (em quilogramas) pela altura (em metros) ao quadrado:
```
IMC = peso / (altura / 100)²
```

**Exemplo:** Pessoa com 70kg e 1,70m
```
IMC = 70 / (170/100)² = 70 / 2,89 = 24,2
```

**Classificação adotada:**
- Abaixo de 18,5: Abaixo do peso
- 18,5 a 24,9: Peso normal
- 25,0 a 29,9: Sobrepeso
- Acima de 30,0: Obesidade

### 1.2 Taxa Metabólica Basal (TMB)

Utilizamos a **Fórmula de Mifflin-St Jeor** (1990), considerada mais precisa que a fórmula de Harris-Benedict para populações modernas.

**Homens:**
```
TMB = 10 × peso (kg) + 6,25 × altura (cm) - 5 × idade (anos) + 5
```

**Mulheres:**
```
TMB = 10 × peso (kg) + 6,25 × altura (cm) - 5 × idade (anos) - 161
```

**Interpretação:** A TMB representa o gasto calórico mínimo diário do corpo em repouso, necessário para manter funções vitais como respiração, circulação e temperatura corporal.

**Exemplo:** Homem de 25 anos, 70kg, 170cm
```
TMB = 10 × 70 + 6,25 × 170 - 5 × 25 + 5
TMB = 700 + 1062,5 - 125 + 5 = 1642,5 kcal/dia
```

### 1.3 Peso Ideal

Utilizamos a **Fórmula de Devine** (1974), amplamente aceita na comunidade médica.

**Homens:**
```
Peso Ideal = 50 + 0,91 × (altura (cm) - 152,4)
```

**Mulheres:**
```
Peso Ideal = 45,5 + 0,91 × (altura (cm) - 152,4)
```

**Exemplo:** Homem com 170cm
```
Peso Ideal = 50 + 0,91 × (170 - 152,4)
Peso Ideal = 50 + 0,91 × 17,6 = 50 + 16,02 = 66,02 kg
```

### 1.4 Percentual de Gordura Corporal

Estimativa baseada em IMC, idade e sexo. Esta é uma aproximação simplificada.

**Homens:**
```
Gordura (%) = 1,20 × IMC + 0,23 × idade - 16,2
```

**Mulheres:**
```
Gordura (%) = 1,20 × IMC + 0,23 × idade - 5,4
```

**Observação:** Esta fórmula fornece apenas uma estimativa. Métodos mais precisos incluem bioimpedância, DEXA scan ou medição de dobras cutâneas.

---

## 2. Modelo de Dados

O aplicativo utiliza uma classe de dados (`data class`) simples para representar cada medição:
```kotlin
data class HealthData(
    val peso: Double,           // Peso em kg
    val altura: Double,         // Altura em cm
    val idade: Int,             // Idade em anos
    val sexo: String,           // "M" ou "F"
    val imc: Double,            // IMC calculado
    val classificacao: String,  // Classificação do IMC
    val tmb: Double,            // Taxa Metabólica Basal em kcal
    val pesoIdeal: Double,      // Peso ideal em kg
    val gordura: Double,        // % de gordura estimado
    val data: String            // Data/hora da medição
)
```

**Justificativa da escolha:**
- Estrutura simples e imutável (imutabilidade é boa prática em Kotlin)
- Todos os dados necessários agrupados em um único objeto
- Facilita passagem de dados entre telas
- Compatível com futuras implementações de banco de dados

**Exemplo de instância:**
```kotlin
HealthData(
    peso = 70.0,
    altura = 170.0,
    idade = 25,
    sexo = "M",
    imc = 24.2,
    classificacao = "Normal",
    tmb = 1650.0,
    pesoIdeal = 68.5,
    gordura = 18.5,
    data = "15/12/2025 14:30"
)
```

---

## 3. Implementação da Persistência

### 3.1 Solução Atual: Armazenamento em Memória

O aplicativo utiliza **StateFlow** no ViewModel para armazenar o histórico de medições em memória RAM durante a sessão:
```kotlin
class ImcViewModel : ViewModel() {
    private val _historico = MutableStateFlow<List<HealthData>>(emptyList())
    val historico: StateFlow<List<HealthData>> = _historico
    
    fun salvar(data: HealthData) {
        _historico.value = _historico.value + data
    }
    
    fun getLista(): List<HealthData> {
        return _historico.value
    }
}
```

**Vantagens desta abordagem:**
- Simplicidade de implementação
- Sem dependências externas
- Rápido acesso aos dados
- Adequado para escopo acadêmico

**Limitações:**
- Dados perdidos ao fechar o aplicativo
- Não há sincronização entre dispositivos
- Limitado pela memória RAM disponível

### 3.2 Por que não usamos Room Database?

Durante o desenvolvimento, consideramos usar Room (biblioteca oficial do Android para persistência local), mas optamos pela solução em memória devido a:

1. **Complexidade adicional**: Room requer configuração de DAO, Entity annotations e Database abstract class
2. **Tempo de desenvolvimento**: O prazo curto do trabalho exigia foco nas funcionalidades principais
3. **Requisitos do projeto**: O trabalho não exigia persistência permanente obrigatória
4. **Simplicidade do código**: Facilita a apresentação e explicação durante a avaliação

---

## 4. Melhorias Futuras

### 4.1 Persistência com Room Database

**Implementação:** Usar Room para salvar dados permanentemente no SQLite local do Android.

**Código exemplo:**
```kotlin
@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val peso: Double,
    val altura: Double,
    // ... demais campos
)

@Dao
interface MeasurementDao {
    @Insert
    suspend fun insert(measurement: Measurement)
    
    @Query("SELECT * FROM measurements ORDER BY date DESC")
    fun getAll(): Flow<List<Measurement>>
}
```

**Benefícios:** Histórico permanente, queries otimizadas, suporte a relações complexas.

### 4.2 Integração com API Externa de Saúde

**Possibilidades:**
- **Google Fit API**: Sincronizar peso e altura automaticamente
- **Apple HealthKit** (para versão iOS futura)
- **Fitbit API**: Importar dados de dispositivos wearables
- **MyFitnessPal API**: Integrar com diário alimentar

**Exemplo de uso:**
```kotlin
// Pseudo-código
val googleFitClient = GoogleFit.getClient(context)
val weightData = googleFitClient.readData(DataType.WEIGHT)
// Usar dados para cálculo automático
```

### 4.3 Autenticação de Usuário

**Implementação com Firebase Authentication:**
- Login com email/senha
- Login social (Google, Facebook)
- Cada usuário teria seu próprio histórico

**Benefícios:**
- Múltiplos usuários no mesmo dispositivo
- Sincronização na nuvem (Firestore)
- Acesso de qualquer dispositivo

### 4.4 Visualizações Gráficas

**Biblioteca sugerida:** Vico Chart ou MPAndroidChart

**Gráficos a implementar:**
- Evolução do IMC ao longo do tempo (linha)
- Comparação TMB vs consumo calórico (barras)
- Distribuição de classificações de IMC (pizza)

**Código exemplo com Vico:**
```kotlin
Chart(
    chart = lineChart(),
    model = entryModelOf(historico.map { it.imc }),
    startAxis = startAxis(),
    bottomAxis = bottomAxis()
)
```

### 4.5 Notificações e Lembretes

**Implementação:** WorkManager para agendamento
```kotlin
val reminderWork = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
    .build()
WorkManager.getInstance(context).enqueue(reminderWork)
```

**Funcionalidade:** Lembrar usuário de registrar medições semanais.

### 4.6 Exportação de Dados

**Formatos sugeridos:**
- **CSV**: Para análise em Excel/Google Sheets
- **PDF**: Relatório visual formatado
- **JSON**: Para backup e portabilidade

**Exemplo de exportação CSV:**
```kotlin
fun exportarCSV(historico: List<HealthData>): String {
    return "Data,Peso,Altura,IMC,TMB\n" +
           historico.joinToString("\n") { 
               "${it.data},${it.peso},${it.altura},${it.imc},${it.tmb}"
           }
}
```

### 4.7 Cálculo de Necessidade Calórica Diária

**Fórmula:** TMB × Fator de Atividade

| Nível de Atividade | Fator |
|-------------------|-------|
| Sedentário | 1.2 |
| Levemente ativo | 1.375 |
| Moderadamente ativo | 1.55 |
| Muito ativo | 1.725 |
| Extremamente ativo | 1.9 |

**Interface sugerida:** Slider ou RadioButtons para selecionar nível de atividade.

### 4.8 Suporte a Múltiplas Unidades

**Implementação:**
- Toggle entre Sistema Métrico (kg/cm) e Imperial (lb/in)
- Conversão automática nos cálculos

### 4.9 Modo Escuro

**Implementação:** Tema escuro com Material 3
```kotlin
@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme()) {
    val colors = if (darkTheme) darkColorScheme() else lightColorScheme()
    MaterialTheme(colorScheme = colors) { /* ... */ }
}
```

---

## 5. Conclusão

O aplicativo desenvolvido atende aos requisitos propostos, implementando cálculo de IMC e três métricas adicionais de saúde (TMB, Peso Ideal e Gordura Corporal). A arquitetura MVVM simplificada proporciona separação clara de responsabilidades e facilita manutenção futura.

A escolha por armazenamento em memória foi adequada ao escopo acadêmico, mas as melhorias sugeridas (especialmente Room Database e visualizações gráficas) tornariam o aplicativo viável para uso real em contextos de saúde e bem-estar.

---

**Referências:**

1. Mifflin MD, St Jeor ST, et al. "A new predictive equation for resting energy expenditure in healthy individuals." Am J Clin Nutr. 1990.
2. Devine BJ. "Gentamicin therapy." Drug Intell Clin Pharm. 1974.
3. Android Developers. "Guide to app architecture." https://developer.android.com/topic/architecture
4. Jetpack Compose Documentation. https://developer.android.com/jetpack/compose