package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Navigation.Bars

@Composable
fun TutorialesScreen(navController: NavController) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                // Header con imagen de fondo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        ),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Tutoriales de Mus",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Domina el arte del Mus español",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                // Lista de tutoriales
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Selecciona un tema:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                        )
                    }

                    item {
                        TutorialCard(
                            title = "Reglas Básicas",
                            description = "Fundamentos esenciales del juego",
                            icon = Icons.Default.Rule,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate("Reglas") }
                        )
                    }

                    item {
                        TutorialCard(
                            title = "El Mazo y las Cartas",
                            description = "Conoce las cartas y sus valores",
                            icon = Icons.Default.Deck,
                            color = MaterialTheme.colorScheme.secondary,
                            onClick = { navController.navigate("Mazo") }
                        )
                    }

                    item {
                        TutorialCard(
                            title = "Estrategias Iniciales",
                            description = "Tácticas para comenzar con ventaja",
                            icon = Icons.Default.Insights,
                            color = MaterialTheme.colorScheme.tertiary,
                            onClick = { navController.navigate("Estrategias") }
                        )
                    }

                    item {
                        TutorialCard(
                            title = "Flujo de Partida",
                            description = "Desarrollo completo de una partida",
                            icon = Icons.Default.PlayCircle,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { navController.navigate("Funcionamiento") }
                        )
                    }

                    item {
                        TutorialCard(
                            title = "Sistema de Puntuación",
                            description = "Cómo se contabilizan los puntos",
                            icon = Icons.Default.Score,
                            color = MaterialTheme.colorScheme.secondary,
                            onClick = { navController.navigate("Puntuacion") }
                        )
                    }



                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Volver al Menú Principal")
                        }
                    }
                }
            }

}

@Composable
fun TutorialCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color = color.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ver más",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}