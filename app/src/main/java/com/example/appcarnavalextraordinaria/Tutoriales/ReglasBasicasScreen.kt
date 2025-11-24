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

// Pantalla que muestra las reglas básicas del juego Mus
@Composable
fun ReglasBasicasScreen(navController: NavController) {
    // Columna principal que ocupa todo el tamaño disponible
    Column(modifier = Modifier.fillMaxSize()) {
        // Header con gradiente vertical usando colores primarios del tema
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
            // Columna para los textos de título y subtítulo
            Column {
                // Texto principal con estilo headline, negrita y color blanco
                Text(
                    text = "Reglas Básicas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Subtítulo con opacidad para suavizar el color blanco
                Text(
                    text = "Fundamentos esenciales del Mus",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Lista con scroll vertical para las reglas, con padding y espacio entre items
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cada 'item' es una regla representada por RuleCard con número y texto
            item {
                RuleCard(
                    ruleNumber = 1,
                    rule = "El Mus se juega normalmente con 4 jugadores organizados en 2 parejas."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 2,
                    rule = "Cada jugador recibe 4 cartas del mazo español de 40 cartas (sin 8s y 9s)."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 3,
                    rule = "El objetivo es ganar bazas y sumar puntos para llegar primero a 40 puntos."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 4,
                    rule = "Las apuestas se realizan en orden específico: Grande → Chica → Pares → Juego."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 5,
                    rule = "Existe un sistema de señales secretas para comunicar información con tu compañero."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 6,
                    rule = "El 'Mus' permite descartar y recibir nuevas cartas para mejorar la mano."
                )
            }
            item {
                RuleCard(
                    ruleNumber = 7,
                    rule = "El 'Órdago' es una apuesta total que puede decidir la partida inmediatamente."
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

// Composable para mostrar una regla con su número en círculo y texto
@Composable
fun RuleCard(ruleNumber: Int, rule: String) {
    // Tarjeta con elevación y color de fondo de la superficie del tema
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        // Fila con padding para contener el número y el texto
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Número dentro de un círculo con fondo primario y texto en blanco
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
                    text = ruleNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            // Espacio horizontal entre número y texto
            Spacer(modifier = Modifier.width(16.dp))
            // Texto que representa la regla con estilo de cuerpo y color del tema
            Text(
                text = rule,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
            )
        }
    }
}
