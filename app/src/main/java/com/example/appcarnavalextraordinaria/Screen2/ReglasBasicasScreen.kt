package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Navigation.Bars
import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReglasBasicasScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = androidx.compose.ui.Modifier.padding(24.dp)) {
                Text(
                    text = "Reglas Básicas del Mus",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                    text = "• El Mus se juega normalmente con 4 jugadores en parejas.\n\n" +
                            "• Cada jugador recibe 4 cartas del mazo español de 40 cartas.\n\n" +
                            "• El objetivo es ganar bazas y sumar puntos para llegar a 40.\n\n" +
                            "• Las apuestas se hacen en órdenes: Grande, chica, pares y juego.\n\n" +
                            "• Existen señales secretas para comunicar la fuerza de las cartas con tu compañero.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )
                Spacer(modifier =androidx.compose.ui.Modifier.height(8.dp))
                Button(
                    onClick = { navController.navigate("Tutoriales") {
                        popUpTo("tutoriales_main") { inclusive = true }
                    } },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Volver ", textAlign = TextAlign.Center, modifier = androidx.compose.ui.Modifier.fillMaxWidth())
                }
            }
        }
    }
}
