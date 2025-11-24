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
    navController: NavController,
    score: Int,
    totalQuestions: Int,
    testTitle: String = "Test",
    testId: Int  // ← AÑADE ESTE PARÁMETRO
) {
    val percentage = if (totalQuestions > 0) {
        (score.toFloat() / totalQuestions.toFloat()) * 100f
    } else {
        0f
    }

    // Determinar resultado y colores
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resultados - $testTitle") },  // ← MUESTRA EL TÍTULO DEL TEST
                navigationIcon = {
                    IconButton(onClick = {
                        // Navegar al inicio de tests en lugar de solo back
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
            // Tarjeta de resultados principal
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
                    // Emoji y título
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.displayLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Información del test
                    Text(
                        text = testTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Puntuación circular
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
                            Text(
                                text = "$score/$totalQuestions",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                            Text(
                                text = "${percentage.toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = color
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Mensaje personalizado
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))



                    Spacer(modifier = Modifier.height(16.dp))

                    // Información de guardado
                    Text(
                        text = " Resultado guardado en tu historial",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

// Helper class para el quadruple
data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)