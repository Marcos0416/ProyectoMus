package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.appcarnavalextraordinaria.Navigation.Bars

@Composable
fun FlujoPartidaMusScreen(navController: NavController) {

            Column(modifier = Modifier.fillMaxSize()) {
                // Header
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
                    Column {
                        Text(
                            text = "Flujo de Partida",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Desarrollo completo de una partida de Mus",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        GamePhaseCard(
                            phaseNumber = 1,
                            phaseTitle = "Reparto de Cartas",
                            content = {
                                Text(
                                    text = "El crupier reparte cuatro cartas a cada jugador. El juego se desarrolla en parejas, por lo que cada jugador también debe coordinarse con su compañero. Cuando quien reparte ha finalizado, debe dejar la baraja siempre a su derecha.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                                )
                            }
                        )
                    }

                    item {
                        GamePhaseCard(
                            phaseNumber = 2,
                            phaseTitle = "Ronda de Mus",
                            content = {
                                Text(
                                    text = "El jugador a la derecha del repartidor inicia. Decir \"Mus\" significa querer mejorar cartas descartándose de algunas. Si todos los jugadores piden Mus, se descartan y reparten nuevas cartas.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                                )
                            }
                        )
                    }

                    item {
                        Text(
                            text = "Fases del Juego",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    item {
                        PhaseDetailCard(
                            phaseNumber = "1",
                            phaseName = "Grande",
                            description = "Gana quien tenga la carta más alta. Reyes (3 y 12) son los más valiosos, seguidos de caballos, sotas, etc."
                        )
                    }

                    item {
                        PhaseDetailCard(
                            phaseNumber = "2",
                            phaseName = "Chica",
                            description = "Gana quien tenga la carta más baja (más 'pitos'). 1 y 2 son los más valiosos, luego 4, 5, 6, etc."
                        )
                    }

                    item {
                        PhaseDetailCard(
                            phaseNumber = "3",
                            phaseName = "Pares",
                            description = "Para quien tenga más parejas. Tipos: Parejas simples, Medias (tríos) y Duples (doble pareja). Jerarquía: Duples > Medias > Parejas."
                        )
                    }

                    item {
                        PhaseDetailCard(
                            phaseNumber = "4",
                            phaseName = "Juego",
                            description = "Quien tenga más de 30 puntos dice 'tengo juego'. 31 es el mejor, luego 32, 40, etc. Si nadie tiene juego, se juega al punto."
                        )
                    }

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

@Composable
fun GamePhaseCard(phaseNumber: Int, phaseTitle: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = phaseNumber.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = phaseTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun PhaseDetailCard(phaseNumber: String, phaseName: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = phaseNumber,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = phaseName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
                )
            }
        }
    }
}