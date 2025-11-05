package com.example.appcarnavalextraordinaria.PartidasInteractivas







import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.os.Handler
import android.os.Looper

data class Carta(val id: Int, val valor: Int)
data class Jugador(val nombre: String, val esHumano: Boolean, val cartas: List<Carta> = emptyList())
enum class Accion { PASAR, ENVIDAR, SUBIR, IGUALAR, RETIRARSE, MUS }
data class Apuesta(val cantidad: Int, val jugador: Jugador)

// Combinaciones de pares
sealed class CombinacionPares
object SinPares : CombinacionPares()
data class Par(val valorCarta: Int) : CombinacionPares()
data class Medias(val valorCarta: Int) : CombinacionPares()
data class Duples(val valorCarta1: Int, val valorCarta2: Int) : CombinacionPares()

data class ResultadoPares(
    val combinacion: CombinacionPares,
    val jugador: Jugador,
    val valorCombinacion: Int = 0
)

class MusGameViewModel : ViewModel() {
    private var siguienteIdCarta = 0

    private val _jugadores = MutableStateFlow(
        listOf(
            Jugador("Tú", true),
            Jugador("Bot 1", false),
            Jugador("Bot 2", false),
            Jugador("Bot 3", false)
        )
    )
    val jugadores: StateFlow<List<Jugador>> = _jugadores

    private val _turno = MutableStateFlow(0)
    val turno: StateFlow<Int> = _turno

    private val _mensajes = MutableStateFlow("Comienza la partida. Pulsa repartir cartas.")
    val mensajes: StateFlow<String> = _mensajes

    private val _cartasRepartidas = MutableStateFlow(false)
    val cartasRepartidas: StateFlow<Boolean> = _cartasRepartidas

    private val _rondaActiva = MutableStateFlow(false)
    val rondaActiva: StateFlow<Boolean> = _rondaActiva

    private val _rondaMusActiva = MutableStateFlow(false)
    val rondaMusActiva: StateFlow<Boolean> = _rondaMusActiva

    private val _acciones = MutableStateFlow(listOf<String>())
    val acciones: StateFlow<List<String>> = _acciones

    private val _apuestaActual = MutableStateFlow<Apuesta?>(null)
    val apuestaActual: StateFlow<Apuesta?> = _apuestaActual

    private val _jugadoresActivos = MutableStateFlow(MutableList(4) { true })
    val jugadoresActivos: StateFlow<List<Boolean>> = _jugadoresActivos

    private val _jugadorUltimaSubida = MutableStateFlow<Int?>(null)
    val jugadorUltimaSubida: StateFlow<Int?> = _jugadorUltimaSubida

    private val _musPedidos = MutableStateFlow(MutableList(4) { false })
    val musPedidos: StateFlow<List<Boolean>> = _musPedidos

    private val _ganadorGrande = MutableStateFlow<Jugador?>(null)
    val ganadorGrande: StateFlow<Jugador?> = _ganadorGrande

    private val _ganadorChica = MutableStateFlow<Jugador?>(null)
    val ganadorChica: StateFlow<Jugador?> = _ganadorChica

    private val _ganadorPares = MutableStateFlow<Pair<Jugador, Jugador>?>(null)
    val ganadorPares: StateFlow<Pair<Jugador, Jugador>?> = _ganadorPares

    private val _rondaParesActiva = MutableStateFlow(false)
    val rondaParesActiva: StateFlow<Boolean> = _rondaParesActiva

    private val _resultadosPares = MutableStateFlow<List<ResultadoPares>>(emptyList())
    val resultadosPares: StateFlow<List<ResultadoPares>> = _resultadosPares

    private val _jugadoresConPares = MutableStateFlow<List<Boolean>>(emptyList())
    val jugadoresConPares: StateFlow<List<Boolean>> = _jugadoresConPares

    private var rondaActual = "grande"
    private var jugadorInicioPartida = 0

    private val _cartasDescartadas = MutableStateFlow(List(4) { emptyList<Int>() })
    val cartasDescartadas: StateFlow<List<List<Int>>> = _cartasDescartadas

    // Definir parejas (0-1 y 2-3)
    private val parejas = listOf(
        listOf(0, 1), // Tú y Bot 1
        listOf(2, 3)  // Bot 2 y Bot 3
    )

    fun repartirCartas() {
        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }
        siguienteIdCarta = 0
        _jugadores.value = _jugadores.value.map {
            it.copy(cartas = List(4) {
                Carta(siguienteIdCarta++, cartasValidas.random())
            })
        }
        _cartasRepartidas.value = true
        rondaActual = "grande"
        _rondaActiva.value = true
        _rondaMusActiva.value = false
        _rondaParesActiva.value = false
        _turno.value = 0
        jugadorInicioPartida = 0
        _mensajes.value = "Cartas repartidas. Es tu turno."
        _acciones.value = emptyList()
        _apuestaActual.value = null
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
        _ganadorGrande.value = null
        _ganadorChica.value = null
        _ganadorPares.value = null
        _resultadosPares.value = emptyList()
        _jugadoresConPares.value = emptyList()
        _cartasDescartadas.value = List(4) { emptyList() }
        _musPedidos.value = MutableList(4) { false }
    }

    fun toggleDescartarCarta(jugadorIndex: Int, cartaId: Int) {
        val descartes = _cartasDescartadas.value.toMutableList()
        val cartasJugador = descartes[jugadorIndex].toMutableList()
        if (cartasJugador.contains(cartaId)) {
            cartasJugador.remove(cartaId)
        } else {
            cartasJugador.add(cartaId)
        }
        descartes[jugadorIndex] = cartasJugador
        _cartasDescartadas.value = descartes
    }

    fun confirmarDescartesJugador(jugadorIndex: Int) {
        val descartes = _cartasDescartadas.value[jugadorIndex]
        val cartasActuales = _jugadores.value[jugadorIndex].cartas.toMutableList()
        cartasActuales.removeAll { descartes.contains(it.id) }
        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }
        val nuevasCartas = List(descartes.size) {
            Carta(siguienteIdCarta++, cartasValidas.random())
        }
        cartasActuales.addAll(nuevasCartas)
        val nuevosJugadores = _jugadores.value.toMutableList()
        nuevosJugadores[jugadorIndex] = nuevosJugadores[jugadorIndex].copy(cartas = cartasActuales)
        _jugadores.value = nuevosJugadores

        val nuevosDescartes = _cartasDescartadas.value.toMutableList()
        nuevosDescartes[jugadorIndex] = emptyList()
        _cartasDescartadas.value = nuevosDescartes

        pasarSiguienteJugadorRondaMus(jugadorIndex)
    }

    private fun pasarSiguienteJugadorRondaMus(jugadorActual: Int) {
        val siguiente = (jugadorActual + 1) % 4
        if (siguiente == 0) {
            _rondaMusActiva.value = false
            _musPedidos.value = MutableList(4) { false }
            _mensajes.value = "Ronda de Mus finalizada. Comienza ronda de Grande."
            iniciarRondaGrande()
        } else {
            _turno.value = siguiente
            _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre} para descartar cartas."
        }
    }

    fun iniciarRondaGrande() {
        rondaActual = "grande"
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Grande iniciada. Apostar y jugar."
        _turno.value = 0
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
    }

    fun iniciarRondaChica() {
        rondaActual = "chica"
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Chica iniciada. Apostar y jugar."
        _turno.value = 0
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
    }

    fun iniciarRondaPares() {
        rondaActual = "pares"
        _rondaActiva.value = true
        _rondaParesActiva.value = true
        _mensajes.value = "Ronda de Pares iniciada. Verificando pares..."

        // Verificar qué jugadores tienen pares
        val resultados = verificarPares()
        _resultadosPares.value = resultados

        // Determinar qué jugadores tienen pares
        val conPares = resultados.map { it.combinacion !is SinPares }
        _jugadoresConPares.value = conPares

        // Determinar qué parejas tienen pares (al menos un jugador con pares)
        val parejasConPares = parejas.map { pareja ->
            pareja.any { conPares[it] }
        }

        // Solo las parejas con pares pueden participar
        val nuevosActivos = MutableList(4) { index ->
            val parejaIndex = if (index <= 1) 0 else 1
            parejasConPares[parejaIndex]
        }
        _jugadoresActivos.value = nuevosActivos

        // Encontrar el primer jugador activo para empezar
        val primerJugadorActivo = nuevosActivos.indexOfFirst { it }
        _turno.value = if (primerJugadorActivo != -1) primerJugadorActivo else 0

        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadorUltimaSubida.value = null

        if (resultados.all { it.combinacion is SinPares }) {
            _mensajes.value = "Ningún jugador tiene pares. Pasando a siguiente ronda..."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaParesSinGanador()
            }, 2000)
        } else {
            val parejasActivas = parejasConPares.count { it }
            _mensajes.value = when (parejasActivas) {
                1 -> "Solo una pareja tiene pares. Ganadores automáticos."
                2 -> "Ambas parejas tienen pares. Comienza ${_jugadores.value[_turno.value].nombre}"
                else -> "Ronda de Pares iniciada"
            }

            // Si solo una pareja tiene pares, ganan automáticamente
            if (parejasActivas == 1) {
                Handler(Looper.getMainLooper()).postDelayed({
                    finalizarRondaParesConGanadorAutomatico()
                }, 2000)
            }
        }
    }

    fun realizarAccion(accion: Accion, cantidad: Int? = null) {
        // En la ronda de pares, solo los jugadores de parejas con pares pueden actuar
        if (rondaActual == "pares") {
            val parejaIndex = if (_turno.value <= 1) 0 else 1
            val parejaTienePares = parejas[parejaIndex].any { _jugadoresConPares.value[it] }
            if (!parejaTienePares) {
                // Esta pareja no tiene pares, no puede actuar
                return
            }
        }

        if (!_rondaActiva.value || !_jugadoresActivos.value[_turno.value]) return

        val jugadorActual = _jugadores.value[_turno.value]
        val log = _acciones.value.toMutableList()
        when (accion) {
            Accion.ENVIDAR, Accion.SUBIR -> {
                val incremento = cantidad ?: 1
                val cantidadActual = _apuestaActual.value?.cantidad ?: 0
                val nuevaCantidad = cantidadActual + incremento
                _apuestaActual.value = Apuesta(nuevaCantidad, jugadorActual)
                _jugadorUltimaSubida.value = _turno.value
                log.add("${jugadorActual.nombre} sube la apuesta a $nuevaCantidad piedras")
            }
            Accion.IGUALAR -> {
                log.add("${jugadorActual.nombre} iguala la apuesta de ${_apuestaActual.value?.cantidad ?: 0} piedras")
                if (todosHanIgualado()) finalizarApuesta()
            }
            Accion.PASAR -> {
                log.add("${jugadorActual.nombre} pasa")
                comprobarUnicoActivo()
            }
            Accion.RETIRARSE -> {
                log.add("${jugadorActual.nombre} se retira de la apuesta")
                _jugadoresActivos.value[_turno.value] = false
                comprobarUnicoActivo()
            }
            Accion.MUS -> {
                log.add("${jugadorActual.nombre} pide Mus")
                val musPedidosMutable = _musPedidos.value.toMutableList()
                musPedidosMutable[_turno.value] = true
                _musPedidos.value = musPedidosMutable
            }
        }
        _acciones.value = log

        // Avanzar turno solo a jugadores activos (de parejas con pares)
        avanzarTurnoPares()
    }

    private fun avanzarTurnoPares() {
        if (rondaActual != "pares") {
            avanzarTurnoMus()
            return
        }

        var siguiente = (_turno.value + 1) % 4
        var intentos = 0

        // Buscar siguiente jugador activo (de pareja con pares)
        while (intentos < 4) {
            val parejaIndex = if (siguiente <= 1) 0 else 1
            val parejaTienePares = parejas[parejaIndex].any { _jugadoresConPares.value[it] }

            if (parejaTienePares && _jugadoresActivos.value[siguiente]) {
                _turno.value = siguiente
                _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"
                return
            }

            siguiente = (siguiente + 1) % 4
            intentos++
        }

        // Si no hay más jugadores activos, finalizar
        _turno.value = siguiente
    }

    private fun comprobarUnicoActivo() {
        val activos = _jugadoresActivos.value
        val quedan = activos.count { it }
        if (quedan == 1) {
            val idxGanador = activos.indexOfFirst { it }
            _rondaActiva.value = false
            _mensajes.value = "¡${_jugadores.value[idxGanador].nombre} gana la apuesta porque todos han pasado o se han retirado!"
            when (rondaActual) {
                "grande" -> _ganadorGrande.value = _jugadores.value[idxGanador]
                "chica" -> _ganadorChica.value = _jugadores.value[idxGanador]
                "pares" -> {
                    // En pares, el ganador es la pareja completa
                    val parejaIndex = if (idxGanador <= 1) 0 else 1
                    _ganadorPares.value = _jugadores.value[parejas[parejaIndex][0]] to _jugadores.value[parejas[parejaIndex][1]]
                }
            }
            pasarApuestaOSiguienteRonda()
        }
    }

    private fun todosHanIgualado(): Boolean {
        val posUltimaSubida = _jugadorUltimaSubida.value ?: return false
        return _turno.value == posUltimaSubida
    }

    private fun finalizarApuesta() {
        when (rondaActual) {
            "grande" -> _ganadorGrande.value = calcularGanadorGrande()
            "chica" -> _ganadorChica.value = calcularGanadorChica()
            "pares" -> _ganadorPares.value = calcularGanadorPares()
        }
        _rondaActiva.value = false
        _mensajes.value = when (rondaActual) {
            "grande" -> "Apuesta finalizada. Ganador de Grande: ${_ganadorGrande.value?.nombre ?: "Nadie"}"
            "chica" -> "Apuesta finalizada. Ganador de Chica: ${_ganadorChica.value?.nombre ?: "Nadie"}"
            "pares" -> {
                val ganadores = _ganadorPares.value
                "Apuesta finalizada. Ganadores de Pares: ${ganadores?.first?.nombre ?: "Nadie"} y ${ganadores?.second?.nombre ?: "Nadie"}"
            }
            else -> ""
        }
        pasarApuestaOSiguienteRonda()
    }

    private fun pasarApuestaOSiguienteRonda() {
        when (rondaActual) {
            "grande" -> iniciarRondaChica()
            "chica" -> iniciarRondaPares()
            "pares" -> {
                _mensajes.value = "Ronda de Pares finalizada. Partida terminada."
                _rondaParesActiva.value = false
            }
        }
    }

    fun iniciarRondaMus() {
        _rondaMusActiva.value = true
        _rondaActiva.value = false
        _mensajes.value = "Ronda de Mus iniciada. Selecciona las cartas a descartar."
        _turno.value = 0
        _cartasDescartadas.value = List(4) { emptyList() }
    }

    fun reiniciar() { repartirCartas() }

    // ----------- GRANDE -----------
    private fun fuerzaCarta(carta: Int): Int = when (carta) {
        3, 12 -> 13
        11 -> 12
        10 -> 11
        7 -> 10
        6 -> 9
        5 -> 8
        4 -> 7
        2 -> 6
        1 -> 5
        else -> 0
    }
    private fun ordenarCartasGrande(cartas: List<Int>) = cartas.sortedByDescending { fuerzaCarta(it) }
    private fun compararCartasGrande(cartas1: List<Int>, cartas2: List<Int>): Int {
        val mano1 = ordenarCartasGrande(cartas1)
        val mano2 = ordenarCartasGrande(cartas2)
        for (i in mano1.indices) {
            val diff = fuerzaCarta(mano1.getOrNull(i) ?: 0) - fuerzaCarta(mano2.getOrNull(i) ?: 0)
            if (diff != 0) return diff
        }
        return 0
    }
    private fun calcularGanadorGrande(): Jugador? {
        var ganador: Jugador? = null
        var mejorMano: List<Int>? = null
        for (i in _jugadores.value.indices) {
            if (_jugadoresActivos.value[i]) {
                val cartasValores = _jugadores.value[i].cartas.map { it.valor }
                if (ganador == null || compararCartasGrande(cartasValores, mejorMano ?: emptyList()) > 0) {
                    ganador = _jugadores.value[i]
                    mejorMano = cartasValores
                }
            }
        }
        return ganador
    }

    // ----------- CHICA -----------
    private fun fuerzaCartaChica(carta: Int): Int = when (carta) {
        1, 2 -> 1
        4 -> 2
        5 -> 3
        6 -> 4
        7 -> 5
        10 -> 6
        11 -> 7
        3, 12 -> 8
        else -> 9
    }
    private fun ordenarCartasChica(cartas: List<Int>) = cartas.sortedBy { fuerzaCartaChica(it) }
    private fun compararCartasChica(cartas1: List<Int>, cartas2: List<Int>): Int {
        val mano1 = ordenarCartasChica(cartas1)
        val mano2 = ordenarCartasChica(cartas2)
        for (i in mano1.indices) {
            val diff = fuerzaCartaChica(mano1.getOrNull(i) ?: 0) - fuerzaCartaChica(mano2.getOrNull(i) ?: 0)
            if (diff != 0) return -diff
        }
        return 0
    }
    private fun calcularGanadorChica(): Jugador? {
        var ganador: Jugador? = null
        var mejorMano: List<Int>? = null
        for (i in _jugadores.value.indices) {
            if (_jugadoresActivos.value[i]) {
                val cartasValores = _jugadores.value[i].cartas.map { it.valor }
                if (ganador == null || compararCartasChica(cartasValores, mejorMano ?: emptyList()) > 0) {
                    ganador = _jugadores.value[i]
                    mejorMano = cartasValores
                }
            }
        }
        return ganador
    }

    // ----------- PARES -----------
    private fun verificarPares(): List<ResultadoPares> {
        return _jugadores.value.map { jugador ->
            val combinacion = detectarCombinacionPares(jugador.cartas.map { it.valor })
            val valorCombinacion = calcularValorCombinacion(combinacion)
            ResultadoPares(combinacion, jugador, valorCombinacion)
        }
    }

    private fun detectarCombinacionPares(cartas: List<Int>): CombinacionPares {
        val conteo = cartas.groupingBy { it }.eachCount()

        // Buscar duples (2 pares diferentes)
        val pares = conteo.entries.filter { it.value >= 2 }
        if (pares.size >= 2) {
            val valoresPares = pares.map { it.key }.sortedByDescending { fuerzaCartaPares(it) }
            return Duples(valoresPares[0], valoresPares[1])
        }

        // Buscar medias (3 cartas iguales)
        conteo.entries.find { it.value == 3 }?.let {
            return Medias(it.key)
        }

        // Buscar par simple (2 cartas iguales)
        conteo.entries.find { it.value == 2 }?.let {
            return Par(it.key)
        }

        return SinPares
    }

    private fun fuerzaCartaPares(carta: Int): Int = when (carta) {
        3, 12 -> 13  // Reyes
        11 -> 12     // Caballos
        10 -> 11     // Sotas
        7 -> 10      // Sietes
        6 -> 9       // Seis
        5 -> 8       // Cincos
        4 -> 7       // Cuatros
        2 -> 6       // Dos
        1 -> 5       // Ases
        else -> 0
    }

    private fun calcularValorCombinacion(combinacion: CombinacionPares): Int {
        return when (combinacion) {
            is SinPares -> 0
            is Par -> 100 + fuerzaCartaPares(combinacion.valorCarta)
            is Medias -> 200 + fuerzaCartaPares(combinacion.valorCarta)
            is Duples -> 300 + fuerzaCartaPares(combinacion.valorCarta1) * 10 +
                    fuerzaCartaPares(combinacion.valorCarta2)
            else -> 0
        }
    }

    private fun calcularGanadorPares(): Pair<Jugador, Jugador>? {
        val resultados = _resultadosPares.value

        // Calcular la mejor combinación por pareja
        val mejorPareja1 = resultados
            .filterIndexed { index, _ -> index <= 1 }
            .maxByOrNull { it.valorCombinacion }

        val mejorPareja2 = resultados
            .filterIndexed { index, _ -> index >= 2 }
            .maxByOrNull { it.valorCombinacion }

        // Si una pareja no tiene pares, la otra gana automáticamente
        if (mejorPareja1?.combinacion is SinPares && mejorPareja2?.combinacion is SinPares) {
            return null
        }

        if (mejorPareja1?.combinacion is SinPares) {
            return _jugadores.value[2] to _jugadores.value[3]
        }

        if (mejorPareja2?.combinacion is SinPares) {
            return _jugadores.value[0] to _jugadores.value[1]
        }

        // Comparar las mejores combinaciones de cada pareja
        val comparacion = (mejorPareja1?.valorCombinacion ?: 0) - (mejorPareja2?.valorCombinacion ?: 0)

        return when {
            comparacion > 0 -> _jugadores.value[0] to _jugadores.value[1]
            comparacion < 0 -> _jugadores.value[2] to _jugadores.value[3]
            else -> {
                // Empate - gana la pareja del jugador que empezó hablando
                if (jugadorInicioPartida <= 1) {
                    _jugadores.value[0] to _jugadores.value[1]
                } else {
                    _jugadores.value[2] to _jugadores.value[3]
                }
            }
        }
    }

    private fun finalizarRondaParesSinGanador() {
        _ganadorPares.value = null
        _rondaActiva.value = false
        _rondaParesActiva.value = false
        _mensajes.value = "Ronda de Pares finalizada sin ganadores"
        pasarApuestaOSiguienteRonda()
    }

    private fun finalizarRondaParesConGanadorAutomatico() {
        val resultados = _resultadosPares.value
        val pareja1TienePares = resultados[0].combinacion !is SinPares || resultados[1].combinacion !is SinPares
        val pareja2TienePares = resultados[2].combinacion !is SinPares || resultados[3].combinacion !is SinPares

        _ganadorPares.value = when {
            pareja1TienePares && !pareja2TienePares -> _jugadores.value[0] to _jugadores.value[1]
            !pareja1TienePares && pareja2TienePares -> _jugadores.value[2] to _jugadores.value[3]
            else -> null
        }

        _rondaActiva.value = false
        _rondaParesActiva.value = false
        _mensajes.value = if (_ganadorPares.value != null) {
            "Ganadores automáticos de Pares: ${_ganadorPares.value!!.first.nombre} y ${_ganadorPares.value!!.second.nombre}"
        } else {
            "Ronda de Pares finalizada sin ganadores"
        }
        pasarApuestaOSiguienteRonda()
    }

    private fun avanzarTurnoMus() {
        val siguiente = (_turno.value + 1) % 4
        if (siguiente == 0) {
            if (_musPedidos.value.all { it }) {
                _musPedidos.value = MutableList(4) { false }
                iniciarRondaMus()
            } else {
                _rondaActiva.value = true
                _mensajes.value = when (rondaActual) {
                    "grande" -> "Comienza la ronda de Grande"
                    "chica" -> "Comienza la ronda de Chica"
                    "pares" -> "Comienza la ronda de Pares"
                    else -> "Comienza la ronda"
                }
                _turno.value = 0
            }
        } else {
            _turno.value = siguiente
            _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"
        }
    }
}





