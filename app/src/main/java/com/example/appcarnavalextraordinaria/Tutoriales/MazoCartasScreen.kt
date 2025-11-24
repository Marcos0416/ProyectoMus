package com.example.appcarnavalextraordinaria.Tutoriales

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// Pantalla que muestra información sobre el mazo y las cartas en Mus
@Composable
fun MazoCartasScreen(navController: NavController) {

    // Columna principal con todo el contenido que ocupa la pantalla completa
    Column(modifier = Modifier.fillMaxSize()) {
        // Header con fondo en gradiente vertical usando colores principales del tema
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
            // Columna para contener los textos del encabezado
            Column {
                // Título grande y en negrita, color blanco
                Text(
                    text = "Mazo y Cartas",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                // Subtítulo con opacidad para suavizar color blanco
                Text(
                    text = "Conoce las herramientas del juego",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Lista perezosa con padding para contenido con separación vertical
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información: Mazo Español con icono de Deck
            item {
                InfoCard(
                    icon = Icons.Default.Deck,
                    title = "Mazo Español",
                    content = "El Mus utiliza un mazo español de 40 cartas con cuatro palos: oros, copas, espadas y bastos."
                )
            }
            // Información: Composición de palos y figuras
            item {
                InfoCard(
                    icon = Icons.Default.FormatListNumbered,
                    title = "Composición",
                    content = "Cada palo contiene números del 1 al 7 y las figuras: sota, caballo y rey."
                )
            }
            // Información: Valores especiales con icono Casino
            item {
                InfoCard(
                    icon = Icons.Default.Casino,
                    title = "Valores Especiales",
                    content = "Los 3 y 12 son reyes (valen 10). Los 1 y 2 son ases (valen 1). Las figuras valen 10 puntos."
                )
            }
            // Tarjeta con ejemplo de cálculo de puntos para una mano
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "📊 Ejemplo de Cálculo",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Mano: 4, 6, 2, Sota\n" +
                                    "Cálculo: 4 + 6 + 1 + 10 = 21 puntos\n\n" +
                                    "• 2 vale 1 punto\n" +
                                    "• 4 vale 4 puntos\n" +
                                    "• 6 vale 6 puntos\n" +
                                    "• Sota vale 10 puntos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                        )
                    }
                }
            }
            // Información: Reparto inicial de cartas con icono Style
            item {
                InfoCard(
                    icon = Icons.Default.Style,
                    title = "Reparto Inicial",
                    content = "Cada jugador recibe 4 cartas al inicio de la partida. El valor de cada carta varía según la apuesta en curso."
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

// Tarjeta genérica para mostrar icono, título y contenido textual
@Composable
fun InfoCard(icon: ImageVector, title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono con color primario y tamaño fijo
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            // Columna para título y descripción expandiendo el ancho disponible
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
                )
            }
        }
    }
}
