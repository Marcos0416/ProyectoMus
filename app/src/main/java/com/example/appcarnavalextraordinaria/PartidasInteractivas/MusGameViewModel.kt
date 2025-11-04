package com.example.appcarnavalextraordinaria.PartidasInteractivas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class Carta(val id: Int, val valor: Int)
data class Jugador(val nombre: String, val esHumano: Boolean, val cartas: List<Carta> = emptyList())
enum class Accion { PASAR, ENVIDAR, SUBIR, IGUALAR, RETIRARSE, MUS }
data class Apuesta(val cantidad: Int, val jugador: Jugador)

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

    private val _parejas = listOf(listOf(0, 2), listOf(1, 3))
    private val _ganadorGrande = MutableStateFlow<Jugador?>(null)
    val ganadorGrande: StateFlow<Jugador?> = _ganadorGrande

    private val _musPedidos = MutableStateFlow(MutableList(4) { false })
    val musPedidos: StateFlow<List<Boolean>> = _musPedidos

    // Descarta cartas por ID
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
        _rondaActiva.value = true
        _turno.value = 0
        _mensajes.value = "Cartas repartidas. Es tu turno."
        _acciones.value = emptyList()
        _apuestaActual.value = null
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
        _ganadorGrande.value = null
        _rondaMusActiva.value = false
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
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Grande iniciada. Apostar y jugar."
        _turno.value = 0
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

    fun iniciarRondaMus() {
        _rondaMusActiva.value = true
        _rondaActiva.value = false
        _mensajes.value = "Ronda de Mus iniciada. Selecciona las cartas a descartar."
        _turno.value = 0
        _cartasDescartadas.value = List(4) { emptyList() } // Resetea descartes
    }


    private fun avanzarTurnoMus() {
        val siguiente = (_turno.value + 1) % 4
        if (siguiente == 0) {
            if (_musPedidos.value.all { it }) {
                _musPedidos.value = MutableList(4) { false }
                iniciarRondaMus()
            } else {
                _rondaActiva.value = true
                _mensajes.value = "Comienza la ronda de Grande"
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
            _ganadorGrande.value = _jugadores.value[idxGanador]
        }
    }

    private fun todosHanIgualado(): Boolean {
        val posUltimaSubida = _jugadorUltimaSubida.value ?: return false
        return _turno.value == posUltimaSubida
    }

    private fun finalizarApuesta() {
        _ganadorGrande.value = calcularGanadorGrande()
        _mensajes.value = "Apuesta finalizada. Ganador de Grande: ${_ganadorGrande.value?.nombre ?: "Nadie"}"
        _rondaActiva.value = false
    }
    private val _ganadorChica = MutableStateFlow<Jugador?>(null)
    val ganadorChica: StateFlow<Jugador?> = _ganadorChica

    fun iniciarRondaChica() {
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Chica iniciada. Apostar y jugar."
        _turno.value = 0
        // Resetea lo que necesites para la ronda de chica
    }

    fun finalizarApuestaChica() {
        _ganadorChica.value = calcularGanadorChica()
        _mensajes.value = "Ronda de Chica finalizada. Ganador de Chica: ${_ganadorChica.value?.nombre ?: "Nadie"}"
        _rondaActiva.value = false
        // Aquí puedes mostrar los ganadores
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
    fun calcularGanadorChica(): Jugador? {
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



    private fun fuerzaCarta(carta: Int): Int {
        // 3 y 12 son más altas, luego 11, 10, 7...1
        return when (carta) {
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
    }

    private fun fuerzaCartaChica(carta: Int): Int {
        // 1 y 2 equivalen y son lo más bajo (ganan), luego 4,5,6,7,10,11, y por último 3 y 12 empatados (pierden)
        return when (carta) {
            1, 2 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            7 -> 5
            10 -> 6
            11 -> 7
            3, 12 -> 8 // más altos en chica
            else -> 9
        }
    }

    private fun ordenarCartasChica(cartas: List<Int>): List<Int> {
        return cartas.sortedBy { fuerzaCartaChica(it) }
    }


    private fun ordenarCartasGrande(cartas: List<Int>): List<Int> {
        return cartas.sortedByDescending { fuerzaCarta(it) }
    }

    private fun compararCartasGrande(cartas1: List<Int>, cartas2: List<Int>): Int {
        val mano1 = ordenarCartasGrande(cartas1)
        val mano2 = ordenarCartasGrande(cartas2)
        for (i in mano1.indices) {
            val diff = fuerzaCarta(mano1.getOrNull(i) ?: 0) - fuerzaCarta(mano2.getOrNull(i) ?: 0)
            if (diff != 0) return diff
        }
        return 0
    }
    private fun compararCartasChica(cartas1: List<Int>, cartas2: List<Int>): Int {
        val mano1 = ordenarCartasChica(cartas1)
        val mano2 = ordenarCartasChica(cartas2)
        for (i in mano1.indices) {
            val diff = fuerzaCartaChica(mano1.getOrNull(i) ?: 0) - fuerzaCartaChica(mano2.getOrNull(i) ?: 0)
            if (diff != 0) return -diff // MENOR gana, así que invertimos el signo respecto a grande
        }
        return 0
    }


    fun reiniciar() {
        repartirCartas()
    }

}



