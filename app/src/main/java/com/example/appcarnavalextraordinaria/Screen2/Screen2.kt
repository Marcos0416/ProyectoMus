package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Navigation.Bars
import com.example.appcarnavalextraordinaria.R
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme

// Pantalla índice de tutoriales
@Composable
fun TutorialesScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                item {
                    Text(
                        text = "Tutoriales de Mus",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 16.dp)
                    )
                }
                item {
                    CardMenuItem(
                        title = "Reglas Básicas",
                        description = "Aprende las reglas básicas del Mus.",
                        imageRes = R.drawable.reglas,
                        onClick = { navController.navigate("Reglas") }
                    )
                }
                item {
                    CardMenuItem(
                        title = "El Mazo y las Cartas",
                        description = "Conoce las cartas usadas en el juego.",
                        imageRes = R.drawable.mazo,
                        onClick = { navController.navigate("Mazo") }
                    )
                }

                item {
                    CardMenuItem(
                        title = "Estrategias Iniciales",
                        description = "Consejos para comenzar a jugar mejor.",
                        imageRes = R.drawable.estrategias,
                        onClick = { navController.navigate("Estrategias") }
                    )
                }
                item {
                    CardMenuItem(
                        title = "Funcionamiento basico de una partida",
                        description = "Consejos para comenzar a jugar mejor.",
                        imageRes = R.drawable.partidasmus,
                        onClick = { navController.navigate("Funcionamiento") }
                    )
                }
                item {
                    CardMenuItem(
                        title = "Puntuaciones",
                        description = "Consejos para puntuar",
                        imageRes = R.drawable.senas,
                        onClick = { navController.navigate("Puntuacion") }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            navController.navigate("main") {
                                popUpTo("tutoriales_main") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Volver",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun CardMenuItem(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            )
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicacionOrdinariaInterfacesTheme  {
        TutorialesScreen(navController = rememberNavController())
    }
}




