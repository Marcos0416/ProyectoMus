package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Navigation.Bars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlujoPartidaMusScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Flujo de una partida básica de Mus",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                item {
                    Text(
                        text =
                        "• Reparto de cartas:\n" +
                                "El crupier reparte cuatro cartas a cada jugador. El juego se desarrolla en parejas, por lo que cada jugador también debe coordinarse con su compañero. Cuando quien reparte ha finalizado, debe dejar la baraja siempre a su derecha. Esto es muy importante porque a la hora de hablar primero también lo es. Una vez que quien reparte ha acabado y deja el mazo a su derecha, debe empezar a hablar quien tiene la baraja a su izquierda, es decir, la persona que está a la derecha de quien repartió.\n\n" +
                                "Ella tiene dos opciones: empezar a jugar en Grande y decir \"Paso\", \"Envido\" o \"Mus\".\n\n" +
                                "¿Y qué significa decir \"Mus\"?\n" +
                                "Decir \"Mus\" significa que quiere mejorar sus cartas y descartarse de algunas. Su compañero también debe decirlo si quiere. Si no quiere, dice \"Corto\" y se empieza a jugar. Si ambos dicen \"Mus\", la otra pareja habla y decide igual. Si la otra pareja también dice \"Mus\", se tiran al medio las cartas que no quieren (se pueden tirar de 1 a 4 cartas, pero hay que tirar al menos una). Después, el crupier reparte las cartas que faltan y comienza otra vez la ronda de hablar, siguiendo este flujo hasta que alguien dice \"Corto\".\n\n" +
                                "Si no dicen mus, se pasa a jugar.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
                    )
                }
                item {
                    Text(
                        text = "Fases del juego:",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Text(
                        text = "-1 Grande:\n" +
                                "Habla sobre quién tiene la carta más alta. Comienza quien tiene la baraja a su izquierda y tiene dos opciones: decir \"Paso\" o \"Envido\" (apostar piedras). Se pueden subir las apuestas y responder \"Paso\" o \"Lo veo\". Existe la apuesta de \"Órdago\", que supone apostar toda la partida.\n" +
                                "En la ronda de grande, los reyes (los 3 y los 12) son los más importantes, luego los caballos, luego las sotas y así sucesivamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item {
                    Text(
                        text = "-2 Pequeña:\n" +
                                "Gana el que tenga la carta más baja (más \"pitos\"), quien tenga más 1 y 2 en la mano, luego los 4, 5, 6 y así sucesivamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item {
                    Text(
                        text = "-3 Pares:\n" +
                                "La ronda de pares es para quien tenga más parejas. Hay tres tipos:\n" +
                                "  • Parejas simples: dos cartas iguales.\n" +
                                "  • Medias: como los tríos en el póker (3 iguales).\n" +
                                "  • Duples: dobles parejas (dos pares) en la misma mano.\n" +
                                "La jerarquía es: duples, medias, luego parejas simples.\n" +
                                "Primero habla quien tiene el mazo a la izquierda declarando si tiene pares, y todos siguen en sentido contrario a las agujas del reloj. Solo juegan quienes tengan pares.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item {
                    Text(
                        text = "-4 Juego:\n" +
                                "En la ronda de juego cuentas los puntos de tus cartas si tienes \"juego\" (más de 30 puntos, no incluido). Por ejemplo, cuentas los valores y si suman más de 30 dices \"tengo juego\". Si nadie tiene juego, se juega a \"punto\" donde gana quien tenga la suma más cercana a 30 por debajo. El 31 es el mejor juego, seguido de 32, luego 40 y luego va bajando hasta 33.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item {
                    Button(
                        onClick = {
                            navController.navigate("Tutoriales") {
                                popUpTo("tutoriales_main") { inclusive = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Volver",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

