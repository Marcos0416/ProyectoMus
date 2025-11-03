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
fun EstrategiasScreen(navController: NavController) {
    Bars(navController = navController) { modifier ->
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(modifier = androidx.compose.ui.Modifier.padding(24.dp)) {
                Text(
                    text = "Estrategias Iniciales",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                    text = "• Observa siempre las señales de tu compañero para aprovecharlas.\n\n" +
                            "• Evalúa la fuerza de tu mano antes de hacer apuestas grandes.\n\n" +
                            "• Intenta confundir al rival con apuestas estratégicas.\n\n" +
                            "• La práctica constante mejora la coordinación con el compañero.\n\n" +
                            "• Mantente atento a los gestos y movimientos durante toda la partida.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 22.sp
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(24.dp))
                Button(
                    onClick = { navController.navigate("Tutoriales") {
                        popUpTo("tutoriales_main") { inclusive = true }
                    } },
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                ) {
                    Text(text = "Volver", textAlign = TextAlign.Center, modifier = androidx.compose.ui.Modifier.fillMaxWidth())
                }
            }
        }
    }
}
