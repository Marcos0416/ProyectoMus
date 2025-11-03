package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MazoCartasScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            LazyColumn(
                modifier = androidx.compose.ui.Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "El Mazo y las Cartas",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    Text(
                        text = "• El Mus utiliza un mazo español de 40 cartas.\n\n" +
                                "• Los palos son oros, copas, espadas y bastos.\n\n" +
                                "• Cada palo tiene números del 1 al 7 y las figuras sota, caballo y rey.\n\n" +
                                "• Los 3 y los 12 valen lo mismo, es decir son reyes los 3, es decir, valen 10, " +
                                "y luego los 2 y los 1 son lo mismo y cuentan 1 a la hora de contar el juego, " +
                                "en resumen los 3 y los 12 son la misma carta y los 2 y los 1 también son la misma carta.\n\n" +
                                "• A la hora de contar cartas, las cartas valen sus mismos puntos que la carta en sí " +
                                "menos las figuras que cuentan 10, es decir, tú a la hora de contar para jugar el juego " +
                                "necesitas contar cuántos puntos tienes en tu mano, por ejemplo, si tienes un 4, un 6, un 2 y una sota, " +
                                "tienes 21, porque el dos vale 1, el 4 vale 4, el 6 vale 6 y la sota pues vale 10 como todas las figuras.\n\n" +
                                "• Cada carta tiene un valor diferente según la apuesta.\n\n" +
                                "• Se reparten 4 cartas a cada jugador al inicio de la partida.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
                    )
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate("Tutoriales") {
                                popUpTo("tutoriales_main") { inclusive = true }
                            }
                        },
                        modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    ) {
                        Text(text = "Volver", textAlign = TextAlign.Center, modifier = androidx.compose.ui.Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

