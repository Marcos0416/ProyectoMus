package com.example.appcarnavalextraordinaria.PartidasInteractivas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.appcarnavalextraordinaria.Data.MovimientoDao
import com.example.appcarnavalextraordinaria.Data.MovimientoEntity
import com.example.appcarnavalextraordinaria.Data.PartidaDao
import com.example.appcarnavalextraordinaria.Data.PartidaEntity
import com.example.appcarnavalextraordinaria.Data.ProgressDao
import com.example.appcarnavalextraordinaria.Data.ProgressEntity
import com.example.appcarnavalextraordinaria.Data.UserDao
import com.example.appcarnavalextraordinaria.Login.UserViewModel
import kotlinx.coroutines.launch

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

class MusGameViewModel(
    private val userDao: UserDao,
    private val partidaDao: PartidaDao,
    private val movimientoDao: MovimientoDao,
    private val progressDao: ProgressDao,
    private val currentUserId: Int,
    private val currentUsername: String
) : ViewModel() {
    private var siguienteIdCarta = 0
    private var jugadorInicioPartida = 0
    private var partidaIdActual: Int? = null

    private val _jugadores = MutableStateFlow(
        listOf(
            Jugador(currentUsername, true),
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

    private val _rondaParesActiva = MutableStateFlow(false)
    val rondaParesActiva: StateFlow<Boolean> = _rondaParesActiva

    private val _rondaJuegoActiva = MutableStateFlow(false)
    val rondaJuegoActiva: StateFlow<Boolean> = _rondaJuegoActiva

    private val _rondaActual = MutableStateFlow("grande")
    val rondaActual: StateFlow<String> = _rondaActual

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

    private val _ganadorJuego = MutableStateFlow<Pair<Jugador, Jugador>?>(null)
    val ganadorJuego: StateFlow<Pair<Jugador, Jugador>?> = _ganadorJuego

    private val _resultadosPares = MutableStateFlow<List<ResultadoPares>>(emptyList())
    val resultadosPares: StateFlow<List<ResultadoPares>> = _resultadosPares

    private val _jugadoresConPares = MutableStateFlow<List<Boolean>>(emptyList())
    val jugadoresConPares: StateFlow<List<Boolean>> = _jugadoresConPares

    private val _puntuacionesJuego = MutableStateFlow<List<Int>>(emptyList())
    val puntuacionesJuego: StateFlow<List<Int>> = _puntuacionesJuego

    private val _cartasDescartadas = MutableStateFlow(List(4) { emptyList<Int>() })
    val cartasDescartadas: StateFlow<List<List<Int>>> = _cartasDescartadas

    private val _pareja1Puntos = MutableStateFlow(0)
    val pareja1Puntos: StateFlow<Int> = _pareja1Puntos

    private val _pareja2Puntos = MutableStateFlow(0)
    val pareja2Puntos: StateFlow<Int> = _pareja2Puntos

    private val _partidaTerminada = MutableStateFlow(false)
    val partidaTerminada: StateFlow<Boolean> = _partidaTerminada

    private val _resultadoPartida = MutableStateFlow("")
    val resultadoPartida: StateFlow<String> = _resultadoPartida


    // Definir parejas (0-1 y 2-3)
    private val parejas = listOf(
        listOf(0, 1), // Tú y Bot 1
        listOf(2, 3)  // Bot 2 y Bot 3
    )

    // ============================= FUNCIONES DE BASE DE DATOS =============================

    private fun iniciarPartidaDB() {
        viewModelScope.launch {
            val partida = PartidaEntity(
                createdBy = currentUserId,
                resultado = "",
                fecha = System.currentTimeMillis()
            )
            partidaIdActual = partidaDao.insertPartida(partida).toInt()
            Log.d("DEBUG", "Creando partida con userId=${currentUserId}")

        }
    }

    private fun registrarMovimiento(accion: Accion, cantidad: Int? = null) {
        viewModelScope.launch {
            partidaIdActual?.let {
                // Obtener el userId del jugador actual (solo para jugador humano)
                val userId = if (_turno.value == 0) currentUserId else -1 // -1 para bots

                val movimiento = MovimientoEntity(
                    partidaId = it,
                    userId = userId,
                    ronda = _rondaActual.value,
                    accion = accion.name,
                    cantidad = cantidad
                )
                movimientoDao.insertMovimiento(movimiento)
            }
        }
    }

    private fun guardarResultadoPartida(resultado: String) {
        viewModelScope.launch {
            _resultadoPartida.value = resultado // ← Actualiza inmediatamente
            partidaIdActual?.let { id ->
                val partida = partidaDao.getPartidaById(id)
                partida?.let {
                    val partidaActualizada = it.copy(resultado = resultado)
                    partidaDao.updatePartida(partidaActualizada)
                }
            }
        }
    }



    private fun actualizarProgresoJugador() {
        viewModelScope.launch {
            val puntos = _pareja1Puntos.value ?: 0
            val progress = ProgressEntity(
                userId = currentUserId,     // <--- el ID del usuario que está jugando
                tutorialStep = 1,           // o el paso actual del tutorial
                score = puntos,
                lastAccess = System.currentTimeMillis()
            )
            progressDao.insertProgress(progress)
        }
    }







    // ============================= FUNCIONES PRINCIPALES DEL JUEGO =============================

    fun repartirCartas() {
        if (partidaIdActual == null)
        {iniciarPartidaDB()
            Log.d("Mus", "Partida DB iniciada con id: " +
                    "$partidaIdActual")}

        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }
        siguienteIdCarta = 0
        _jugadores.value = _jugadores.value.map {
            it.copy(cartas = List(4) {
                Carta(siguienteIdCarta++, cartasValidas.random())
            })
        }
        _cartasRepartidas.value = true
        _rondaActual.value = "grande"
        _rondaActiva.value = true
        _rondaMusActiva.value = false
        _rondaParesActiva.value = false
        _rondaJuegoActiva.value = false

        jugadorInicioPartida = (jugadorInicioPartida + 1) % 4
        Log.d("Mus", "Inicio partida, empieza jugador: " +
                "${_jugadores.value[jugadorInicioPartida].nombre}")

        _turno.value = jugadorInicioPartida
        _mensajes.value = "Cartas repartidas. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}."
        _acciones.value = emptyList()
        _apuestaActual.value = null
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
        _ganadorGrande.value = null
        _ganadorChica.value = null
        _ganadorPares.value = null
        _ganadorJuego.value = null
        _resultadosPares.value = emptyList()
        _jugadoresConPares.value = emptyList()
        _puntuacionesJuego.value = emptyList()
        _cartasDescartadas.value = List(4) { emptyList() }
        _musPedidos.value = MutableList(4) { false }

        Log.d("MusGame", "Estado inicial " +
                "de la partida configurado correctamente.")

        // NO resetear _pareja1Puntos y _pareja2Puntos aquí
    }

    // Añade esta función para reiniciar completamente (solo cuando un equipo llega a 40)
    fun reiniciarPartidaCompleta() {
        _pareja1Puntos.value = 0
        _pareja2Puntos.value = 0
        partidaIdActual = null
        _resultadoPartida.value = "" // Resetear el resultado
        repartirCartas()
    }

    // Esta función solo para nueva mano, no reinicia marcador
    fun nuevaMano() {
        repartirCartas()
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
        _rondaActual.value = "grande"
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Grande iniciada. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}."
        _turno.value = jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
    }

    fun iniciarRondaChica() {
        _rondaActual.value = "chica"
        _rondaActiva.value = true
        _mensajes.value = "Ronda de Chica iniciada. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}."
        _turno.value = jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadoresActivos.value = MutableList(4) { true }
        _jugadorUltimaSubida.value = null
    }

    fun iniciarRondaPares() {
        _rondaActual.value = "pares"
        _rondaActiva.value = true
        _rondaParesActiva.value = true
        _mensajes.value = "Ronda de Pares iniciada. Verificando pares..."

        // Verificar qué jugadores tienen pares
        val resultados = verificarPares()
        _resultadosPares.value = resultados

        // Determinar qué jugadores tienen pares
        val conPares = resultados.map { it.combinacion !is SinPares }
        _jugadoresConPares.value = conPares

        // Verificar qué parejas tienen pares
        val pareja1TienePares = conPares[0] || conPares[1]  // Tú y Bot 1
        val pareja2TienePares = conPares[2] || conPares[3]  // Bot 2 y Bot 3

        // Si solo una pareja tiene pares, ganan automáticamente
        if (pareja1TienePares && !pareja2TienePares) {
            _ganadorPares.value = _jugadores.value[0] to _jugadores.value[1]
            _mensajes.value = "¡Tú y Bot 1 ganan automáticamente los pares! La otra pareja no tiene pares."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaParesConGanadorAutomatico()
            }, 2000)
            return
        } else if (!pareja1TienePares && pareja2TienePares) {
            _ganadorPares.value = _jugadores.value[2] to _jugadores.value[3]
            _mensajes.value = "¡Bot 2 y Bot 3 ganan automáticamente los pares! La otra pareja no tiene pares."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaParesConGanadorAutomatico()
            }, 2000)
            return
        } else if (!pareja1TienePares && !pareja2TienePares) {
            _mensajes.value = "Ninguna pareja tiene pares. Pasando a siguiente ronda..."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaParesSinGanador()
            }, 2000)
            return
        }

        // Ambas parejas tienen pares - proceder con apuestas normales
        // Solo los jugadores con pares pueden participar
        _jugadoresActivos.value = conPares.toMutableList()

        // Encontrar el primer jugador activo (con pares) empezando desde jugadorInicioPartida
        var primerJugadorActivo = -1
        for (i in 0 until 4) {
            val index = (jugadorInicioPartida + i) % 4
            if (conPares[index]) {
                primerJugadorActivo = index
                break
            }
        }

        _turno.value = if (primerJugadorActivo != -1) primerJugadorActivo else jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadorUltimaSubida.value = null

        _mensajes.value = "Ambas parejas tienen pares. Comienza ${_jugadores.value[_turno.value].nombre}"
    }

    fun iniciarRondaJuego() {
        _rondaActual.value = "juego"
        _rondaActiva.value = true
        _rondaJuegoActiva.value = true
        _mensajes.value = "Ronda de Juego iniciada. Calculando puntuaciones..."

        // Calcular puntuaciones de todos los jugadores
        val puntuaciones = _jugadores.value.map { calcularPuntuacionJuego(it.cartas.map { carta -> carta.valor }) }
        _puntuacionesJuego.value = puntuaciones

        // Determinar qué jugadores tienen juego (31 o más)
        val conJuego = puntuaciones.map { it >= 31 }

        // Verificar qué parejas tienen juego
        val pareja1TieneJuego = conJuego[0] || conJuego[1]  // Tú y Bot 1
        val pareja2TieneJuego = conJuego[2] || conJuego[3]  // Bot 2 y Bot 3

        // Si solo una pareja tiene juego, ganan automáticamente y termina la partida
        if (pareja1TieneJuego && !pareja2TieneJuego) {
            _ganadorJuego.value = _jugadores.value[0] to _jugadores.value[1]
            _mensajes.value = "¡Tú y Bot 1 ganan automáticamente el juego! La otra pareja no tiene juego."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaJuegoConGanadorAutomatico()
            }, 2000)
            return
        } else if (!pareja1TieneJuego && pareja2TieneJuego) {
            _ganadorJuego.value = _jugadores.value[2] to _jugadores.value[3]
            _mensajes.value = "¡Bot 2 y Bot 3 ganan automáticamente el juego! La otra pareja no tiene juego."
            Handler(Looper.getMainLooper()).postDelayed({
                finalizarRondaJuegoConGanadorAutomatico()
            }, 2000)
            return
        }

        // Verificar si hay jugadores con juego
        val hayJugadoresConJuego = conJuego.any { it }

        if (!hayJugadoresConJuego) {
            // Si nadie tiene juego, todos juegan al punto
            _jugadoresActivos.value = MutableList(4) { true }
            _mensajes.value = "Ningún jugador tiene juego. Se juega al punto. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}"
        } else {
            // Si ambas parejas tienen juego, solo los jugadores con juego pueden participar
            _jugadoresActivos.value = conJuego.toMutableList()
            _mensajes.value = "Ambas parejas tienen juego. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}"
        }

        // Encontrar el primer jugador activo empezando desde jugadorInicioPartida
        var primerJugadorActivo = -1
        for (i in 0 until 4) {
            val index = (jugadorInicioPartida + i) % 4
            if (_jugadoresActivos.value[index]) {
                primerJugadorActivo = index
                break
            }
        }

        _turno.value = if (primerJugadorActivo != -1) primerJugadorActivo else jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadorUltimaSubida.value = null
    }

    fun realizarAccion(accion: Accion, cantidad: Int? = null) {
        // Solo en la ronda de pares verificamos restricciones
        if (_rondaActual.value == "pares") {
            val pareja1TienePares = _jugadoresConPares.value[0] || _jugadoresConPares.value[1]
            val pareja2TienePares = _jugadoresConPares.value[2] || _jugadoresConPares.value[3]

            // Si solo una pareja tiene pares, no se permiten acciones (ya ganaron automáticamente)
            if ((pareja1TienePares && !pareja2TienePares) || (!pareja1TienePares && pareja2TienePares)) {
                _mensajes.value = "La ronda de pares ya tiene ganadores automáticos. No se permiten más acciones."
                return
            }

            // Si el jugador actual no tiene pares, no puede actuar en la ronda de pares
            if (!_jugadoresConPares.value[_turno.value]) {
                _mensajes.value = "${_jugadores.value[_turno.value].nombre} no tiene pares y no puede actuar en esta ronda."
                avanzarTurno()
                return
            }
        }

        // En la ronda de juego, si hay jugadores con juego, solo ellos pueden actuar
        if (_rondaActual.value == "juego") {
            val puntuaciones = _puntuacionesJuego.value
            val hayJugadoresConJuego = puntuaciones.any { it >= 31 }

            if (hayJugadoresConJuego) {
                val tieneJuego = puntuaciones[_turno.value] >= 31
                if (!tieneJuego) {
                    _mensajes.value = "${_jugadores.value[_turno.value].nombre} no tiene juego y no puede actuar en esta ronda."
                    avanzarTurno()
                    return
                }
            }
            // Si no hay jugadores con juego, todos pueden participar (juego al punto)
        }

        // Para Grande y Chica, todos los jugadores activos pueden participar
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
                registrarMovimiento(accion, incremento)
            }
            Accion.IGUALAR -> {
                log.add("${jugadorActual.nombre} iguala la apuesta de ${_apuestaActual.value?.cantidad ?: 0} piedras")
                registrarMovimiento(accion, _apuestaActual.value?.cantidad)
                if (todosHanIgualado()) finalizarApuesta()
            }
            Accion.PASAR -> {
                log.add("${jugadorActual.nombre} pasa")
                registrarMovimiento(accion)
                comprobarUnicoActivo()
            }
            Accion.RETIRARSE -> {
                log.add("${jugadorActual.nombre} se retira de la apuesta")
                registrarMovimiento(accion)
                _jugadoresActivos.value[_turno.value] = false
                comprobarUnicoActivo()
            }
            Accion.MUS -> {
                log.add("${jugadorActual.nombre} pide Mus")
                registrarMovimiento(accion)
                val musPedidosMutable = _musPedidos.value.toMutableList()
                musPedidosMutable[_turno.value] = true
                _musPedidos.value = musPedidosMutable
            }
        }
        _acciones.value = log
        avanzarTurno()
    }

    private fun avanzarTurno() {
        when (_rondaActual.value) {
            "pares" -> {
                // En pares, solo avanzar a jugadores con pares
                var siguiente = (_turno.value + 1) % 4
                var intentos = 0
                while (intentos < 4) {
                    if (_jugadoresConPares.value[siguiente] && _jugadoresActivos.value[siguiente]) {
                        _turno.value = siguiente
                        _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"
                        return
                    }
                    siguiente = (siguiente + 1) % 4
                    intentos++
                }
            }
            "juego" -> {
                val puntuaciones = _puntuacionesJuego.value
                val hayJugadoresConJuego = puntuaciones.any { it >= 31 }

                if (hayJugadoresConJuego) {
                    // En juego, solo avanzar a jugadores con juego
                    var siguiente = (_turno.value + 1) % 4
                    var intentos = 0
                    while (intentos < 4) {
                        if (puntuaciones[siguiente] >= 31 && _jugadoresActivos.value[siguiente]) {
                            _turno.value = siguiente
                            _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"
                            return
                        }
                        siguiente = (siguiente + 1) % 4
                        intentos++
                    }
                } else {
                    // Si no hay juego, todos pueden participar (punto)
                    val siguiente = (_turno.value + 1) % 4
                    _turno.value = siguiente
                    _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"

                    if (siguiente == jugadorInicioPartida) {
                        if (_musPedidos.value.all { it }) {
                            _musPedidos.value = MutableList(4) { false }
                            iniciarRondaMus()
                        }
                    }
                }
            }
            else -> {
                // Para GRANDE y CHICA, comportamiento normal - todos pueden participar
                val siguiente = (_turno.value + 1) % 4
                _turno.value = siguiente
                _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"

                if (siguiente == jugadorInicioPartida) {
                    if (_musPedidos.value.all { it }) {
                        _musPedidos.value = MutableList(4) { false }
                        iniciarRondaMus()
                    }
                }
            }
        }
    }

    private fun comprobarUnicoActivo() {
        val activos = _jugadoresActivos.value
        val quedan = activos.count { it }
        if (quedan == 1) {
            val idxGanador = activos.indexOfFirst { it }
            _rondaActiva.value = false
            _mensajes.value = "¡${_jugadores.value[idxGanador].nombre} gana la apuesta porque todos han pasado o se han retirado!"
            when (_rondaActual.value) {
                "grande" -> _ganadorGrande.value = _jugadores.value[idxGanador]
                "chica" -> _ganadorChica.value = _jugadores.value[idxGanador]
                "pares" -> {
                    // En pares, el ganador es la pareja completa
                    val parejaIndex = if (idxGanador <= 1) 0 else 1
                    _ganadorPares.value = _jugadores.value[parejas[parejaIndex][0]] to _jugadores.value[parejas[parejaIndex][1]]
                }
                "juego" -> {
                    // En juego, el ganador es la pareja completa
                    val parejaIndex = if (idxGanador <= 1) 0 else 1
                    _ganadorJuego.value = _jugadores.value[parejas[parejaIndex][0]] to _jugadores.value[parejas[parejaIndex][1]]
                }
            }
            pasarApuestaOSiguienteRonda()
        }
    }

    private fun todosHanIgualado(): Boolean {
        val posUltimaSubida = _jugadorUltimaSubida.value ?: return false
        return _turno.value == posUltimaSubida
    }



    // 🪙 Asignar puntos a la pareja ganadora
    private fun asignarPuntosRonda(ganador: Jugador?, puntos: Int) {
        if (ganador == null) return
        // Usar el nombre real del usuario para la comparación
        val parejaIndex = if (ganador.nombre == currentUsername || ganador.nombre == "Bot 1") 0 else 1
        if (parejaIndex == 0) _pareja1Puntos.value += puntos else _pareja2Puntos.value += puntos
    }

    private fun asignarPuntosPareja(pareja: Pair<Jugador, Jugador>, puntos: Int) {
        // Usar el nombre real del usuario para la comparación
        val parejaIndex = if (pareja.first.nombre == currentUsername || pareja.second.nombre == currentUsername) 0 else 1
        if (parejaIndex == 0) _pareja1Puntos.value += puntos else _pareja2Puntos.value += puntos
    }

    // 🎲 Calcular puntos de pares según combinaciones (suma para la pareja ganadora)
    private fun calcularPuntosPares(ganadores: Pair<Jugador, Jugador>): Int {
        // Buscar los resultados de pares correspondientes a los dos jugadores de la pareja ganadora
        val resultadosPareja = _resultadosPares.value.filter { resultado ->
            resultado.jugador == ganadores.first || resultado.jugador == ganadores.second
        }

        var total = 0
        resultadosPareja.forEach { resultado ->
            total += when (resultado.combinacion) {
                is Par -> 1
                is Medias -> 2
                is Duples -> 3
                else -> 0
            }
        }
        return total
    }

    // 🎮 Calcular puntos en juego para la pareja (suma por jugador: 3 si 31, 2 si tiene juego >=31, 0 si no)
    private fun calcularPuntosJuego(ganadores: Pair<Jugador, Jugador>): Int {
        val puntuaciones = _puntuacionesJuego.value

        val idx1 = _jugadores.value.indexOf(ganadores.first)
        val idx2 = _jugadores.value.indexOf(ganadores.second)

        val p1 = puntuaciones.getOrNull(idx1) ?: 0
        val p2 = puntuaciones.getOrNull(idx2) ?: 0

        fun puntosPorJugador(p: Int): Int {
            return when {
                p == 31 -> 3
                p >= 31 -> 2
                else -> 0
            }
        }

        return puntosPorJugador(p1) + puntosPorJugador(p2)
    }
    private fun guardarPartidaCompleta() {
        viewModelScope.launch {
            val puntosPareja1 = _pareja1Puntos.value
            val puntosPareja2 = _pareja2Puntos.value

            // Determinar ganador
            val ganadores = if (puntosPareja1 >= 40) {
                "Ganadores: Pareja 1 ($puntosPareja1-$puntosPareja2)"
            } else {
                "Ganadores: Pareja 2 ($puntosPareja1-$puntosPareja2)"
            }

            // Crear la partida con toda la información
            val partida = PartidaEntity(
                createdBy = currentUserId,
                resultado = ganadores
            )

            partidaDao.insertPartida(partida)
            Log.d("PARTIDA", "Partida guardada: $ganadores")
        }
    }

    // 🏁 Comprobar si una pareja llega a 40 puntos
    private fun comprobarFinDePartida() {
        val puntosPareja1 = _pareja1Puntos.value
        val puntosPareja2 = _pareja2Puntos.value

        when {
            puntosPareja1 >= 40 -> {
                _mensajes.value = "🎉 ¡${_jugadores.value[0].nombre} y ${_jugadores.value[1].nombre} habéis ganado la partida!"
                _rondaActiva.value = false

                val resultado = "Ganadores: Pareja 1 (${puntosPareja1} - ${puntosPareja2})"
                guardarResultadoPartida(resultado)

                _partidaTerminada.value = true   // ← INDICA QUE TERMINÓ LA PARTIDA
            }

            puntosPareja2 >= 40 -> {
                _mensajes.value = "💀 ¡${_jugadores.value[2].nombre} y ${_jugadores.value[3].nombre} han ganado la partida!"
                _rondaActiva.value = false

                val resultado = "Ganadores: Pareja 2 (${puntosPareja1} - ${puntosPareja2})"
                guardarResultadoPartida(resultado)

                _partidaTerminada.value = true   // ← INDICA QUE TERMINÓ LA PARTIDA
            }
        }
    }


    private fun pasarApuestaOSiguienteRonda() {
        // Obtener la apuesta final de la ronda (embite)
        val apuesta = _apuestaActual.value?.cantidad ?: 0
        val esPaso = apuesta == 0

        when (_rondaActual.value) {
            "grande" -> {
                val ganador = _ganadorGrande.value
                if (ganador != null) {
                    val puntos = if (esPaso) 1 else apuesta
                    asignarPuntosRonda(ganador, puntos)
                }
            }
            "chica" -> {
                val ganador = _ganadorChica.value
                if (ganador != null) {
                    val puntos = if (esPaso) 1 else apuesta
                    asignarPuntosRonda(ganador, puntos)
                }
            }
            "pares" -> {
                val ganadores = _ganadorPares.value
                if (ganadores != null) {
                    val extra = calcularPuntosPares(ganadores) // puntos por pares de la pareja
                    val total = if (esPaso) {
                        // en paso en pares: se lleva lo calculado por los pares (si no hubiera puntos, podríamos dar 1)
                        if (extra > 0) extra else 1
                    } else {
                        // si hay embite: se lleva embite + puntos de pares
                        apuesta + extra
                    }
                    asignarPuntosPareja(ganadores, total)
                }
            }
            "juego" -> {
                val ganadores = _ganadorJuego.value
                if (ganadores != null) {
                    val extra = calcularPuntosJuego(ganadores) // suma 2/3 por jugador según tenga juego/31
                    val total = if (esPaso) {
                        // si nadie envidó, se llevan los puntos de juego (2/3 por jugador)
                        if (extra > 0) extra else 1
                    } else {
                        // si hay embite: embite + puntos de juego
                        apuesta + extra
                    }
                    asignarPuntosPareja(ganadores, total)
                }
            }
        }

        // 🏁 Comprobar si alguien llegó a 40
        comprobarFinDePartida()

        // 🔄 Avanzar de ronda
        when (_rondaActual.value) {
            "grande" -> iniciarRondaChica()
            "chica" -> iniciarRondaPares()
            "pares" -> iniciarRondaJuego()
            "juego" -> {
                if (_ganadorJuego.value == null) {
                    _mensajes.value = "Ronda de Juego finalizada. Partida terminada."
                    guardarResultadoPartida("Partida completada (${_pareja1Puntos.value} - ${_pareja2Puntos.value})")
                    actualizarProgresoJugador()
                }
                _rondaJuegoActiva.value = false
            }
        }

        // Reset de apuesta para la siguiente ronda
        _apuestaActual.value = null
        _jugadorUltimaSubida.value = null
    }

    fun iniciarRondaMus() {
        _rondaMusActiva.value = true
        _rondaActiva.value = false
        _mensajes.value = "Ronda de Mus iniciada. Selecciona las cartas a descartar."
        _turno.value = 0
        _cartasDescartadas.value = List(4) { emptyList() }
    }

    fun reiniciar() {

        repartirCartas()
    }

    // ============================= FUNCIONES AUXILIARES DEL JUEGO =============================

    private fun normalizarValorCarta(valor: Int): Int {
        return when (valor) {
            12, 3 -> 3  // Rey = 3
            1, 2  -> 1  // As = 1
            else -> valor
        }
    }

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

    // ----------- PARES -----------
    private fun verificarPares(): List<ResultadoPares> {
        return _jugadores.value.map { jugador ->
            val combinacion = detectarCombinacionPares(jugador.cartas.map { it.valor })
            val valorCombinacion = calcularValorCombinacion(combinacion)
            ResultadoPares(combinacion, jugador, valorCombinacion)
        }
    }

    private fun detectarCombinacionPares(cartas: List<Int>): CombinacionPares {
        // Normalizar las cartas (3 y 12 son iguales)
        val cartasNormalizadas = cartas.map { normalizarValorCarta(it) }

        val conteo = cartasNormalizadas.groupingBy { it }.eachCount()

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
        3 -> 13  // Reyes (tanto 3 como 12)
        11 -> 12 // Caballos
        10 -> 11 // Sotas
        7 -> 10  // Sietes
        6 -> 9   // Seis
        5 -> 8   // Cincos
        4 -> 7   // Cuatros
        2 -> 6   // Dos
        1 -> 5   // Ases
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

    // ----------- JUEGO -----------
    private fun calcularPuntuacionJuego(cartas: List<Int>): Int {
        return cartas.sumOf { carta ->
            when (carta) {
                1, 2 -> 1 // Ases y doses valen 1
                in 4..7 -> carta // 4, 5, 6, 7 valen su valor
                10, 11 -> 10 // Sotas y caballos valen 10
                3, 12 -> 10 // Reyes valen 10
                else -> 0 // No debería haber otros valores
            }
        }
    }

    private fun calcularValorJuego(puntuacion: Int): Int {
        return when (puntuacion) {
            31 -> 100 // El mejor
            32 -> 90
            40 -> 80
            37 -> 70
            36 -> 60
            35 -> 50
            34 -> 40
            33 -> 30
            in 0..30 -> puntuacion // Para punto, valor directo
            else -> 0
        }
    }

    private fun calcularGanadorJuego(): Pair<Jugador, Jugador>? {
        val puntuaciones = _puntuacionesJuego.value
        val hayJugadoresConJuego = puntuaciones.any { it >= 31 }

        // Calcular la mejor puntuación por pareja
        val mejorPareja1 = if (hayJugadoresConJuego) {
            // Si hay juego, solo considerar jugadores con juego
            puntuaciones
                .filterIndexed { index, _ -> index <= 1 }
                .filter { it >= 31 }
                .maxOrNull() ?: -1
        } else {
            // Si no hay juego, considerar todos los jugadores (punto)
            puntuaciones
                .filterIndexed { index, _ -> index <= 1 }
                .maxOrNull() ?: -1
        }

        val mejorPareja2 = if (hayJugadoresConJuego) {
            puntuaciones
                .filterIndexed { index, _ -> index >= 2 }
                .filter { it >= 31 }
                .maxOrNull() ?: -1
        } else {
            puntuaciones
                .filterIndexed { index, _ -> index >= 2 }
                .maxOrNull() ?: -1
        }

        // Si una pareja no tiene jugadores con juego (cuando hay juego) o no tiene puntos (cuando no hay juego), la otra gana automáticamente
        if (hayJugadoresConJuego) {
            if (mejorPareja1 == -1 && mejorPareja2 == -1) return null
            if (mejorPareja1 == -1) return _jugadores.value[2] to _jugadores.value[3]
            if (mejorPareja2 == -1) return _jugadores.value[0] to _jugadores.value[1]
        } else {
            if (mejorPareja1 == -1 && mejorPareja2 == -1) return null
            if (mejorPareja1 == -1) return _jugadores.value[2] to _jugadores.value[3]
            if (mejorPareja2 == -1) return _jugadores.value[0] to _jugadores.value[1]
        }

        // Comparar las mejores puntuaciones de cada pareja
        val valorPareja1 = calcularValorJuego(mejorPareja1)
        val valorPareja2 = calcularValorJuego(mejorPareja2)

        val comparacion = valorPareja1 - valorPareja2

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
        _rondaActiva.value = false
        _rondaParesActiva.value = false
        _mensajes.value = if (_ganadorPares.value != null) {
            "Ganadores automáticos de Pares: ${_ganadorPares.value!!.first.nombre} y ${_ganadorPares.value!!.second.nombre}"
        } else {
            "Ronda de Pares finalizada sin ganadores"
        }
        pasarApuestaOSiguienteRonda()
    }

    private fun finalizarRondaJuegoConGanadorAutomatico() {
        _rondaActiva.value = false
        _rondaJuegoActiva.value = false
        _mensajes.value = if (_ganadorJuego.value != null) {
            "Ganadores automáticos de Juego: ${_ganadorJuego.value!!.first.nombre} y ${_ganadorJuego.value!!.second.nombre}"
        } else {
            "Ronda de Juego finalizada sin ganadores"
        }
        // En juego, si hay ganadores automáticos, la partida termina inmediatamente
        _mensajes.value = "Partida terminada. ${_mensajes.value}"
        guardarResultadoPartida("Ronda completada (${_pareja1Puntos.value} - ${_pareja2Puntos.value})")
        actualizarProgresoJugador()
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
    private fun finalizarApuesta() {
        when (_rondaActual.value) {
            "grande" -> _ganadorGrande.value = calcularGanadorGrande()
            "chica" -> _ganadorChica.value = calcularGanadorChica()
            "pares" -> _ganadorPares.value = calcularGanadorPares()
            "juego" -> _ganadorJuego.value = calcularGanadorJuego()
        }
        _rondaActiva.value = false
        _mensajes.value = when (_rondaActual.value) {
            "grande" -> "Apuesta finalizada. Ganador de Grande: ${_ganadorGrande.value?.nombre ?: "Nadie"}"
            "chica" -> "Apuesta finalizada. Ganador de Chica: ${_ganadorChica.value?.nombre ?: "Nadie"}"
            "pares" -> {
                val ganadores = _ganadorPares.value
                "Apuesta finalizada. Ganadores de Pares: ${ganadores?.first?.nombre ?: "Nadie"} y ${ganadores?.second?.nombre ?: "Nadie"}"
            }
            "juego" -> {
                val ganadores = _ganadorJuego.value
                val puntuaciones = _puntuacionesJuego.value
                val tieneJuego = puntuaciones.any { it >= 31 }

                if (tieneJuego) {
                    "Apuesta finalizada. Ganadores de Juego: ${ganadores?.first?.nombre ?: "Nadie"} y ${ganadores?.second?.nombre ?: "Nadie"}"
                } else {
                    "Apuesta finalizada. Ganadores del Punto: ${ganadores?.first?.nombre ?: "Nadie"} y ${ganadores?.second?.nombre ?: "Nadie"}"
                }
            }
            else -> ""
        }
        pasarApuestaOSiguienteRonda()
    }
    fun reiniciarPartidaManual() {
        _partidaTerminada.value = false
        reiniciarPartidaCompleta()
    }



}