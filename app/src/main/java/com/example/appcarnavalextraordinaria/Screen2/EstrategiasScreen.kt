package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Insights
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
fun EstrategiasScreen(navController: NavController) {

         
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Header con gradiente
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
                            text = "Estrategias Avanzadas",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tácticas para dominar el juego",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Contenido
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
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

@Composable
fun StrategyPoint(number: Int, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Número con círculo
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
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
        }
    }
}