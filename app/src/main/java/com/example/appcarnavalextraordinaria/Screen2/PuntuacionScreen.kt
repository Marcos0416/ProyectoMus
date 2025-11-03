package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
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
fun PuntuacionScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Sistema de Puntuación en Mus",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    Text(
                        text = "En Mus, se juega generalmente hasta alcanzar 40 puntos. La pareja que primero llegue a 40 puntos gana la partida. Los puntos se consiguen en función de las apuestas ganadas en cada ronda del juego:\n\n" +
                                "• Grande: Se gana un punto por ganar la ronda de cartas más altas.\n" +
                                "\n\n• Chica: Se gana un punto por ganar la ronda de cartas más bajas.\n" +
                                "\n\n• Pares: Se ganan puntos dependiendo del tipo de pares (simples, medias, duples)." +
                                "Aqui por ejemplo si ganas esa ronda y tienes solo parejas se cuenta 1 por par de compañero," +
                                "si tienes medias si cuentan 2 por medias de  cada compañero y si tienes duples" +
                                "se cuentan 3 por cada duples de compañero y si ha habido un embite" +
                                "pues se puntua tambien dicho embite\n" +
                                "\n\n• Juego: Se gana un punto a la pareja con el mejor conteo de puntos en la mano, si alguien tiene 31 o más." +
                                "Es decir,ganas esa ronda se cuenta si tienes juego normal 2 y si tienes" +
                                "31 de juego se cuenta 3 por cada compañero y el embite que hubiera \n\n" +
                                "Además, si se acepta el \"órdago\", se apuesta la partida entera y la pareja ganadora suma todos los puntos restantes para ganar la partida.\n\n" +
                                "Si una pareja gana una apuesta, suma las piedras correspondientes. Si la otra pareja no acepta la apuesta (dice \"paso\"), quien apostó suma las piedras mínimas de esa ronda.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
                    )
                }
                item {
                    Button(
                        onClick = { navController.navigate("Tutoriales") {
                            popUpTo("tutoriales_main") { inclusive = true }
                        } },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Volver a Tutoriales",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
