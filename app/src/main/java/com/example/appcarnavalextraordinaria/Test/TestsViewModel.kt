package com.example.appcarnavalextraordinaria.Test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcarnavalextraordinaria.Data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TestsViewModel(
    private val testDao: TestDao,
    private val testResultDao: TestResultDao,
    private val currentUserId: Int
) : ViewModel() {

    val tests = testDao.getAllTests().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Añade un estado para saber si está creando tests
    private val _isCreatingTests = MutableStateFlow(false)
    val isCreatingTests = _isCreatingTests.asStateFlow()

    init {
        println("DEBUG: TestsViewModel creado - UserId: $currentUserId")
    }

    fun createSampleTestsIfFirstLaunch(context: Context) {
        // Evitar múltiples llamadas simultáneas
        if (_isCreatingTests.value) return

        viewModelScope.launch {
            _isCreatingTests.value = true
            try {
                createTests(context)
            } catch (e: Exception) {
                println("ERROR CRÍTICO: ${e.message}")
                e.printStackTrace()
            } finally {
                _isCreatingTests.value = false
            }
        }
    }

    private suspend fun createTests(context: Context) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("first_launch_tests", true)



        // Verificar si ya hay tests
        val existingTests = testDao.getAllTests().first()
        println("DEBUG: Tests existentes en BD: ${existingTests.size}")



        if (!firstLaunch) {
            println("DEBUG: No es primera vez y no hay tests, creando de todos modos")
        }

        println("DEBUG: Iniciando creación de tests...")

        // Limpiar tablas
        testDao.deleteAllQuestions()
        testDao.deleteAllTests()

        val test1Id = testDao.insertTest(
            TestEntity(
                title = "Fundamentos del Mus",
                description = "Conceptos básicos y reglas esenciales"

            )
        ).toInt()

        val test2Id = testDao.insertTest(
            TestEntity(
                title = "Señales y Comunicación",
                description = "Lenguaje no verbal y estrategias de equipo"

            )
        ).toInt()

        val test3Id = testDao.insertTest(
            TestEntity(
                title = "Estrategias Avanzadas",
                description = "Técnicas para jugadores experimentados"

            )
        ).toInt()

        val test4Id = testDao.insertTest(
            TestEntity(
                title = "Puntuación y Juego",
                description = "Cálculo de puntos y situaciones de juego"

            )
        ).toInt()
        val test5Id = testDao.insertTest(
            TestEntity(
                title = "Situaciones Especiales",
                description = "Casos particulares y reglas avanzadas"

            )
        ).toInt()
        val test6Id = testDao.insertTest(
            TestEntity(
                title = "Historia y Cultura",
                description = "Orígenes y variantes del Mus"

            )
        ).toInt()

        // Preguntas para Test 1 - Fundamentos
        val preguntasTest1 = listOf(
            QuestionEntity(
                testId = test1Id,
                section = "Conceptos Básicos",
                questionText = "¿Cuál es el número ideal de jugadores para una partida de Mus?",
                options = "2 jugadores||4 jugadores (2 parejas)||6 jugadores||Cualquier número",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test1Id,
                section = "Conceptos Básicos",
                questionText = "¿Qué cartas se eliminan del mazo en el Mus?",
                options = "Ochos y nueves||Reyes y ases||Doses y treses||Todas las figuras",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test1Id,
                section = "Las Manos",
                questionText = "¿En qué orden se juegan las manos tradicionalmente?",
                options = "Grande, Chica, Pares, Juego||Pares, Juego, Grande, Chica||Juego, Grande, Chica, Pares||Chica, Grande, Pares, Juego",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test1Id,
                section = "Las Manos",
                questionText = "¿Qué es el 'Mus' en el juego?",
                options = "Cambiar cartas||Pedir punto||Retirarse||Ver las cartas del rival",
                correctIndex = 0
            )
        )

        // Preguntas para Test 2 - Señales
        val preguntasTest2 = listOf(
            QuestionEntity(
                testId = test2Id,
                section = "Señales Básicas",
                questionText = "¿Qué significa levantar las cejas en el Mus?",
                options = "Tengo buenas cartas||Quiero Mus||Tengo juego||Me retiro",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test2Id,
                section = "Señales Básicas",
                questionText = "¿Cómo se indica normalmente que se tienen 'pares'?",
                options = "Guiñar un ojo||Sacar la lengua||Fruncir los labios||Sonreír",
                correctIndex = 2
            ),
            QuestionEntity(
                testId = test2Id,
                section = "Señales Avanzadas",
                questionText = "¿Qué indica mover la cabeza hacia un lado?",
                options = "Tengo la Grande||Tengo la Chica||Tengo Juego||Tengo Pares",
                correctIndex = 3
            ),
            QuestionEntity(
                testId = test2Id,
                section = "Comunicación de Equipo",
                questionText = "¿Por qué es importante la comunicación con la pareja?",
                options = "Para coordinar estrategias||Para hacer trampa||Para divertirse más||Para jugar más rápido",
                correctIndex = 0
            )
        )

        // Preguntas para Test 3 - Estrategias
        val preguntasTest3 = listOf(
            QuestionEntity(
                testId = test3Id,
                section = "Estrategias Básicas",
                questionText = "¿Cuándo es recomendable pedir Mus?",
                options = "Siempre||Cuando tengas cartas bajas||Cuando tengas cartas altas||Nunca",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test3Id,
                section = "Estrategias Básicas",
                questionText = "¿Qué hacer si tienes 3 reyes y un 5?",
                options = "Mus completo||Mus de una carta||No pedir Mus||Retirarse",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test3Id,
                section = "Psicología del Juego",
                questionText = "¿Por qué es importante observar al rival?",
                options = "Para detectar sus señales||Para hacerle nervioso||Para divertirse||No es importante",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test3Id,
                section = "Gestión de Riesgos",
                questionText = "¿Cuándo deberías 'envidar' alto?",
                options = "Siempre en la primera mano||Cuando tengas una mano muy fuerte||Nunca||Solo cuando vayas perdiendo",
                correctIndex = 1
            )
        )

        // Preguntas para Test 4 - Puntuación
        val preguntasTest4 = listOf(
            QuestionEntity(
                testId = test4Id,
                section = "Cálculo de Puntos",
                questionText = "¿Cuánto vale un 'par' simple?",
                options = "1 punto||2 puntos||3 puntos||5 puntos",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test4Id,
                section = "Cálculo de Puntos",
                questionText = "¿Qué puntuación se necesita para ganar un juego?",
                options = "30 puntos||40 puntos||50 puntos||100 puntos",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test4Id,
                section = "Juego y Punto",
                questionText = "¿Qué es jugar 'al punto'?",
                options = "Cuando nadie tiene 31 o más||Cuando todos tienen juego||Cuando hay empate||Cuando se juega rápido",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test4Id,
                section = "Situaciones Especiales",
                questionText = "¿Qué pasa si dos jugadores tienen 31?",
                options = "Gana el que empezó||Empate||Se juega otra mano||Gana el que tenga mejores cartas",
                correctIndex = 1
            )
        )
        val preguntasTest5 = listOf(
            QuestionEntity(
                testId = test5Id,
                section = "Reglas Especiales",
                questionText = "¿Qué es 'ir de mus'?",
                options = "Pedir mus con malas cartas||No pedir mus con buenas cartas||Mus obligatorio||Jugar sin mus",
                correctIndex = 1
            ),
            QuestionEntity(
                testId = test5Id,
                section = "Reglas Especiales",
                questionText = "¿Qué pasa si todos los jugadores piden mus?",
                options = "Se reparten nuevas cartas||Se juega con las mismas||Se anula la mano||Se cambia de pareja",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test5Id,
                section = "Casos de Empate",
                questionText = "¿Cómo se resuelve un empate en la Grande?",
                options = "Gana el jugador más cercano al dador||Se reparte el punto||Se juega otra mano||Gana la pareja que empezó",
                correctIndex = 0
            )
        )
        val preguntasTest6 = listOf(
            QuestionEntity(
                testId = test6Id,
                section = "Historia",
                questionText = "¿De qué región de España es originario el Mus?",
                options = "País Vasco||Andalucía||Cataluña||Galicia",
                correctIndex = 0
            ),
            QuestionEntity(
                testId = test6Id,
                section = "Variantes",
                questionText = "¿En qué se diferencia el Mus 'a lo grande'?",
                options = "Se juega sin pares||Solo se juega grande y chica||Se usan 6 cartas||No hay mus",
                correctIndex = 1
            )
        )


        // Insertar preguntas
        testDao.insertQuestions(preguntasTest1)
        testDao.insertQuestions(preguntasTest2)
        testDao.insertQuestions(preguntasTest3)
        testDao.insertQuestions(preguntasTest4)
        testDao.insertQuestions(preguntasTest5)
        testDao.insertQuestions(preguntasTest6)

        // Verificar inserción
        val finalTests = testDao.getAllTests().first()
        val questionsCount1 = testDao.getQuestionsForTest(test1Id).first().size
        val questionsCount2 = testDao.getQuestionsForTest(test2Id).first().size

        println("DEBUG: Verificación - Tests: ${finalTests.size}, " +
                "Preguntas Test1: $questionsCount1, Preguntas Test2: " +
                "$questionsCount2")

    }
    // Función para guardar el resultado del test
    fun saveTestResult(testId: Int, score: Int, totalQuestions: Int) {
        viewModelScope.launch {
            val testResult = TestResultEntity(
                userId = currentUserId,
                testId = testId,
                score = score,
                maxScore = totalQuestions
            )
            testResultDao.insertTestResult(testResult)
            println("DEBUG: Resultado guardado - Test: $testId, " +
                    "Score: $score/$totalQuestions")
        }
    }

    // Función para obtener el historial de tests del usuario
    fun getTestResults() = testResultDao.getTestResultsByUser(currentUserId)

    // Función para obtener estadísticas
    suspend fun getTestStats(): Pair<Int, Double?> {
        val completedTests = testResultDao.getCompletedTestsCount(currentUserId)
        val averageScore = testResultDao.getAverageScore(currentUserId)
        return Pair(completedTests, averageScore)
    }
}
