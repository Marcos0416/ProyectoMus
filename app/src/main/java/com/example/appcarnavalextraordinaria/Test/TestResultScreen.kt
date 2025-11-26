package com.example.appcarnavalextraordinaria.Test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestResultScreen(
    navController: NavController,   // Controlador de navegación para volver a pantallas anteriores
    score: Int,                     // Número de respuestas correctas del usuario
    totalQuestions: Int,            // Total de preguntas en el test
    testTitle: String = "Test",     // Nombre del test mostrado en la interfaz
    testId: Int                     // ID del test (útil si se quiere ampliar funcionalidades)
) {
    // Cálculo del porcentaje de aciertos
    val percentage = if (totalQuestions > 0) {
        (score.toFloat() / totalQuestions.toFloat()) * 100f
    } else {
        0f
    }

    // Selección de mensaje, título y colores según el porcentaje obtenido
    val (title, message, color, emoji) = when {
        percentage >= 90 -> Quadruple(
            "¡Excelente! ",
            "Dominas completamente este tema. ¡Eres un experto en Mus!",
            MaterialTheme.colorScheme.primary,
            ""
        )
        percentage >= 70 -> Quadruple(
            "¡Muy bien! ",
            "Tienes un buen conocimiento del Mus. Sigue practicando.",
            MaterialTheme.colorScheme.secondary,
            ""
        )
        percentage >= 50 -> Quadruple(
            "¡Bien hecho! ",
            "Vas por buen camino. Sigue aprendiendo para mejorar.",
            MaterialTheme.colorScheme.tertiary,
            ""
        )
        else -> Quadruple(
            "Sigue practicando ",
            "El Mus requiere práctica. No te rindas y vuelve a intentarlo.",
            MaterialTheme.colorScheme.error,
            ""
        )
    }

    // Estructura general de la pantalla
    Scaffold(
        topBar = {
            // Barra superior con botón de volver
            TopAppBar(
                title = { Text("Resultados - $testTitle") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Vuelve a la pantalla principal de tests, limpiando el back stack
                        navController.navigate("tests") {
                            popUpTo("tests") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver a tests")
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Tarjeta central donde se muestra el resultado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // (Opcional) Emoji según el resultado, si se usa
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título principal según el porcentaje (Excelente, Muy bien, etc.)
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Muestra el título del test
                    Text(
                        text = testTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Indicador circular con puntuación y porcentaje
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = color.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            // Puntuación en formato "X/Y"
                            Text(
                                text = "$score/$totalQuestions",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                            // Porcentaje
                            Text(
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = color
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Mensaje adaptado al rendimiento del usuario
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Indicador informativo de que la puntuación ha sido guardada
                    Text(
                        text = " Resultado guardado en tu historial",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botones inferiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botón para volver a la lista de tests
                OutlinedButton(
                    onClick = { navController.navigate("tests") },
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Text("Más Tests")
                }
            }
        }
    }
}

// Clase auxiliar para devolver cuatro valores en un when
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
