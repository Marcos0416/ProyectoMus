package com.example.appcarnavalextraordinaria.Test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Data.TestDao
import com.example.appcarnavalextraordinaria.Data.TestResultDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestDetailScreen(
    testId: Int,                       // ID del test seleccionado
    testDao: TestDao,                  // DAO para obtener preguntas del test
    testResultDao: TestResultDao,      // DAO para guardar los resultados
    navController: NavController,      // Controlador de navegación
    currentUserId: Int,                // ID del usuario actual
    testTitle: String = "Test"         // Título del test
) {
    // ViewModel con una factory personalizada para pasar DAOs y userId
    val viewModel: TestsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TestsViewModel(testDao, testResultDao, currentUserId) as T
        }
    })

    // Flujo de preguntas del test
    val questionsFlow = remember { testDao.getQuestionsForTest(testId) }
    val questions by questionsFlow.collectAsState(initial = emptyList())

    // Estado de la UI
    var currentIndex by remember { mutableStateOf(0) }             // Pregunta actual
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }  // Respuesta seleccionada
    var showResults by remember { mutableStateOf(false) }          // Mostrar resultados
    var userAnswers by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) } // Respuestas del usuario
    var hasSavedResult by remember { mutableStateOf(false) }       // Evitar guardar doble

    val currentQuestion = questions.getOrNull(currentIndex)
    val totalQuestions = questions.size

    // Cuando cambia la pregunta, cargar selección previa si existe
    LaunchedEffect(currentIndex) {
        selectedAnswer = userAnswers[currentIndex]
    }

    // Guardar resultados cuando se llega al final
    LaunchedEffect(showResults) {
        if (showResults && !hasSavedResult) {
            val score = questions.indices.count { index ->
                userAnswers[index] == questions[index].correctIndex
            }
            viewModel.saveTestResult(testId, score, totalQuestions)
            hasSavedResult = true
        }
    }

    // Si ya finalizó el test, mostrar pantalla de resultado
    if (showResults) {
        val score = questions.indices.count { index ->
            userAnswers[index] == questions[index].correctIndex
        }

        TestResultScreen(
            navController = navController,
            score = score,
            totalQuestions = totalQuestions,
            testTitle = testTitle,
            testId = testId
        )
        return
    }

    // Si todavía no cargaron las preguntas
    if (questions.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Cargando preguntas...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
        return
    }

    val question = currentQuestion ?: return

    // Estructura principal con top bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        testTitle,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Barra de progreso del test
            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / totalQuestions.toFloat() },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = MaterialTheme.colorScheme.primary,
            )

            // Encabezado con número de pregunta y sección
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pregunta ${currentIndex + 1} de $totalQuestions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Etiqueta de sección de la pregunta
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = question.section,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Tarjeta principal que muestra pregunta y opciones
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp)
                ) {
                    // Texto de la pregunta
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Muestra cada opción como tarjeta clicable
                    val options = question.options.split("||")
                    options.forEachIndexed { index, option ->

                        val isSelected = selectedAnswer == index

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = LocalIndication.current,
                                    onClick = {
                                        selectedAnswer = index
                                        userAnswers = userAnswers + (currentIndex to index)
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border =
                            if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            else null
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // RadioButton como selector visual
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedAnswer = index
                                        userAnswers = userAnswers + (currentIndex to index)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))

                                // Texto de la opción
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // Botones de navegación inferior
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Botón "Anterior"
                OutlinedButton(
                    onClick = {
                        if (currentIndex > 0) {
                            currentIndex--
                        }
                    },
                    enabled = currentIndex > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Anterior")
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botón "Siguiente" o "Finalizar"
                Button(
                    onClick = {
                        if (currentIndex < totalQuestions - 1) {
                            currentIndex++
                        } else {
                            showResults = true
                        }
                    },
                    enabled = selectedAnswer != null,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (currentIndex < totalQuestions - 1) "Siguiente" else "Finalizar",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
