package com.example.appcarnavalextraordinaria.Tutoriales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Composable que muestra la pantalla con el sistema de puntuación del Mus
@Composable
fun PuntuacionScreen(navController: NavController) {

    // Columna principal que ocupa la pantalla completa
    Column(modifier = Modifier.fillMaxSize()) {
        // Header con fondo en gradiente vertical azul primario y contenedor
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Contenedor para títulos del header
            Column {
                // Título principal grande, negrita y color blanco
                Text(
                    text = "Sistema de Puntuación",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Subtítulo con opacidad para suavizar color blanco
                Text(
                    text = "Cómo se contabilizan los puntos en el Mus",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Lista perezosa para presentación de las fases y explicaciones
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Explicación general del objetivo del juego
            item {
                Text(
                    text = "Objetivo del Juego",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Gana la pareja que primero alcance 40 puntos. Los puntos se consiguen mediante apuestas ganadas en cada ronda.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }

            // Tarjeta con explicación de la fase "Grande"
            item {
                ScoringPhaseCard(
                    phaseName = "Grande",
                    points = "1 punto",
                    description = "Se gana por tener la carta más alta. Reyes (3 y 12) son los más valiosos."
                )
            }

            // Tarjeta con explicación de la fase "Chica"
            item {
                ScoringPhaseCard(
                    phaseName = "Chica",
                    points = "1 punto",
                    description = "Se gana por tener la carta más baja. Ases (1 y 2) son los más valiosos."
                )
            }

            // Tarjeta con explicación de la fase "Pares" con los puntos variados
            item {
                ScoringPhaseCard(
                    phaseName = "Pares",
                    points = "Variable",
                    description = "• Parejas simples: 1 punto\n• Medias: 2 puntos\n• Duples: 3 puntos\nSe suman embites adicionales."
                )
            }

            // Tarjeta con explicación de la fase "Juego" con puntuaciones específicas
            item {
                ScoringPhaseCard(
                    phaseName = "Juego",
                    points = "Variable",
                    description = "• Juego normal (≥31): 2 puntos\n• 31 exacto: 3 puntos\n• Se suman embites adicionales.\nSi nadie tiene juego, se juega al punto."
                )
            }

            // Tarjeta explicativa especial para la apuesta "Órdago"
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "🎯 Órdago",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Apuesta total donde la pareja ganadora suma todos los puntos restantes para ganar la partida inmediatamente.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f),
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                        )
                    }
                }
            }

            // Botón para volver a la pantalla anterior (Tutoriales)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver a Tutoriales")
                }
            }
        }
    }
}

// Composable que muestra una tarjeta con el nombre de la fase, puntos asignados y descripción
@Composable
fun ScoringPhaseCard(phaseName: String, points: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Columna con el nombre de la fase y la descripción
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phaseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }
            // Espacio horizontal entre texto y recuadro de puntos
            Spacer(modifier = Modifier.width(16.dp))
            // Recuadro con puntos asignados a esta fase
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = points,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
