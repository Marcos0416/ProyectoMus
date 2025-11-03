package com.example.appcarnavalextraordinaria.Screen3


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Navigation.Bars
import com.example.appcarnavalextraordinaria.R
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme
import com.google.android.gms.maps.model.Gap
import java.time.temporal.ValueRange
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SenalesScreen(navController: NavController) {
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
                        text = "Señales en Mus",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                item {
                    Text(
                        text = "En el juego de Mus, las señales son gestos discretos que permiten a los jugadores comunicar la fuerza o características de sus cartas a sus compañeros. Conocerlas es fundamental para coordinar jugadas y apuestas efectivamente.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 24.sp
                    )
                }
                item {
                    Text(
                        text = "Lista de señales comunes:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                val señales = listOf(
                    "👂 Tocarse la oreja: Indica que tiene solomillo (31 con 3 reyes).",
                    "😬 Morderse la parte derecha inferior de la boca: Indica 3 reyes.",
                    "😬 Morderse la boca: Indica 2 reyes.",
                    "😛 Sacar la lengua hacia un lado: Indica 3 pitos (2 y 1).",
                    "😛 Sacar la lengua: Indica 2 pitos (2 y 1).",
                    "😉 Torcer la boca hacia un lado: Indica que tiene medias (de lo que sea).",
                    "😉 Levantar las cejas: Indica que tiene duples (de lo que sea).",
                    "😉 Guiñar un ojo: Indica que tiene 31 en el juego.",
                    "😌 Cerrar los ojos: Indica que no tiene nada.",
                    "🤷 Levantar los hombros: Indica que tiene 30 al punto."
                )
                items(señales) { señal ->
                    Text(
                        text = señal,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                item {
                    Text(
                        text = "Importante:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                    )
                }
                item {
                    Text(
                        text = "El uso de señales debe ser discreto para no ser detectado por los rivales. Su dominio y comprensión mejoran notablemente el juego en pareja y es uno de los elementos clave del [translate:Mus].",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        lineHeight = 22.sp
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
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Volver a Tutoriales",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicacionOrdinariaInterfacesTheme  {
        SenalesScreen(navController = rememberNavController())
    }

}



