package com.example.appcarnavalextraordinaria.Screen1

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SignalWifi4Bar
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Data.PartidaDao
import com.example.appcarnavalextraordinaria.Data.PartidaEntity
import com.example.appcarnavalextraordinaria.Login.UserViewModel
import com.example.appcarnavalextraordinaria.Navigation.Bars
import com.example.appcarnavalextraordinaria.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    partidaDao: PartidaDao,
    currentUserId: Int,
    currentUsername: String,
    context: Context
) {
    val loggedUsername = userViewModel.getSession(context)
    val partidasList = remember { mutableStateListOf<PartidaEntity>() }

    // Cargar partidas del usuario
    LaunchedEffect(currentUserId) {
        partidaDao.getPartidasByUser(currentUserId).collect { list ->
            partidasList.clear()
            partidasList.addAll(list)
        }
    }

    // Colores personalizados
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = Color(0xFF0f3460) // Fondo azul oscuro elegante
        ) {
            Scaffold(
                containerColor = Color.Transparent
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // HEADER SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF16213e),
                                            Color(0xFF0f3460)
                                        )
                                    )
                                )
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // User Info Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Welcome Text
                                Column {
                                    Text(
                                        text = "¡Bienvenido!",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        text = loggedUsername ?: "Usuario",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                // User Avatar with Logout
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .shadow(8.dp, CircleShape)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        Color(0xFF667eea),
                                                        Color(0xFF764ba2)
                                                    )
                                                ),
                                                shape = CircleShape
                                            )
                                            .border(
                                                2.dp,
                                                Color.White.copy(alpha = 0.3f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = (loggedUsername?.first()?.uppercase() ?: "U"),
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Logout Button
                                    IconButton(
                                        onClick = {
                                            userViewModel.logout(context)
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                Color.Red.copy(alpha = 0.2f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ExitToApp,
                                            contentDescription = "Cerrar sesión",
                                            tint = Color.Red,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Main Logo and Title
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .shadow(16.dp, CircleShape)
                                    .background(Color.White, CircleShape)
                                    .border(2.dp, primaryColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.imagenmustfg),
                                    contentDescription = "Logo Mus",
                                    modifier = Modifier.size(100.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "MUS EDUCATIVO",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Domina el arte del Mus vasco\nAprende, practica y mejora",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    value = partidasList.size.toString(),
                                    label = "Partidas",
                                    icon = Icons.Default.PlayArrow
                                )
                                StatItem(
                                    value = partidasList.count { it.resultado.contains("Pareja 1") }.toString(),
                                    label = "Victorias",
                                    icon = Icons.Default.Star
                                )
                                StatItem(
                                    value = if (partidasList.isNotEmpty()) "Activo" else "Nuevo",
                                    label = "Estado",
                                    icon = Icons.Default.TrendingUp
                                )
                            }
                        }
                    }

                    // MAIN MENU SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Explorar Contenido",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                            )

                            // Feature Cards
                            FeatureCard(
                                title = "Tutoriales Interactivos",
                                description = "Aprende desde cero con guías paso a paso",
                                icon = Icons.Default.School,
                                iconColor = Color(0xFF4CAF50),
                                gradientColors = listOf(Color(0xFF4CAF50), Color(0xFF45a049)),
                                onClick = { navController.navigate("Tutoriales") }
                            )

                            FeatureCard(
                                title = "Diccionario de Señales",
                                description = "Domina el lenguaje no verbal del Mus",
                                icon = Icons.Default.SignalWifi4Bar,
                                iconColor = Color(0xFF2196F3),
                                gradientColors = listOf(Color(0xFF2196F3), Color(0xFF1976D2)),
                                onClick = { navController.navigate("Senales") }
                            )

                            FeatureCard(
                                title = "Partidas Interactivas",
                                description = "Practica contra bots inteligentes",
                                icon = Icons.Default.SportsEsports,
                                iconColor = Color(0xFFFF9800),
                                gradientColors = listOf(Color(0xFFFF9800), Color(0xFFF57C00)),
                                onClick = {
                                    if (loggedUsername.isNullOrEmpty()) {
                                        // Si no está logueado, navegar al login
                                        navController.navigate("login") {
                                            // Opcional: limpiar el back stack
                                            popUpTo("main") { saveState = true }
                                        }
                                    } else {
                                        // Si está logueado, navegar a la partida
                                        navController.navigate("Partida")
                                    }
                                }
                            )

                            FeatureCard(
                                title = "Tests de Conocimiento",
                                description = "Pon a prueba lo aprendido",
                                icon = Icons.Default.Quiz,
                                iconColor = Color(0xFF9C27B0),
                                gradientColors = listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2)),
                                onClick = { navController.navigate("tests") }
                            )
                        }
                    }

                    // PARTIDAS SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Historial de Partidas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                            )

                            if (partidasList.isNotEmpty()) {
                                // Resumen de partidas
                                PartidasSummaryCard(partidasList = partidasList)

                                Spacer(modifier = Modifier.height(16.dp))

                                // Partidas recientes
                                Text(
                                    text = "Partidas Recientes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    if (partidasList.isNotEmpty()) {
                        items(partidasList.take(3)) { partida ->
                            PartidaItem(
                                partida = partida,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        item {
                            EmptyPartidasCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                onJugarClick = { navController.navigate("Partida") }
                            )
                        }
                    }

                    // BOTTOM SPACER
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        brush = Brush.horizontalGradient(colors = gradientColors),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
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
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
            }

            // Arrow Indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Ir",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun PartidasSummaryCard(partidasList: List<PartidaEntity>) {
    val totalPartidas = partidasList.size
    val partidasGanadas = partidasList.count { it.resultado.contains("Pareja 1") }
    val partidasPerdidas = partidasList.count { it.resultado.contains("Pareja 2") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resumen de Partidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Partidas",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PartidaStat(
                    value = totalPartidas.toString(),
                    label = "Total",
                    color = MaterialTheme.colorScheme.primary
                )
                PartidaStat(
                    value = partidasGanadas.toString(),
                    label = "Victorias",
                    color = Color(0xFF4CAF50)
                )
                PartidaStat(
                    value = partidasPerdidas.toString(),
                    label = "Derrotas",
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
private fun PartidaStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PartidaItem(partida: PartidaEntity, modifier: Modifier = Modifier) {
    val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(partida.fecha))

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según el resultado
            val (icono, color) = if (partida.resultado.contains("Pareja 1")) {
                Pair(Icons.Default.Star, Color(0xFF4CAF50)) // Ganada
            } else {
                Pair(Icons.Default.SportsEsports, Color(0xFFF44336)) // Perdida
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = "Resultado",
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = partida.resultado,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Fecha: $fechaFormateada",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun EmptyPartidasCard(
    modifier: Modifier = Modifier,
    onJugarClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Comenzar",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Aún no has jugado partidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Comienza tu primera partida para ver tu historial aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = LocalIndication.current,
                        onClick = { onJugarClick() }
                    ),

                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Jugar Primera Partida",
                    modifier = Modifier.padding(vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}