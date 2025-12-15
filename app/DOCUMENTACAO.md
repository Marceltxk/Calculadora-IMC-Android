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

O aplicativo utiliza uma `data class` anotada como Entity do Room para representar cada medição:

```kotlin
@Entity(tableName = "medicoes")
data class HealthData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,              // ID único auto-incrementado
    val peso: Double,             // Peso em kg
    val altura: Double,           // Altura em cm
    val idade: Int,               // Idade em anos
    val sexo: String,             // "M" ou "F"
    val imc: Double,              // IMC calculado
    val classificacao: String,    // Classificação do IMC
    val tmb: Double,              // Taxa Metabólica Basal em kcal
    val pesoIdeal: Double,        // Peso ideal em kg
    val gordura: Double,          // % de gordura estimado
    val data: String              // Data/hora da medição
)
```

**Justificativa da escolha:**
- Estrutura imutável (imutabilidade é boa prática em Kotlin)
- Anotações Room (@Entity, @PrimaryKey) permitem persistência automática
- ID auto-incrementado garante unicidade de cada registro
- Todos os dados necessários agrupados em um único objeto
- Compatível com banco de dados SQLite via Room

**Exemplo de instância:**
```kotlin
HealthData(
    id = 1,
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

### 3.1 Solução Implementada: Room Database

O aplicativo utiliza **Room** (biblioteca oficial do Android) para persistência local permanente dos dados.

#### 3.1.1 Estrutura do Room

**Entity (HealthData.kt):**
```kotlin
@Entity(tableName = "medicoes")
data class HealthData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    // ... demais campos
)
```

**DAO - Data Access Object (MedicaoDao.kt):**
```kotlin
@Dao
interface MedicaoDao {
    @Insert
    suspend fun inserir(medicao: HealthData)
    
    @Query("SELECT * FROM medicoes ORDER BY id DESC")
    suspend fun listar(): List<HealthData>
}
```

**Database (AppDatabase.kt):**
```kotlin
@Database(entities = [HealthData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun medicaoDao(): MedicaoDao
    
    companion object {
        private var instancia: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return instancia ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "imc_database"
            ).build().also { instancia = it }
        }
    }
}
```

#### 3.1.2 Integração com ViewModel

```kotlin
class ImcViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).medicaoDao()
    private val _historico = MutableStateFlow<List<HealthData>>(emptyList())
    
    init {
        carregarHistorico()
    }
    
    fun salvar(data: HealthData) {
        viewModelScope.launch {
            dao.inserir(data)
            carregarHistorico()
        }
    }
}
```

### 3.2 Vantagens da Solução Room

- **Persistência permanente:** Dados mantidos mesmo após fechar o aplicativo
- **Type-safe:** Queries verificadas em tempo de compilação
- **Integração com Coroutines:** Operações assíncronas simplificadas
- **Biblioteca oficial:** Suporte e documentação do Google
- **Abstração de SQLite:** Não precisa escrever SQL manualmente

### 3.3 Fluxo de Dados

1. Usuário calcula IMC na tela Home
2. ViewModel recebe dados e chama `dao.inserir()`
3. Room salva no banco SQLite local
4. ViewModel atualiza StateFlow após inserção
5. Tela de histórico observa StateFlow e atualiza automaticamente
6. Dados persistem no disco (pasta `/data/data/package/databases/`)

---

## 4. Melhorias Futuras

### 4.1 Funcionalidades Adicionais no Room

**Operações CRUD completas:**
```kotlin
@Dao
interface MedicaoDao {
    @Insert
    suspend fun inserir(medicao: HealthData)
    
    @Update
    suspend fun atualizar(medicao: HealthData)
    
    @Delete
    suspend fun deletar(medicao: HealthData)
    
    @Query("SELECT * FROM medicoes WHERE id = :id")
    suspend fun buscarPorId(id: Int): HealthData?
}
```

**Benefícios:** Permitiria editar ou excluir medições antigas.

### 4.2 Visualizações Gráficas

**Biblioteca sugerida:** MPAndroidChart ou Vico Chart

**Gráficos a implementar:**
- Evolução do IMC ao longo do tempo (gráfico de linha)
- Comparação TMB vs período (gráfico de barras)
- Distribuição de classificações de IMC (gráfico de pizza)

**Consulta SQL necessária:**
```kotlin
@Query("SELECT * FROM medicoes ORDER BY data ASC")
suspend fun listarParaGrafico(): List<HealthData>
```

### 4.3 Exportação de Dados

**Implementação com Room:**
```kotlin
suspend fun exportarCSV(): String {
    val medicoes = dao.listar()
    return "ID,Data,Peso,Altura,IMC,TMB\n" +
           medicoes.joinToString("\n") { 
               "${it.id},${it.data},${it.peso},${it.altura},${it.imc},${it.tmb}"
           }
}
```

### 4.4 Backup em Nuvem (Firebase)

**Sincronização Room + Firebase:**
- Manter Room como fonte local primária
- Sincronizar com Firestore periodicamente
- Resolver conflitos por timestamp (last-write-wins)

### 4.5 Migration de Banco de Dados

**Para adicionar novos campos:**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE medicoes ADD COLUMN observacoes TEXT")
    }
}
```

### 4.6 Autenticação Multi-usuário

**Adicionar campo userId:**
```kotlin
@Entity(tableName = "medicoes")
data class HealthData(
    // ... campos existentes
    val userId: String = "default"
)
```

**Query por usuário:**
```kotlin
@Query("SELECT * FROM medicoes WHERE userId = :userId ORDER BY id DESC")
suspend fun listarPorUsuario(userId: String): List<HealthData>
```

### 4.7 Notificações Periódicas

**WorkManager + Room:**
```kotlin
class LembreteWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Notificar usuário para fazer nova medição
        return Result.success()
    }
}
```

---

## 5. Conclusão

O aplicativo desenvolvido atende plenamente aos requisitos propostos, implementando:
- Cálculo de IMC e três métricas adicionais (TMB, Peso Ideal, Gordura)
- Arquitetura MVVM com separação clara de responsabilidades
- Persistência permanente com Room Database
- Interface intuitiva com Jetpack Compose
- Histórico ordenado e detalhamento de medições

A escolha por Room Database garante que os dados do usuário sejam mantidos de forma segura e eficiente no dispositivo, cumprindo o requisito de persistência local. A arquitetura implementada facilita manutenção futura e adição de novas funcionalidades.

O projeto demonstra conhecimento prático de desenvolvimento Android moderno, utilizando as melhores práticas e bibliotecas oficiais recomendadas pelo Google.

---

**Referências:**

1. Mifflin MD, St Jeor ST, et al. "A new predictive equation for resting energy expenditure in healthy individuals." Am J Clin Nutr. 1990.
2. Devine BJ. "Gentamicin therapy." Drug Intell Clin Pharm. 1974.
3. Android Developers. "Guide to app architecture." https://developer.android.com/topic/architecture
4. Android Developers. "Save data in a local database using Room." https://developer.android.com/training/data-storage/room
5. Jetpack Compose Documentation. https://developer.android.com/jetpack/compose