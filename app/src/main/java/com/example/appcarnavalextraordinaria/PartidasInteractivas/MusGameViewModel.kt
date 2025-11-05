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

    private val _ganadorPares = MutableStateFlow<Jugador?>(null)
    val ganadorPares: StateFlow<Jugador?> = _ganadorPares

    private val _rondaParesActiva = MutableStateFlow(false)
    val rondaParesActiva: StateFlow<Boolean> = _rondaParesActiva

    private val _resultadosPares = MutableStateFlow<List<ResultadoPares>>(emptyList())
    val resultadosPares: StateFlow<List<ResultadoPares>> = _resultadosPares

    private var rondaActual = "grande"
    private var jugadorInicioPartida = 0

    private val _cartasDescartadas = MutableStateFlow(List(4) { emptyList<Int>() })
    val cartasDescartadas: StateFlow<List<List<Int>>> = _cartasDescartadas

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
        _turno.value = jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null

        // Verificar si hay pares
        val resultados = verificarPares()
        _resultadosPares.value = resultados

        if (resultados.all { it.combinacion is SinPares }) {
            _mensajes.value = "Ningún jugador tiene pares. Pasando a siguiente ronda..."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaParesSinGanador()
            }, 2000)
        } else {
            _mensajes.value = "Hay pares en la mesa. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}"
        }
    }

    fun realizarAccion(accion: Accion, cantidad: Int? = null) {
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
        avanzarTurnoMus()
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
                "pares" -> _ganadorPares.value = _jugadores.value[idxGanador]
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
            "pares" -> "Apuesta finalizada. Ganador de Pares: ${_ganadorPares.value?.nombre ?: "Nadie"}"
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

    private fun calcularGanadorPares(): Jugador? {
        val resultados = _resultadosPares.value
        var mejorResultado: ResultadoPares? = null

        // Encontrar la mejor combinación entre jugadores activos
        for (resultado in resultados) {
            val jugadorIndex = _jugadores.value.indexOf(resultado.jugador)
            if (_jugadoresActivos.value[jugadorIndex]) {
                if (mejorResultado == null || resultado.valorCombinacion > mejorResultado.valorCombinacion) {
                    mejorResultado = resultado
                } else if (resultado.valorCombinacion == mejorResultado.valorCombinacion) {
                    // Empate - desempate por jugador que empezó hablando
                    val jugadorActualIndex = _jugadores.value.indexOf(resultado.jugador)
                    val mejorJugadorIndex = _jugadores.value.indexOf(mejorResultado.jugador)

                    // Si el jugador actual es el que empezó hablando, gana
                    if (jugadorActualIndex == jugadorInicioPartida) {
                        mejorResultado = resultado
                    }
                    // Si el mejor actual no es el que empezó pero el nuevo sí, cambiamos
                    else if (mejorJugadorIndex != jugadorInicioPartida && jugadorActualIndex == jugadorInicioPartida) {
                        mejorResultado = resultado
                    }
                }
            }
        }

        return mejorResultado?.jugador
    }

    private fun finalizarRondaParesSinGanador() {
        _ganadorPares.value = null
        _rondaActiva.value = false
        _rondaParesActiva.value = false
        _mensajes.value = "Ronda de Pares finalizada sin ganadores"
        pasarApuestaOSiguienteRonda()
    }
}





