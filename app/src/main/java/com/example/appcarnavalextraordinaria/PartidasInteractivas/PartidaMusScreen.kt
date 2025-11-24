package com.example.appcarnavalextraordinaria.PartidasInteractivas

import android.content.Context
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Data.MovimientoDao
import com.example.appcarnavalextraordinaria.Data.PartidaDao

import com.example.appcarnavalextraordinaria.Data.UserDao
import com.example.appcarnavalextraordinaria.Navigation.Bars

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartidaMusScreen(
    navController: NavController,
    userDao: UserDao,
    partidaDao: PartidaDao,
    movimientoDao: MovimientoDao,
    currentUserId: Int,
    currentUsername: String
) {
    val context = LocalContext.current
    // Obtenemos el nombre de usuario guardado en las preferencias o usamos "Usuario" por defecto
    val currentUsername = remember {
        val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        sharedPreferences.getString("username", "Usuario") ?: "Usuario"
    }

    // Creamos el ViewModel para manejar la lógica del juego
    val musGameViewModel: MusGameViewModel = viewModel(
        factory = MusGameViewModelFactory(userDao, partidaDao, movimientoDao, currentUserId, currentUsername)
    )

    // Obtenemos los distintos estados reactivos del ViewModel para UI
    val jugadores by musGameViewModel.jugadores.collectAsState()
    val turno by musGameViewModel.turno.collectAsState()
    val mensajes by musGameViewModel.mensajes.collectAsState()
    val cartasRepartidas by musGameViewModel.cartasRepartidas.collectAsState()
    val rondaActiva by musGameViewModel.rondaActiva.collectAsState()
    val rondaMusActiva by musGameViewModel.rondaMusActiva.collectAsState()
    val rondaParesActiva by musGameViewModel.rondaParesActiva.collectAsState()
    val rondaJuegoActiva by musGameViewModel.rondaJuegoActiva.collectAsState()
    val rondaActual by musGameViewModel.rondaActual.collectAsState()
    val acciones by musGameViewModel.acciones.collectAsState()
    val apuestaActual by musGameViewModel.apuestaActual.collectAsState()
    val ganadorGrande by musGameViewModel.ganadorGrande.collectAsState()
    val ganadorChica by musGameViewModel.ganadorChica.collectAsState()
    val ganadorPares by musGameViewModel.ganadorPares.collectAsState()
    val ganadorJuego by musGameViewModel.ganadorJuego.collectAsState()
    val resultadosPares by musGameViewModel.resultadosPares.collectAsState()
    val jugadoresConPares by musGameViewModel.jugadoresConPares.collectAsState()
    val jugadoresActivos by musGameViewModel.jugadoresActivos.collectAsState()
    val cartasDescartadas by musGameViewModel.cartasDescartadas.collectAsState()
    val puntuacionesJuego by musGameViewModel.puntuacionesJuego.collectAsState()
    val partidaTerminada by musGameViewModel.partidaTerminada.collectAsState()

    // Estado para cantidad a subir en apuesta, con valor inicial 1 (como texto editable)
    var cantidadSubir by remember { mutableStateOf("1") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título y marcador de puntos para ambas parejas
        item {
            Text(
                "Partida de Mus - Apuestas",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tú & Bot 1: ${musGameViewModel.pareja1Puntos.collectAsState().value}")
                Text("Bot 2 & Bot 3: ${musGameViewModel.pareja2Puntos.collectAsState().value}")
            }
        }

        // Botón para repartir cartas solo si no están repartidas
        item {
            if (!cartasRepartidas) {
                Button(onClick = { musGameViewModel.repartirCartas() }) {
                    Text("Repartir Cartas")
                }
            }
        }

        // Se muestra info de pares si la ronda está activa
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

        // Se muestra info de juego si ronda está activa
        item {
            if (rondaJuegoActiva) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text(
                        "Ronda de Juego - Puntuaciones:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Magenta
                    )
                    jugadores.forEachIndexed { index, jugador ->
                        val puntuacion = puntuacionesJuego.getOrNull(index) ?: 0
                        val tieneJuego = puntuacion >= 31
                        val puedeJugar = if (puntuacionesJuego.any { it >= 31 }) {
                            tieneJuego
                        } else {
                            true // Si nadie tiene juego, todos juegan al punto
                        }

                        Text(
                            "${jugador.nombre}: $puntuacion puntos" +
                                    if (tieneJuego) " (JUEGO)" else " (Punto)" +
                                            if (!puedeJugar) " - No puede apostar" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                !puedeJugar -> Color.Gray
                                tieneJuego -> Color.Green
                                else -> Color.Blue
                            }
                        )
                    }
                }
            }
        }

        // Muestra las cartas y estado de cada jugador
        items(jugadores.size) { idx ->
            val puedeJugarEnJuego = if (rondaJuegoActiva) {
                val puntuacion = puntuacionesJuego.getOrNull(idx) ?: 0
                if (puntuacionesJuego.any { it >= 31 }) {
                    puntuacion >= 31
                } else {
                    true
                }
            } else {
                true
            }

            CartaJugador(
                jugadores[idx],
                turno == idx,
                jugadoresConPares.getOrNull(idx) == true,
                jugadoresActivos.getOrNull(idx) == true && puedeJugarEnJuego
            )
        }

        // Muestra los ganadores de las distintas rondas, si la ronda no está activa
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
                        val parejaGanadora = if (ganadorPares!!.first.nombre == "Tú" || ganadorPares!!.first.nombre == "Bot 1") {
                            "Tú y Bot 1"
                        } else {
                            "Bot 2 y Bot 3"
                        }
                        Text(
                            "Pares: $parejaGanadora",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Green
                        )
                    }
                    if (ganadorJuego != null) {
                        val parejaGanadora = if (ganadorJuego!!.first.nombre == "Tú" || ganadorJuego!!.first.nombre == "Bot 1") {
                            "Tú y Bot 1"
                        } else {
                            "Bot 2 y Bot 3"
                        }
                        val tieneJuego = puntuacionesJuego.any { it >= 31 }
                        Text(
                            if (tieneJuego) "Juego: $parejaGanadora" else "Punto: $parejaGanadora",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8A2BE2)
                        )
                    }
                    if (ganadorGrande != null && ganadorChica != null && ganadorPares != null && ganadorJuego != null) {
                        Text(
                            "Partida terminada!",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Información de apuesta actual
        item {
            Text(
                "Apuesta actual: ${apuestaActual?.cantidad ?: 0} piedras",
                style = MaterialTheme.typography.titleMedium
            )
        }
        // Mensajes de estado y turno
        item {
            Text(text = mensajes, fontSize = 18.sp)
        }

        // UI para descartar cartas en ronda Mus
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
                            Text(
                                when (carta.valor) {
                                    3 -> "R"
                                    12 -> "R"
                                    11 -> "C"
                                    10 -> "S"
                                    else -> carta.valor.toString()
                                },
                                fontSize = 16.sp
                            )
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

                when {
                    rondaParesActiva && rondaActual == "pares" -> {
                        val tienePares = jugadoresConPares.getOrNull(turno) == true
                        Text(
                            if (tienePares) "Tiene pares - Puede apostar" else "No tiene pares - No puede apostar en esta ronda",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (tienePares) Color.Green else Color.Red
                        )
                    }
                    rondaJuegoActiva && (rondaActual == "juego" || rondaActual == "punto") -> {
                        val puntuacion = puntuacionesJuego.getOrNull(turno) ?: 0
                        val tieneJuego = puntuacion >= 31
                        val hayJugadoresConJuego = puntuacionesJuego.any { it >= 31 }
                        val puedeJugar = if (hayJugadoresConJuego) tieneJuego else true

                        Text(
                            "Puntuación: $puntuacion - " +
                                    if (tieneJuego) "TIENE JUEGO" else "PUNTO" +
                                            if (!puedeJugar) " - No puede apostar" else " - Puede apostar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                !puedeJugar -> Color.Red
                                tieneJuego -> Color.Green
                                else -> Color.Blue
                            }
                        )
                    }
                    else -> {
                        Text(
                            "Ronda de ${rondaActual.replaceFirstChar { it.uppercase() }} - Todos pueden apostar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Blue
                        )
                    }
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
                    Button(onClick = { musGameViewModel.realizarAccion(Accion.PASAR) }) {
                        Text("Pasar")
                    }
                    Button(
                        onClick = {
                            val cant = cantidadSubir.toIntOrNull() ?: 1
                            musGameViewModel.realizarAccion(Accion.SUBIR, cant)
                        }
                    ) {
                        Text("Subir")
                    }
                    Button(onClick = { musGameViewModel.realizarAccion(Accion.IGUALAR) }) {
                        Text("Igualar")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { musGameViewModel.realizarAccion(Accion.RETIRARSE) }) {
                        Text("Retirarse")
                    }
                    Button(onClick = { musGameViewModel.realizarAccion(Accion.MUS) }) {
                        Text("Mus")
                    }
                }
            }
        }

        // Muestra todas las acciones realizadas en la ronda
        items(acciones.size) { i ->
            Text(
                acciones[i],
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth().padding(4.dp),
                textAlign = TextAlign.Center
            )
        }

        // Muestra resultado de partida si terminó
        item {
            if (partidaTerminada) {
                val resultado by musGameViewModel.resultadoPartida.collectAsState()
                Text(
                    text = resultado,
                    color = Color.Red,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Botón para reiniciar partida completa
        item {
            Button(
                onClick = { musGameViewModel.reiniciarPartidaManual() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Reiniciar partida completa")
            }
        }
        // Botón para repartir una nueva mano sin reiniciar puntos
        item{
            Button(
                onClick = { musGameViewModel.nuevaMano() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Repartir nuevas cartas")
            }
        }
        // Botón para volver a la pantalla principal
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver a la pantalla principal")
            }
        }
    }
}

// Composable para mostrar la UI de un solo jugador y sus cartas
@Composable
fun CartaJugador(jugador: Jugador, esTurno: Boolean, tienePares: Boolean, estaActivo: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(
                when {
                    !estaActivo -> Color.LightGray.copy(alpha = 0.5f) // Deshabilitado visual
                    esTurno -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Turno destacado
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
                        Text(
                            when (carta.valor) {
                                3 -> "R" // Rey se muestra como "R"
                                12 -> "R"
                                11 -> "C" // Caballo
                                10 -> "S" // Sota
                                else -> carta.valor.toString()
                            },
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// Función auxiliar que devuelve el nombre descriptivo de la combinación de pares
private fun obtenerNombreCombinacion(combinacion: CombinacionPares): String {
    return when (combinacion) {
        is SinPares -> "Sin pares"
        is Par -> {
            val nombreCarta = when (combinacion.valorCarta) {
                3 -> "Reyes"
                11 -> "Caballos"
                10 -> "Sotas"
                7 -> "Sietes"
                6 -> "Seis"
                5 -> "Cincos"
                4 -> "Cuatros"
                2 -> "Dos"
                1 -> "Ases"
                else -> combinacion.valorCarta.toString()
            }
            "Par de $nombreCarta"
        }
        is Medias -> {
            val nombreCarta = when (combinacion.valorCarta) {
                3 -> "Reyes"
                11 -> "Caballos"
                10 -> "Sotas"
                7 -> "Sietes"
                6 -> "Seis"
                5 -> "Cincos"
                4 -> "Cuatros"
                2 -> "Dos"
                1 -> "Ases"
                else -> combinacion.valorCarta.toString()
            }
            "Medias de $nombreCarta"
        }
        is Duples -> {
            val nombreCarta1 = when (combinacion.valorCarta1) {
                3 -> "Reyes"
                11 -> "Caballos"
                10 -> "Sotas"
                7 -> "Sietes"
                6 -> "Seis"
                5 -> "Cincos"
                4 -> "Cuatros"
                2 -> "Dos"
                1 -> "Ases"
                else -> combinacion.valorCarta1.toString()
            }
            val nombreCarta2 = when (combinacion.valorCarta2) {
                3 -> "Reyes"
                11 -> "Caballos"
                10 -> "Sotas"
                7 -> "Sietes"
                6 -> "Seis"
                5 -> "Cincos"
                4 -> "Cuatros"
                2 -> "Dos"
                1 -> "Ases"
                else -> combinacion.valorCarta2.toString()
            }
            "Duples de $nombreCarta1 y $nombreCarta2"
        }
        else -> "Desconocido"
    }
}
