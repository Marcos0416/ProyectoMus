package com.example.appcarnavalextraordinaria.Tutoriales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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

// Composable que muestra la pantalla de Estrategias avanzadas
@Composable
fun EstrategiasScreen(navController: NavController) {

    // Columna principal que ocupa todo el espacio disponible y usa color de fondo del tema
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header con fondo en gradiente vertical usando colores primarios del tema
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
            // Columna para textos del header
            Column {
                // Título principal del header con estilo grande, negrita y color blanco
                Text(
                    text = "Estrategias Avanzadas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Subtítulo con opacidad para suavizar color blanco
                Text(
                    text = "Tácticas para dominar el juego",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Contenido principal en LazyColumn para scroll, con padding y separación vertical entre ítems
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cada item es una llamada a StrategyPoint con número, título y descripción de la estrategia
            item {
                StrategyPoint(
                    number = 1,
                    title = "Observación Constante",
                    description = "Observa siempre las señales de tu compañero para coordinarlas con tu juego y maximizar las oportunidades."
                )
            }

            item {
                StrategyPoint(
                    number = 2,
                    title = "Evaluación Precisa",
                    description = "Analiza cuidadosamente la fuerza de tu mano antes de realizar apuestas grandes o arriesgadas."
                )
            }

            item {
                StrategyPoint(
                    number = 3,
                    title = "Juego Psicológico",
                    description = "Utiliza apuestas estratégicas para confundir a tus oponentes y controlar el ritmo del juego."
                )
            }

            item {
                StrategyPoint(
                    number = 4,
                    title = "Práctica Continua",
                    description = "La coordinación con tu compañero mejora significativamente con la práctica constante y la comunicación."
                )
            }

            item {
                StrategyPoint(
                    number = 5,
                    title = "Atención Total",
                    description = "Mantén máxima atención a los gestos y movimientos de todos los jugadores durante toda la partida."
                )
            }

            // Botón para volver a la pantalla anterior (Tutoriales)
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Volver a Tutoriales", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Composable para mostrar cada punto de estrategia con número, título y descripción
@Composable
fun StrategyPoint(number: Int, title: String, description: String) {
    // Tarjeta con elevación leve y color de fondo de la superficie del tema
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // Fila con padding, con alineación vertical al inicio (arriba)
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Número dentro de un círculo con fondo primario y texto blanco
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Espacio horizontal entre número y contenido textual
            Spacer(modifier = Modifier.width(16.dp))

            // Columna ocupando el resto del espacio con título y descripción
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Título con estilo medio y color primario
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Descripción con estilo de cuerpo y color de texto para superficie
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }
        }
    }
}
