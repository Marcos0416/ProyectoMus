package com.example.appcarnavalextraordinaria.PartidasInteractivas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Jugador(val nombre: String, val esHumano: Boolean, val cartas: List<Int> = emptyList())

enum class Accion { PASAR, ENVIDAR, SUBIR, IGUALAR, RETIRARSE, MUS }

data class Apuesta(val cantidad: Int, val jugador: Jugador)

class MusGameViewModel : ViewModel() {
    private val _jugadores = MutableStateFlow(
        listOf(
            Jugador("Tú", true),
            Jugador("Bot 1", false),
            Jugador("Bot 2", false),
            Jugador("Bot 3", false)
        )
    )
    val jugadores: StateFlow<List<Jugador>> = _jugadores

    // Estado de turnos y apuestas
    private val _turno = MutableStateFlow(0)
    val turno: StateFlow<Int> = _turno

    private val _mensajes = MutableStateFlow("Comienza la partida. Pulsa repartir cartas.")
    val mensajes: StateFlow<String> = _mensajes

    private val _cartasRepartidas = MutableStateFlow(false)
    val cartasRepartidas: StateFlow<Boolean> = _cartasRepartidas

    private val _rondaActiva = MutableStateFlow(false)
    val rondaActiva: StateFlow<Boolean> = _rondaActiva

    private val _acciones = MutableStateFlow(listOf<String>())
    val acciones: StateFlow<List<String>> = _acciones

    private val _apuestaActual = MutableStateFlow<Apuesta?>(null)
    val apuestaActual: StateFlow<Apuesta?> = _apuestaActual

    private val _jugadoresActivos = MutableStateFlow(MutableList(4) { true }) // Activo en apuesta
    val jugadoresActivos: StateFlow<List<Boolean>> = _jugadoresActivos

    private val _jugadorUltimaSubida = MutableStateFlow<Int?>(null)
    val jugadorUltimaSubida: StateFlow<Int?> = _jugadorUltimaSubida

    private val _esperandoRespuesta = MutableStateFlow(false)
    val esperandoRespuesta: StateFlow<Boolean> = _esperandoRespuesta

    private val _parejas = listOf(listOf(0,2), listOf(1,3))
    private val _ganadorGrande = MutableStateFlow<Jugador?>(null)
    val ganadorGrande: StateFlow<Jugador?> = _ganadorGrande

    fun repartirCartas() {
        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }
        _jugadores.value = _jugadores.value.map {
            it.copy(cartas = List(4) { cartasValidas.random() })
        }
        _cartasRepartidas.value = true
        _rondaActiva.value = true
        _turno.value = 0
        _mensajes.value = "Cartas repartidas. Es tu turno."
        _acciones.value = emptyList()
        _apuestaActual.value = null
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
        _esperandoRespuesta.value = false
        _ganadorGrande.value = null
    }

    private fun fuerzaCarta(carta: Int): Int {
        return when (carta) {
            3 -> 14
            12 -> 13
            in 1..7 -> carta
            in 10..11 -> carta - 10 + 8
            else -> carta
        }
    }
    private fun ordenarCartasGrande(cartas: List<Int>) = cartas.sortedByDescending { fuerzaCarta(it) }
    private fun compararCartasGrande(cartas1: List<Int>, cartas2: List<Int>): Int {
        val mano1 = ordenarCartasGrande(cartas1)
        val mano2 = ordenarCartasGrande(cartas2)
        for (i in mano1.indices) {
            val diff = fuerzaCarta(mano1[i]) - fuerzaCarta(mano2[i])
            if (diff != 0) return diff
        }
        return 0 // Empate total
    }
    private fun calcularGanadorGrande(): Jugador? {
        var ganador: Jugador? = null
        var mejorMano: List<Int>? = null
        for (i in _jugadores.value.indices) {
            if (_jugadoresActivos.value[i]) {
                val cartasOrdenadas = ordenarCartasGrande(_jugadores.value[i].cartas)
                if (ganador == null || compararCartasGrande(cartasOrdenadas, mejorMano!!) > 0) {
                    ganador = _jugadores.value[i]
                    mejorMano = cartasOrdenadas
                }
            }
        }
        return ganador
    }

    fun realizarAccion(accion: Accion, cantidad: Int? = null) {
        if (!_rondaActiva.value || !_jugadoresActivos.value[_turno.value]) return
        val jugadorActual = _jugadores.value[_turno.value]
        val log = _acciones.value.toMutableList()
        when (accion) {
            Accion.ENVIDAR, Accion.SUBIR -> {
                val nuevaCantidad = cantidad ?: ((_apuestaActual.value?.cantidad ?: 2) + 1)
                _apuestaActual.value = Apuesta(nuevaCantidad, jugadorActual)
                _jugadorUltimaSubida.value = _turno.value
                _esperandoRespuesta.value = true
                log.add("${jugadorActual.nombre} sube la apuesta a $nuevaCantidad piedras")
            }
            Accion.IGUALAR -> {
                log.add("${jugadorActual.nombre} iguala la apuesta de ${_apuestaActual.value?.cantidad ?: 0} piedras")
                // Si todos han igualado tras última subida, termina
                if (todosHanIgualado()) finalizarApuesta()
            }
            Accion.PASAR -> {
                log.add("${jugadorActual.nombre} pasa")
                // Pasa, siguiente compañero decide o cierra apuesta si todos han pasado
                comprobarUnicoActivo()
            }
            Accion.RETIRARSE -> {
                log.add("${jugadorActual.nombre} se retira de la apuesta")
                _jugadoresActivos.value[_turno.value] = false
                // Si solo queda un equipo, estos ganan automáticamente
                comprobarUnicoActivo()
            }
            Accion.MUS -> {
                log.add("${jugadorActual.nombre} pide Mus")
            }
        }
        _acciones.value = log
        avanzarTurnoFlujoMus()
    }

    // Verifica si todos los adversarios han igualado la subida
    private fun todosHanIgualado(): Boolean {
        val activos = _jugadoresActivos.value
        val posUltimaSubida = _jugadorUltimaSubida.value ?: return false
        // Si todos los jugadores activos después del último que subió ya han respondido
        // (Puedes expandir para agregar un registro detallado de respuestas)
        // Aquí consideramos una única rotación: realista usar una cola de respuestas
        return _turno.value == posUltimaSubida
    }

    // Verifica si solo queda un equipo activo en la apuesta
    private fun soloQuedaUnEquipo(): Boolean {
        val activos = _jugadoresActivos.value
        val equipoA = _parejas[0].count { activos[it] }
        val equipoB = _parejas[1].count { activos[it] }
        return (equipoA == 0 && equipoB > 0) || (equipoB == 0 && equipoA > 0)
    }

    private fun finalizarApuesta() {
        _ganadorGrande.value = calcularGanadorGrande()
        _mensajes.value = "Apuesta finalizada. Ganador de Grande: ${_ganadorGrande.value?.nombre ?: "Nadie"}"
        _rondaActiva.value = false
    }

    // Flujo de avance según Mus real
    private fun avanzarTurnoFlujoMus() {
        val siguiente = (1..4).map { (_turno.value + it) % 4 }
            .firstOrNull { _jugadoresActivos.value[it] } // Primer activo
        if (siguiente != null && _rondaActiva.value) {
            _turno.value = siguiente
            val jugador = _jugadores.value[_turno.value]
            _mensajes.value = "Turno de ${jugador.nombre}"
            if (!jugador.esHumano) {
                viewModelScope.launch {
                    delay(1200)
                    // Bot decide acción válida según contexto
                    decidirAccionBot()
                }
            }
        } else {
            finalizarApuesta()
        }
    }

    // Bot toma decisión básica
    private fun decidirAccionBot() {
        val manoBot = ordenarCartasGrande(_jugadores.value[_turno.value].cartas)
        val fuerza = fuerzaCarta(manoBot[0]) // Carta más fuerte
        val apuestaActual = _apuestaActual.value?.cantidad ?: 2

        // Define umbrales según tu juego (puedes ajustar)
        when {
            fuerza >= 12 -> realizarAccion(Accion.SUBIR, apuestaActual + 2)
            fuerza in 10..11 -> realizarAccion(Accion.IGUALAR)
            fuerza in 7..9 -> realizarAccion(Accion.PASAR)
            else -> realizarAccion(Accion.RETIRARSE)
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


    fun reiniciar() = repartirCartas()
}

