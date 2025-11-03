package com.example.appcarnavalextraordinaria.PartidasInteractivas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Navigation.Bars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidaMusScreen(navController: NavController, musGameViewModel: MusGameViewModel = viewModel()) {
    val jugadores by musGameViewModel.jugadores.collectAsState()
    val turno by musGameViewModel.turno.collectAsState()
    val mensajes by musGameViewModel.mensajes.collectAsState()
    val cartasRepartidas by musGameViewModel.cartasRepartidas.collectAsState()
    val rondaActiva by musGameViewModel.rondaActiva.collectAsState()
    val acciones by musGameViewModel.acciones.collectAsState()
    val apuestaActual by musGameViewModel.apuestaActual.collectAsState()
    val ganadorGrande by musGameViewModel.ganadorGrande.collectAsState()
    val jugadoresActivos by musGameViewModel.jugadoresActivos.collectAsState()

    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Partida de Mus - Apuestas reales",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary)

                if (!cartasRepartidas) {
                    Button(onClick = { musGameViewModel.repartirCartas() }) {
                        Text("Repartir Cartas")
                    }
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    jugadores.forEachIndexed { idx, jugador ->
                        CartaJugador(jugador, turno == idx)
                    }
                    ganadorGrande?.let { ganador ->
                        Text(
                            text = "Ganador de Grande: ${ganador.nombre}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }

                Text(
                    text = "Apuesta actual: ${apuestaActual?.cantidad ?: 0} piedras",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(text = mensajes, fontSize = 18.sp, modifier = Modifier.padding(vertical = 12.dp))

                // Solo el humano en turno activo puede ver botones
                if (rondaActiva && jugadores[turno].esHumano && jugadoresActivos[turno]) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = { musGameViewModel.realizarAccion(Accion.PASAR) }) { Text("Pasar") }
                        Button(onClick = { musGameViewModel.realizarAccion(Accion.SUBIR, (apuestaActual?.cantidad ?: 2) + 1) }) { Text("Subir") }
                        Button(onClick = { musGameViewModel.realizarAccion(Accion.IGUALAR) }) { Text("Igualar") }
                        Button(onClick = { musGameViewModel.realizarAccion(Accion.RETIRARSE) }) { Text("Retirarse") }
                    }
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(acciones.size) { i ->
                        Text(
                            acciones[i],
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (!rondaActiva && cartasRepartidas) {
                    Button(onClick = { musGameViewModel.reiniciar() }) {
                        Text("Reiniciar Ronda")
                    }
                }
            }
        }
    }
}


@Composable
fun CartaJugador(jugador: Jugador, esTurno: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(if (esTurno) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(jugador.nombre, style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                jugador.cartas.forEach { carta ->
                    Box(
                        modifier = Modifier
                            .size(40.dp, 60.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = carta.toString())
                    }
                }
            }
        }
    }
}







