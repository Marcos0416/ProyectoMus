package com.example.appcarnavalextraordinaria.PartidasInteractivas



import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val rondaMusActiva by musGameViewModel.rondaMusActiva.collectAsState()
    val rondaParesActiva by musGameViewModel.rondaParesActiva.collectAsState()
    val acciones by musGameViewModel.acciones.collectAsState()
    val apuestaActual by musGameViewModel.apuestaActual.collectAsState()
    val ganadorGrande by musGameViewModel.ganadorGrande.collectAsState()
    val ganadorChica by musGameViewModel.ganadorChica.collectAsState()
    val ganadorPares by musGameViewModel.ganadorPares.collectAsState()
    val resultadosPares by musGameViewModel.resultadosPares.collectAsState()
    val jugadoresConPares by musGameViewModel.jugadoresConPares.collectAsState()
    val jugadoresActivos by musGameViewModel.jugadoresActivos.collectAsState()
    val cartasDescartadas by musGameViewModel.cartasDescartadas.collectAsState()

    var cantidadSubir by remember { mutableStateOf("1") }

    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        "Partida de Mus - Apuestas",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                item {
                    if (!cartasRepartidas) {
                        Button(onClick = { musGameViewModel.repartirCartas() }) {
                            Text("Repartir Cartas")
                        }
                    }
                }

                // Mostrar información de pares si está activa la ronda
                item {
                    if (rondaParesActiva) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .padding(8.dp)
                        ) {
                            Text(
                                "Ronda de Pares - Combinaciones:",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Magenta
                            )
                            resultadosPares.forEachIndexed { index, resultado ->
                                val tienePares = jugadoresConPares.getOrNull(index) ?: false
                                Text(
                                    "${resultado.jugador.nombre}: ${obtenerNombreCombinacion(resultado.combinacion)}" +
                                            if (!tienePares) " (Sin pares)" else "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (tienePares) Color.Unspecified else Color.Gray
                                )
                            }
                        }
                    }
                }

                items(jugadores.size) { idx ->
                    CartaJugador(jugadores[idx], turno == idx,
                        jugadoresConPares.getOrNull(idx) == true,
                        jugadoresActivos.getOrNull(idx) == true)
                }

                // Mostrar ganadores
                item {
                    if (!rondaActiva && cartasRepartidas) {
                        Column {
                            if (ganadorGrande != null) {
                                Text(
                                    "Grande: ${ganadorGrande?.nombre ?: "Nadie"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Blue
                                )
                            }
                            if (ganadorChica != null) {
                                Text(
                                    "Chica: ${ganadorChica?.nombre ?: "Nadie"}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Red
                                )
                            }
                            if (ganadorPares != null) {
                                Text(
                                    "Pares: ${ganadorPares!!.first.nombre} y ${ganadorPares!!.second.nombre}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Green
                                )
                            }
                            if (ganadorGrande != null && ganadorChica != null && ganadorPares != null) {
                                Text(
                                    "Partida terminada!",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Apuesta actual: ${apuestaActual?.cantidad ?: 0} piedras",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                item {
                    Text(text = mensajes, fontSize = 18.sp)
                }

                // Descartes Mus
                if (rondaMusActiva) {
                    item {
                        Text("Selecciona las cartas a descartar para: ${jugadores[turno].nombre}")
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            jugadores[turno].cartas.forEach { carta ->
                                val descartada = cartasDescartadas[turno].contains(carta.id)
                                Box(
                                    modifier = Modifier
                                        .size(50.dp, 70.dp)
                                        .background(if (descartada) Color.Red else Color.LightGray)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = LocalIndication.current,
                                            onClick = { musGameViewModel.toggleDescartarCarta(turno, carta.id) }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(carta.valor.toString(), fontSize = 20.sp)
                                }
                            }
                        }
                        Button(
                            onClick = { musGameViewModel.confirmarDescartesJugador(turno) },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Text("Confirmar descartes")
                        }
                    }
                } else if (rondaActiva && jugadoresActivos[turno]) {
                    item {
                        Text("Turno de: ${jugadores[turno].nombre}", style = MaterialTheme.typography.titleMedium)

                        // En ronda de pares, mostrar si el jugador tiene pares
                        if (rondaParesActiva) {
                            val tienePares = jugadoresConPares.getOrNull(turno) == true
                            Text(
                                if (tienePares) "Tiene pares - Puede apostar" else "No tiene pares - No puede apostar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (tienePares) Color.Green else Color.Red
                            )
                        }

                        TextField(
                            value = cantidadSubir,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() }) {
                                    cantidadSubir = newValue
                                }
                            },
                            label = { Text("Piedras a subir") },
                            modifier = Modifier.width(150.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { musGameViewModel.realizarAccion(Accion.PASAR) }) { Text("Pasar") }
                            Button(
                                onClick = {
                                    val cant = cantidadSubir.toIntOrNull() ?: 1
                                    musGameViewModel.realizarAccion(Accion.SUBIR, cant)
                                }
                            ) { Text("Subir") }
                            Button(onClick = { musGameViewModel.realizarAccion(Accion.IGUALAR) }) { Text("Igualar") }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(onClick = { musGameViewModel.realizarAccion(Accion.RETIRARSE) }) { Text("Retirarse") }
                            Button(onClick = { musGameViewModel.realizarAccion(Accion.MUS) }) { Text("Mus") }
                        }
                    }
                }

                items(acciones.size) { i ->
                    Text(
                        acciones[i],
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    if (!rondaActiva && cartasRepartidas && !rondaMusActiva) {
                        Button(
                            onClick = { musGameViewModel.reiniciar() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reiniciar Ronda")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartaJugador(jugador: Jugador, esTurno: Boolean, tienePares: Boolean, estaActivo: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                when {
                    !estaActivo -> Color.LightGray.copy(alpha = 0.5f)
                    esTurno -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                jugador.nombre +
                        (if (!estaActivo) " (No activo)" else "") +
                        (if (tienePares) " ✓" else ""),
                style = MaterialTheme.typography.titleMedium,
                color = when {
                    !estaActivo -> Color.Gray
                    tienePares -> Color.Green
                    else -> Color.Unspecified
                }
            )
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
                        Text(carta.valor.toString())
                    }
                }
            }
        }
    }
}

// Función auxiliar para obtener el nombre de la combinación
private fun obtenerNombreCombinacion(combinacion: CombinacionPares): String {
    return when (combinacion) {
        is SinPares -> "Sin pares"
        is Par -> "Par de ${combinacion.valorCarta}"
        is Medias -> "Medias de ${combinacion.valorCarta}"
        is Duples -> "Duples de ${combinacion.valorCarta1} y ${combinacion.valorCarta2}"
        else -> "Desconocido"
    }
}










