package com.example.appcarnavalextraordinaria.Test

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Data.TestDao
import com.example.appcarnavalextraordinaria.Data.TestEntity
import com.example.appcarnavalextraordinaria.Data.TestResultDao
import com.example.appcarnavalextraordinaria.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestsListScreen(
    navController: NavController,
    testDao: TestDao,
    testResultDao: TestResultDao,
    currentUserId: Int,
) {
    val context = LocalContext.current

    // ViewModel con factory manual para pasar los DAOs
    val viewModel: TestsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TestsViewModel(testDao, testResultDao, currentUserId) as T
        }
    })

    // Estado de los tests
    val tests by viewModel.tests.collectAsState()
    val isCreatingTests by viewModel.isCreatingTests.collectAsState()

    // Genera tests por defecto si es la primera vez
    LaunchedEffect(Unit) {
        viewModel.createSampleTestsIfFirstLaunch(context)
    }

    // Colores usados en la pantalla
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mus Educativo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    // Botón para volver a la pantalla principal
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {

                // Tarjeta informativa del header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            " Pon a prueba tus conocimientos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Completa los tests y mejora tu juego",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = onSurfaceColor.copy(alpha = 0.8f)
                        )
                    }
                }

                // Diferentes estados de la pantalla
                when {

                    // Estado cargando tests por primera vez
                    isCreatingTests -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = primaryColor,
                                    strokeWidth = 4.dp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Preparando tests...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = onSurfaceColor
                                )
                            }
                        }
                    }

                    // No hay tests guardados en BDD
                    tests.isEmpty() -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.test),
                                contentDescription = "Sin tests",
                                modifier = Modifier.size(80.dp),
                                tint = onSurfaceColor.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No hay tests disponibles",
                                style = MaterialTheme.typography.titleMedium,
                                color = onSurfaceColor
                            )
                        }
                    }

                    // Lista de tests disponible
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tests) { test ->
                                // Card por cada test
                                TestCard(
                                    test = test,
                                    onClick = { navController.navigate("testDetail/${test.id}") }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestCard(test: TestEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // Clickable personalizado sin ripple extra
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            // Selección de icono y color según el título del test
            val (icon, color) = when {
                test.title.contains("Básico", ignoreCase = true) -> Pair("📚", MaterialTheme.colorScheme.primary)
                test.title.contains("Señales", ignoreCase = true) -> Pair("👁️", MaterialTheme.colorScheme.secondary)
                test.title.contains("Estrategia", ignoreCase = true) -> Pair("🧠", MaterialTheme.colorScheme.tertiary)
                else -> Pair("✅", MaterialTheme.colorScheme.primary)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono del test
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Título del test
                    Text(
                        text = test.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )

                    // Descripción opcional
                    test.description?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                // Flecha de navegación
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Comenzar test",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Etiqueta de dificultad
            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                val difficulty = when {
                    test.title.contains("Básico", ignoreCase = true) -> "Principiante"
                    test.title.contains("Señales", ignoreCase = true) -> "Intermedio"
                    test.title.contains("Estrategia", ignoreCase = true) -> "Avanzado"
                    else -> "General"
                }

                Box(
                    modifier = Modifier
                        .background(
                            color = color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = difficulty,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
