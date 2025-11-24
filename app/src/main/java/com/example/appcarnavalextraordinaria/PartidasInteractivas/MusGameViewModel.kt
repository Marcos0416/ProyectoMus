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

import com.example.appcarnavalextraordinaria.Data.UserDao
import com.example.appcarnavalextraordinaria.Login.UserViewModel
import kotlinx.coroutines.launch



// Representa una carta con un id único interno y un valor (para las reglas de Mus)
data class Carta(val id: Int, val valor: Int)

// Representa un jugador: nombre, si es humano o bot y sus cartas actuales
data class Jugador(
    val nombre: String,
    val esHumano: Boolean,
    val cartas: List<Carta> = emptyList()
)

// Posibles acciones que se pueden hacer en una apuesta de Mus
enum class Accion { PASAR, ENVIDAR, SUBIR, IGUALAR, RETIRARSE, MUS }

// Representa una apuesta concreta: cantidad apostada y el jugador que la hace
data class Apuesta(val cantidad: Int, val jugador: Jugador)

// ---------------------- COMBINACIONES DE PARES ----------------------

// Jerarquía sellada para representar los tipos de pares en Mus
sealed class CombinacionPares
object SinPares : CombinacionPares()
data class Par(val valorCarta: Int) : CombinacionPares()
data class Medias(val valorCarta: Int) : CombinacionPares()
data class Duples(val valorCarta1: Int, val valorCarta2: Int) : CombinacionPares()

// Resultado de pares para un jugador concreto
data class ResultadoPares(
    val combinacion: CombinacionPares, // Tipo de combinación (sin pares, par, medias, duples...)
    val jugador: Jugador,              // Jugador al que pertenece la combinación
    val valorCombinacion: Int = 0      // Valor numérico usado para comparar quién gana
)

// ViewModel que controla toda la lógica de la partida de Mus
class MusGameViewModel(
    private val userDao: UserDao,             // DAO para acceder a usuarios si hace falta
    private val partidaDao: PartidaDao,       // DAO para guardar/leer partidas en la BD
    private val movimientoDao: MovimientoDao, // DAO para registrar movimientos/apuestas
    private val currentUserId: Int,           // ID del usuario humano actual
    private val currentUsername: String       // Nombre del usuario humano actual
) : ViewModel() {

    // Contador interno para ir generando IDs únicos de cartas (si se usan)
    private var siguienteIdCarta = 0

    // Índice (0-3) del jugador que empieza la partida
    private var jugadorInicioPartida = 0

    // ID de la partida actual en la BD (null mientras no se ha creado)
    private var partidaIdActual: Int? = null

    // ---------------------- ESTADO PRINCIPAL DE LA PARTIDA ----------------------

    // Lista de jugadores: el humano y tres bots
    private val _jugadores = MutableStateFlow(
        listOf(
            Jugador(currentUsername, true),   // Posición 0: jugador humano
            Jugador("Bot 1", false),          // Posición 1
            Jugador("Bot 2", false),          // Posición 2
            Jugador("Bot 3", false)           // Posición 3
        )
    )
    val jugadores: StateFlow<List<Jugador>> = _jugadores // Expuesto como solo lectura a la UI

    // Índice del jugador al que le toca el turno (0..3)
    private val _turno = MutableStateFlow(0)
    val turno: StateFlow<Int> = _turno

    // Mensajes informativos que se muestran en la UI (por ejemplo, "Comienza la partida...")
    private val _mensajes = MutableStateFlow("Comienza la partida. Pulsa repartir cartas.")
    val mensajes: StateFlow<String> = _mensajes

    // Indica si ya se han repartido las cartas de la ronda actual
    private val _cartasRepartidas = MutableStateFlow(false)
    val cartasRepartidas: StateFlow<Boolean> = _cartasRepartidas

    // Indica si hay una ronda de apuestas activa (en cualquiera de las fases)
    private val _rondaActiva = MutableStateFlow(false)
    val rondaActiva: StateFlow<Boolean> = _rondaActiva

    // Flags para saber en qué fase/ronda estamos
    private val _rondaMusActiva = MutableStateFlow(false)
    val rondaMusActiva: StateFlow<Boolean> = _rondaMusActiva

    private val _rondaParesActiva = MutableStateFlow(false)
    val rondaParesActiva: StateFlow<Boolean> = _rondaParesActiva

    private val _rondaJuegoActiva = MutableStateFlow(false)
    val rondaJuegoActiva: StateFlow<Boolean> = _rondaJuegoActiva

    // Nombre de la ronda actual: "grande", "chica", "pares" o "juego"
    private val _rondaActual = MutableStateFlow("grande")
    val rondaActual: StateFlow<String> = _rondaActual

    // Historial de acciones en la ronda (texto tipo "Jugador X envida 2", etc.)
    private val _acciones = MutableStateFlow(listOf<String>())
    val acciones: StateFlow<List<String>> = _acciones

    // Apuesta actual en juego (cantidad y jugador que la lanzó)
    private val _apuestaActual = MutableStateFlow<Apuesta?>(null)
    val apuestaActual: StateFlow<Apuesta?> = _apuestaActual

    // Lista que indica si cada jugador sigue activo en la apuesta (true) o se ha retirado (false)
    private val _jugadoresActivos = MutableStateFlow(MutableList(4) { true })
    val jugadoresActivos: StateFlow<List<Boolean>> = _jugadoresActivos

    // Índice del jugador que hizo la última subida de apuesta (para saber cuándo todos han igualado)
    private val _jugadorUltimaSubida = MutableStateFlow<Int?>(null)
    val jugadorUltimaSubida: StateFlow<Int?> = _jugadorUltimaSubida

    // Para cada jugador, si ha pedido "Mus" en esta mano
    private val _musPedidos = MutableStateFlow(MutableList(4) { false })
    val musPedidos: StateFlow<List<Boolean>> = _musPedidos

    // Ganadores por tipo de ronda
    private val _ganadorGrande = MutableStateFlow<Jugador?>(null)
    val ganadorGrande: StateFlow<Jugador?> = _ganadorGrande

    private val _ganadorChica = MutableStateFlow<Jugador?>(null)
    val ganadorChica: StateFlow<Jugador?> = _ganadorChica

    // En pares y juego, gana una pareja (dos jugadores)
    private val _ganadorPares = MutableStateFlow<Pair<Jugador, Jugador>?>(null)
    val ganadorPares: StateFlow<Pair<Jugador, Jugador>?> = _ganadorPares

    private val _ganadorJuego = MutableStateFlow<Pair<Jugador, Jugador>?>(null)
    val ganadorJuego: StateFlow<Pair<Jugador, Jugador>?> = _ganadorJuego

    // Resultados individuales de pares para todos los jugadores
    private val _resultadosPares = MutableStateFlow<List<ResultadoPares>>(emptyList())
    val resultadosPares: StateFlow<List<ResultadoPares>> = _resultadosPares

    // Para cada jugador, si tiene pares o no
    private val _jugadoresConPares = MutableStateFlow<List<Boolean>>(emptyList())
    val jugadoresConPares: StateFlow<List<Boolean>> = _jugadoresConPares

    // Puntuaciones de juego (suma de cartas >= 31...) por jugador, si lo usas así
    private val _puntuacionesJuego = MutableStateFlow<List<Int>>(emptyList())
    val puntuacionesJuego: StateFlow<List<Int>> = _puntuacionesJuego

    // Cartas descartadas por cada jugador en rondas de Mus. Cada lista interna son ids de cartas
    private val _cartasDescartadas = MutableStateFlow(List(4) { emptyList<Int>() })
    val cartasDescartadas: StateFlow<List<List<Int>>> = _cartasDescartadas

    // Puntos acumulados de la pareja 1 (jugadores 0 y 1)
    private val _pareja1Puntos = MutableStateFlow(0)
    val pareja1Puntos: StateFlow<Int> = _pareja1Puntos

    // Puntos acumulados de la pareja 2 (jugadores 2 y 3)
    private val _pareja2Puntos = MutableStateFlow(0)
    val pareja2Puntos: StateFlow<Int> = _pareja2Puntos

    // Indica si la partida ha terminado (alguna pareja llegó a los puntos objetivo)
    private val _partidaTerminada = MutableStateFlow(false)
    val partidaTerminada: StateFlow<Boolean> = _partidaTerminada

    // Mensaje resumen final de la partida (quién ganó, resultado global, etc.)
    private val _resultadoPartida = MutableStateFlow("")
    val resultadoPartida: StateFlow<String> = _resultadoPartida

    // Definición de parejas: (0,1) y (2,3)
    private val parejas = listOf(
        listOf(0, 1), // Pareja 1: Jugador humano y Bot 1
        listOf(2, 3)  // Pareja 2: Bot 2 y Bot 3
    )

    // ============================= FUNCIONES DE BASE DE DATOS =============================

    // Crea una nueva partida en la base de datos si no existe, asociada al usuario actual
    private fun iniciarPartidaDB() {
        viewModelScope.launch {
            val partida = PartidaEntity(
                createdBy = currentUserId,              // Usuario humano que inició esta partida
                resultado = "",                         // Resultado vacío al principio
                fecha = System.currentTimeMillis()      // Fecha/hora actual
            )
            // Insertar partida y guardar el id generado
            partidaIdActual = partidaDao.insertPartida(partida).toInt()
            Log.d("DEBUG", "Creando partida con userId=${currentUserId}")
        }
    }

    // Registra un movimiento (acción) en la tabla de movimientos si hay partida en curso
    private fun registrarMovimiento(accion: Accion, cantidad: Int? = null) {
        viewModelScope.launch {
            partidaIdActual?.let { partidaId ->
                // Si el turno es del jugador humano (posición 0), usamos su ID real; si no, -1 para bots
                val userId = if (_turno.value == 0) currentUserId else -1

                val movimiento = MovimientoEntity(
                    partidaId = partidaId,
                    userId = userId,
                    ronda = _rondaActual.value,  // grande, chica, pares o juego
                    accion = accion.name,        // Nombre de la acción (PASAR, ENVIDAR...)
                    cantidad = cantidad          // Cantidad apostada si aplica
                )
                // Guardar movimiento en BD
                movimientoDao.insertMovimiento(movimiento)
            }
        }
    }

    // Guarda el resultado final de la partida en BD y actualiza el estado observable
    private fun guardarResultadoPartida(resultado: String) {
        viewModelScope.launch {
            _resultadoPartida.value = resultado // Actualiza el mensaje para la UI
            partidaIdActual?.let { id ->
                // Recupera partida actual de la BD
                val partida = partidaDao.getPartidaById(id)
                partida?.let {
                    // Crea una copia de la partida con el resultado actualizado
                    val partidaActualizada = it.copy(resultado = resultado)
                    // Actualiza el registro en la BD
                    partidaDao.updatePartida(partidaActualizada)
                }
            }
        }
    }

    // ---------------------- (otras funciones del ViewModel seguirían aquí) ----------------------













// ============================= FUNCIONES PRINCIPALES DEL JUEGO =============================

    // Función que reparte las cartas a los jugadores al inicio de la partida o de una mano nueva
    fun repartirCartas() {
        // Si la partida aún no existe en la base de datos, la iniciamos
        if (partidaIdActual == null) {
            iniciarPartidaDB()
            Log.d("Mus", "Partida DB iniciada con id: $partidaIdActual")
        }

        // Las cartas válidas son números del 1 al 12 excepto el 8 y 9 que no se usan en Mus
        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }

        // Reseteamos contador interno de IDs únicos para las cartas
        siguienteIdCarta = 0

        // Repartimos 4 cartas por jugador, cada una con ID único y valor aleatorio de las válidas
        _jugadores.value = _jugadores.value.map {
            it.copy(cartas = List(4) {
                Carta(siguienteIdCarta++, cartasValidas.random())
            })
        }

        // Actualizamos valores del estado para reflejar el inicio de la ronda Grande
        _cartasRepartidas.value = true
        _rondaActual.value = "grande"
        _rondaActiva.value = true
        _rondaMusActiva.value = false
        _rondaParesActiva.value = false
        _rondaJuegoActiva.value = false

        // El jugador que inicia la partida se va rotando para igualdad
        jugadorInicioPartida = (jugadorInicioPartida + 1) % 4
        Log.d("Mus", "Inicio partida, empieza jugador: ${_jugadores.value[jugadorInicioPartida].nombre}")

        // Marcamos el turno para el jugador que inicia y mostramos mensaje
        _turno.value = jugadorInicioPartida
        _mensajes.value = "Cartas repartidas. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}."

        // Reseteamos lista de acciones, apuesta actual, jugadores activos, último en subir apuesta y ganadores
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

        Log.d("MusGame", "Estado inicial de la partida configurado correctamente.")

        // NOTA: No se reinician los puntos acumulados de las parejas aquí para mantener el marcador
    }

    // Reinicia completamente la partida, reseteando puntos y estado, luego reparte cartas nuevas
    fun reiniciarPartidaCompleta() {
        _pareja1Puntos.value = 0
        _pareja2Puntos.value = 0
        partidaIdActual = null
        _resultadoPartida.value = "" // Limpia mensaje de resultado final
        repartirCartas()
    }

    // Solo para iniciar una nueva mano sin reiniciar puntos ni partida
    fun nuevaMano() {
        repartirCartas()
    }

    // Alterna el estado de una carta para descartar o no (se marca o desmarca)
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

    // Confirma las cartas descartadas por un jugador, las elimina y reparte nuevas cartas válidas
    fun confirmarDescartesJugador(jugadorIndex: Int) {
        val descartes = _cartasDescartadas.value[jugadorIndex]
        val cartasActuales = _jugadores.value[jugadorIndex].cartas.toMutableList()
        // Elimina las cartas descartadas
        cartasActuales.removeAll { descartes.contains(it.id) }
        // Cartas válidas sin 8 y 9
        val cartasValidas = (1..12).filterNot { it == 8 || it == 9 }
        // Genera nuevas cartas para reemplazar descartes
        val nuevasCartas = List(descartes.size) {
            Carta(siguienteIdCarta++, cartasValidas.random())
        }
        // Añade las nuevas cartas a las actuales
        cartasActuales.addAll(nuevasCartas)
        // Actualiza el jugador con las nuevas cartas
        val nuevosJugadores = _jugadores.value.toMutableList()
        nuevosJugadores[jugadorIndex] = nuevosJugadores[jugadorIndex].copy(cartas = cartasActuales)
        _jugadores.value = nuevosJugadores

        // Limpia los descartes del jugador para la próxima fase
        val nuevosDescartes = _cartasDescartadas.value.toMutableList()
        nuevosDescartes[jugadorIndex] = emptyList()
        _cartasDescartadas.value = nuevosDescartes

        // Pasa el turno al siguiente jugador en la ronda de Mus
        pasarSiguienteJugadorRondaMus(jugadorIndex)
    }

    // Pasa el turno al siguiente jugador o termina la ronda Mus si ya todos jugaron
    private fun pasarSiguienteJugadorRondaMus(jugadorActual: Int) {
        val siguiente = (jugadorActual + 1) % 4
        if (siguiente == 0) {
            // Si ya volvemos al jugador 0, termina ronda de Mus y empieza ronda Grande
            _rondaMusActiva.value = false
            _musPedidos.value = MutableList(4) { false }
            _mensajes.value = "Ronda de Mus finalizada. Comienza ronda de Grande."
            iniciarRondaGrande()
        } else {
            // Si no, el turno pasa al siguiente jugador y se muestra mensaje
            _turno.value = siguiente
            _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre} para descartar cartas."
        }
    }

    // Inicia la ronda de "Grande"
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

    // Inicia la ronda de "Chica"
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

    // Inicia la ronda de "Pares"
    fun iniciarRondaPares() {
        _rondaActual.value = "pares"
        _rondaActiva.value = true
        _rondaParesActiva.value = true
        _mensajes.value = "Ronda de Pares iniciada. Verificando pares..."

        // Calcula qué jugadores tienen pares
        val resultados = verificarPares()
        _resultadosPares.value = resultados

        // Indica qué jugadores tienen pares (true/false)
        val conPares = resultados.map { it.combinacion !is SinPares }
        _jugadoresConPares.value = conPares

        // Determina si cada pareja tiene pares
        val pareja1TienePares = conPares[0] || conPares[1]  // pareja jugador humano + Bot1
        val pareja2TienePares = conPares[2] || conPares[3]  // pareja Bot2 + Bot3

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

        // Si ambas parejas tienen pares, solo los jugadores con pares pueden apostar
        _jugadoresActivos.value = conPares.toMutableList()

        // Encontrar primer jugador activo para iniciar apuestas
        var primerJugadorActivo = -1
        for (i in 0 until 4) {
            val index = (jugadorInicioPartida + i) % 4
            if (conPares[index]) {
                primerJugadorActivo = index
                break
            }
        }

        // Establecer turno al primer jugador activo o al que inicia partida si no hay ninguno
        _turno.value = if (primerJugadorActivo != -1) primerJugadorActivo else jugadorInicioPartida
        _apuestaActual.value = null
        _acciones.value = emptyList()
        _jugadorUltimaSubida.value = null

        _mensajes.value = "Ambas parejas tienen pares. Comienza ${_jugadores.value[_turno.value].nombre}"
    }

    // Inicia la ronda de "Juego"
    fun iniciarRondaJuego() {
        _rondaActual.value = "juego"
        _rondaActiva.value = true
        _rondaJuegoActiva.value = true
        _mensajes.value = "Ronda de Juego iniciada. Calculando puntuaciones..."

        // Calcula las puntuaciones de cada jugador según sus cartas
        val puntuaciones = _jugadores.value.map { calcularPuntuacionJuego(it.cartas.map { carta -> carta.valor }) }
        _puntuacionesJuego.value = puntuaciones

        // Jugadores con puntuación mayor o igual a 31 tienen juego
        val conJuego = puntuaciones.map { it >= 31 }

        // Verifica parejas con juego
        val pareja1TieneJuego = conJuego[0] || conJuego[1]
        val pareja2TieneJuego = conJuego[2] || conJuego[3]

        // Ganadores automáticos si solo una pareja tiene juego
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

        val hayJugadoresConJuego = conJuego.any { it }

        if (!hayJugadoresConJuego) {
            // Si nadie tiene juego, todos juegan al punto (puntuación parcial)
            _jugadoresActivos.value = MutableList(4) { true }
            _mensajes.value = "Ningún jugador tiene juego. Se juega al punto. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}"
        } else {
            // Si hay jugadores con juego, sólo ellos pueden participar
            _jugadoresActivos.value = conJuego.toMutableList()
            _mensajes.value = "Ambas parejas tienen juego. Comienza ${_jugadores.value[jugadorInicioPartida].nombre}"
        }

        // Busca el primer jugador activo para iniciar turno
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

    // Función para realizar una acción durante la ronda de apuestas
    fun realizarAccion(accion: Accion, cantidad: Int? = null) {
        // Restricciones específicas durante la ronda de pares
        if (_rondaActual.value == "pares") {
            // Si una pareja no tiene pares, impide cualquier acción
            val pareja1TienePares = _jugadoresConPares.value[0] || _jugadoresConPares.value[1]
            val pareja2TienePares = _jugadoresConPares.value[2] || _jugadoresConPares.value[3]

            if ((pareja1TienePares && !pareja2TienePares) || (!pareja1TienePares && pareja2TienePares)) {
                _mensajes.value = "La ronda de pares ya tiene ganadores automáticos. No se permiten más acciones."
                return
            }

            // Si el jugador actual no tiene pares, se le salta su turno
            if (!_jugadoresConPares.value[_turno.value]) {
                _mensajes.value = "${_jugadores.value[_turno.value].nombre} no tiene pares y no puede actuar en esta ronda."
                avanzarTurno()
                return
            }
        }

        // Restricciones durante la ronda de juego, sólo jugadores con juego activo pueden participar
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
            // Si no hay jugadores con juego, todos pueden participar
        }

        // Validación general: la ronda debe estar activa, y el jugador actual debe estar activo en la apuesta
        if (!_rondaActiva.value || !_jugadoresActivos.value[_turno.value]) return

        val jugadorActual = _jugadores.value[_turno.value]
        val log = _acciones.value.toMutableList()

        // Realiza acción dependiendo del tipo enviado
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

        // Actualiza listado de acciones y avanza el turno al siguiente jugador
        _acciones.value = log
        avanzarTurno()
    }


    // Función que avanza el turno al siguiente jugador activo según la ronda actual
    private fun avanzarTurno() {
        when (_rondaActual.value) {
            "pares" -> {
                // En ronda de pares solo avanzamos a jugadores con pares activos
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
                    // Solo pasan turno a jugadores con juego activos
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
                    // Si nadie tiene juego, todos participan (ronda punto)
                    val siguiente = (_turno.value + 1) % 4
                    _turno.value = siguiente
                    _mensajes.value = "Turno de ${_jugadores.value[siguiente].nombre}"

                    // Si se ha completado un ciclo y todos pidieron Mus, inicia la ronda Mus
                    if (siguiente == jugadorInicioPartida) {
                        if (_musPedidos.value.all { it }) {
                            _musPedidos.value = MutableList(4) { false }
                            iniciarRondaMus()
                        }
                    }
                }
            }
            else -> {
                // Para grandes y chica, turno normal entre todos
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

    // Función que comprueba si solo queda un jugador activo en la apuesta
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
                    // En ronda pares ganador es toda la pareja
                    val parejaIndex = if (idxGanador <= 1) 0 else 1
                    _ganadorPares.value = _jugadores.value[parejas[parejaIndex][0]] to _jugadores.value[parejas[parejaIndex][1]]
                }
                "juego" -> {
                    // En ronda juego ganador es toda la pareja
                    val parejaIndex = if (idxGanador <= 1) 0 else 1
                    _ganadorJuego.value = _jugadores.value[parejas[parejaIndex][0]] to _jugadores.value[parejas[parejaIndex][1]]
                }
            }
            pasarApuestaOSiguienteRonda()
        }
    }

    // Comprueba si todos han igualado la apuesta
    private fun todosHanIgualado(): Boolean {
        val posUltimaSubida = _jugadorUltimaSubida.value ?: return false
        return _turno.value == posUltimaSubida
    }

    // Asigna puntos a la pareja ganadora de la ronda dada
    private fun asignarPuntosRonda(ganador: Jugador?, puntos: Int) {
        if (ganador == null) return
        // Determina la pareja según el nombre del ganador
        val parejaIndex = if (ganador.nombre == currentUsername || ganador.nombre == "Bot 1") 0 else 1
        if (parejaIndex == 0) _pareja1Puntos.value += puntos else _pareja2Puntos.value += puntos
    }

    // Asigna puntos a la pareja ganadora
    private fun asignarPuntosPareja(pareja: Pair<Jugador, Jugador>, puntos: Int) {
        // Determina pareja según si uno de los dos jugadores es el usuario actual
        val parejaIndex = if (pareja.first.nombre == currentUsername || pareja.second.nombre == currentUsername) 0 else 1
        if (parejaIndex == 0) _pareja1Puntos.value += puntos else _pareja2Puntos.value += puntos
    }

    // Calcula puntos ganados por pares sumando según el tipo de combinación
    private fun calcularPuntosPares(ganadores: Pair<Jugador, Jugador>): Int {
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

    // Calcula puntos de juego para la pareja según puntuación de cada jugador
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



    // Comprueba si alguna pareja alcanzó los 40 puntos para finalizar partida
    private fun comprobarFinDePartida() {
        val puntosPareja1 = _pareja1Puntos.value
        val puntosPareja2 = _pareja2Puntos.value

        when {
            puntosPareja1 >= 40 -> {
                _mensajes.value = "🎉 ¡${_jugadores.value[0].nombre} y ${_jugadores.value[1].nombre} habéis ganado la partida!"
                _rondaActiva.value = false

                val resultado = "Ganadores: Pareja 1 (${puntosPareja1} - ${puntosPareja2})"
                guardarResultadoPartida(resultado)

                _partidaTerminada.value = true
            }

            puntosPareja2 >= 40 -> {
                _mensajes.value = "💀 ¡${_jugadores.value[2].nombre} y ${_jugadores.value[3].nombre} han ganado la partida!"
                _rondaActiva.value = false

                val resultado = "Ganadores: Pareja 2 (${puntosPareja1} - ${puntosPareja2})"
                guardarResultadoPartida(resultado)

                _partidaTerminada.value = true
            }
        }
    }

    // Avanza la partida a la siguiente ronda o asigna puntos de la ronda actual
    private fun pasarApuestaOSiguienteRonda() {
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
                    val extra = calcularPuntosPares(ganadores)
                    val total = if (esPaso) {
                        if (extra > 0) extra else 1
                    } else {
                        apuesta + extra
                    }
                    asignarPuntosPareja(ganadores, total)
                }
            }
            "juego" -> {
                val ganadores = _ganadorJuego.value
                if (ganadores != null) {
                    val extra = calcularPuntosJuego(ganadores)
                    val total = if (esPaso) {
                        if (extra > 0) extra else 1
                    } else {
                        apuesta + extra
                    }
                    asignarPuntosPareja(ganadores, total)
                }
            }
        }

        // Comprobamos si alguien llegó a 40 puntos para acabar
        comprobarFinDePartida()

        // Avanzamos a la siguiente ronda o finalizamos partida
        when (_rondaActual.value) {
            "grande" -> iniciarRondaChica()
            "chica" -> iniciarRondaPares()
            "pares" -> iniciarRondaJuego()
            "juego" -> {
                if (_ganadorJuego.value == null) {
                    _mensajes.value = "Ronda de Juego finalizada. Partida terminada."
                    guardarResultadoPartida("Partida completada (${_pareja1Puntos.value} - ${_pareja2Puntos.value})")
                }
                _rondaJuegoActiva.value = false
            }
        }

        // Reiniciamos apuesta y jugador de última subida para siguiente ronda
        _apuestaActual.value = null
        _jugadorUltimaSubida.value = null
    }

    // Inicia la ronda de Mus: activa ronda Mus y resetea descartes, mensaje y turno
    fun iniciarRondaMus() {
        _rondaMusActiva.value = true
        _rondaActiva.value = false
        _mensajes.value = "Ronda de Mus iniciada. Selecciona las cartas a descartar."
        _turno.value = 0
        _cartasDescartadas.value = List(4) { emptyList() }
    }




    // ============================= FUNCIONES AUXILIARES DEL JUEGO =============================

    // Normaliza valores de carta para pares y juego (3 y 12 se consideran iguales, 1 y 2 iguales)
    private fun normalizarValorCarta(valor: Int): Int {
        return when (valor) {
            12, 3 -> 3  // Rey = 3
            1, 2  -> 1  // As = 1
            else -> valor
        }
    }

    // ----------- GRANDE -----------
// Asigna fuerza a cada carta para la ronda Grande (orden de mayor a menor fuerza)
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
    // Ordena las cartas según su fuerza en Grande
    private fun ordenarCartasGrande(cartas: List<Int>) = cartas.sortedByDescending { fuerzaCarta(it) }
    // Compara dos manos para Grande, carta a carta, según fuerza
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
// Asigna fuerza a cada carta para ronda Chica (orden de menor a mayor fuerza)
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
    // Ordena cartas para Chica de menor a mayor fuerza
    private fun ordenarCartasChica(cartas: List<Int>) = cartas.sortedBy { fuerzaCartaChica(it) }
    // Compara dos manos para Chica, con orden invertido (menor es mejor)
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
// Verifica combinaciones de pares para todos los jugadores y devuelve resultados
    private fun verificarPares(): List<ResultadoPares> {
        return _jugadores.value.map { jugador ->
            val combinacion = detectarCombinacionPares(jugador.cartas.map { it.valor })
            val valorCombinacion = calcularValorCombinacion(combinacion)
            ResultadoPares(combinacion, jugador, valorCombinacion)
        }
    }

    // Detecta la combinación de pares (duples, medias, par o sin pares) dada una lista de valores
    private fun detectarCombinacionPares(cartas: List<Int>): CombinacionPares {
        val cartasNormalizadas = cartas.map { normalizarValorCarta(it) }
        val conteo = cartasNormalizadas.groupingBy { it }.eachCount()

        val pares = conteo.entries.filter { it.value >= 2 }
        if (pares.size >= 2) {
            val valoresPares = pares.map { it.key }.sortedByDescending { fuerzaCartaPares(it) }
            return Duples(valoresPares[0], valoresPares[1])
        }

        conteo.entries.find { it.value == 3 }?.let {
            return Medias(it.key)
        }
        conteo.entries.find { it.value == 2 }?.let {
            return Par(it.key)
        }
        return SinPares
    }

    // Asigna fuerza para cartas en la ronda pares
    private fun fuerzaCartaPares(carta: Int): Int = when (carta) {
        3 -> 13  // Reyes (3 o 12)
        11 -> 12 // Caballos
        10 -> 11 // Sotas
        7 -> 10
        6 -> 9
        5 -> 8
        4 -> 7
        2 -> 6
        1 -> 5
        else -> 0
    }

    // Calcula valor numérico para una combinación de pares (para comparar prioridades)
    private fun calcularValorCombinacion(combinacion: CombinacionPares): Int {
        return when (combinacion) {
            is SinPares -> 0
            is Par -> 100 + fuerzaCartaPares(combinacion.valorCarta)
            is Medias -> 200 + fuerzaCartaPares(combinacion.valorCarta)
            is Duples -> 300 + fuerzaCartaPares(combinacion.valorCarta1) * 10 + fuerzaCartaPares(combinacion.valorCarta2)
            else -> 0
        }
    }

    // Calcula cuál pareja gana los pares en base al valor de las combinaciones
    private fun calcularGanadorPares(): Pair<Jugador, Jugador>? {
        val resultados = _resultadosPares.value

        val mejorPareja1 = resultados.filterIndexed { index, _ -> index <= 1 }
            .maxByOrNull { it.valorCombinacion }

        val mejorPareja2 = resultados.filterIndexed { index, _ -> index >= 2 }
            .maxByOrNull { it.valorCombinacion }

        if (mejorPareja1?.combinacion is SinPares && mejorPareja2?.combinacion is SinPares) {
            return null
        }
        if (mejorPareja1?.combinacion is SinPares) {
            return _jugadores.value[2] to _jugadores.value[3]
        }
        if (mejorPareja2?.combinacion is SinPares) {
            return _jugadores.value[0] to _jugadores.value[1]
        }

        val comparacion = (mejorPareja1?.valorCombinacion ?: 0) - (mejorPareja2?.valorCombinacion ?: 0)

        return when {
            comparacion > 0 -> _jugadores.value[0] to _jugadores.value[1]
            comparacion < 0 -> _jugadores.value[2] to _jugadores.value[3]
            else -> {
                if (jugadorInicioPartida <= 1) _jugadores.value[0] to _jugadores.value[1]
                else _jugadores.value[2] to _jugadores.value[3]
            }
        }
    }

    // ----------- JUEGO -----------
// Suma los valores de las cartas para calcular puntuación en la ronda juego
    private fun calcularPuntuacionJuego(cartas: List<Int>): Int {
        return cartas.sumOf { carta ->
            when (carta) {
                1, 2 -> 1
                in 4..7 -> carta
                10, 11 -> 10
                3, 12 -> 10
                else -> 0
            }
        }
    }

    // Asigna valor numérico para comparar puntuaciones en juego (31 máximo)
    private fun calcularValorJuego(puntuacion: Int): Int {
        return when (puntuacion) {
            31 -> 100
            32 -> 90
            40 -> 80
            37 -> 70
            36 -> 60
            35 -> 50
            34 -> 40
            33 -> 30
            in 0..30 -> puntuacion
            else -> 0
        }
    }

    // Calcula pareja ganadora en la ronda juego
    private fun calcularGanadorJuego(): Pair<Jugador, Jugador>? {
        val puntuaciones = _puntuacionesJuego.value
        val hayJugadoresConJuego = puntuaciones.any { it >= 31 }

        val mejorPareja1 = if (hayJugadoresConJuego) {
            puntuaciones.filterIndexed { index, _ -> index <= 1 }.filter { it >= 31 }.maxOrNull() ?: -1
        } else {
            puntuaciones.filterIndexed { index, _ -> index <= 1 }.maxOrNull() ?: -1
        }
        val mejorPareja2 = if (hayJugadoresConJuego) {
            puntuaciones.filterIndexed { index, _ -> index >= 2 }.filter { it >= 31 }.maxOrNull() ?: -1
        } else {
            puntuaciones.filterIndexed { index, _ -> index >= 2 }.maxOrNull() ?: -1
        }

        if (hayJugadoresConJuego) {
            if (mejorPareja1 == -1 && mejorPareja2 == -1) return null
            if (mejorPareja1 == -1) return _jugadores.value[2] to _jugadores.value[3]
            if (mejorPareja2 == -1) return _jugadores.value[0] to _jugadores.value[1]
        } else {
            if (mejorPareja1 == -1 && mejorPareja2 == -1) return null
            if (mejorPareja1 == -1) return _jugadores.value[2] to _jugadores.value[3]
            if (mejorPareja2 == -1) return _jugadores.value[0] to _jugadores.value[1]
        }

        val valorPareja1 = calcularValorJuego(mejorPareja1)
        val valorPareja2 = calcularValorJuego(mejorPareja2)

        val comparacion = valorPareja1 - valorPareja2

        return when {
            comparacion > 0 -> _jugadores.value[0] to _jugadores.value[1]
            comparacion < 0 -> _jugadores.value[2] to _jugadores.value[3]
            else -> if (jugadorInicioPartida <= 1) _jugadores.value[0] to _jugadores.value[1] else _jugadores.value[2] to _jugadores.value[3]
        }
    }

    // Función para finalizar la ronda pares sin ganador
    private fun finalizarRondaParesSinGanador() {
        _ganadorPares.value = null
        _rondaActiva.value = false
        _rondaParesActiva.value = false
        _mensajes.value = "Ronda de Pares finalizada sin ganadores"
        pasarApuestaOSiguienteRonda()
    }

    // Finaliza ronda pares mostrando ganador automático
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

    // Finaliza ronda de juego con ganador automático
    private fun finalizarRondaJuegoConGanadorAutomatico() {
        _rondaActiva.value = false
        _rondaJuegoActiva.value = false
        _mensajes.value = if (_ganadorJuego.value != null) {
            "Ganadores automáticos de Juego: ${_ganadorJuego.value!!.first.nombre} y ${_ganadorJuego.value!!.second.nombre}"
        } else {
            "Ronda de Juego finalizada sin ganadores"
        }
        _mensajes.value = "Partida terminada. ${_mensajes.value}"
        guardarResultadoPartida("Ronda completada (${_pareja1Puntos.value} - ${_pareja2Puntos.value})")
    }

    // Calcula el ganador de la ronda chica
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

    // Calcula el ganador de la ronda grande
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

    // Finaliza la apuesta en curso calculando ganadores y mostrando mensajes
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

    // Reinicia partida manualmente (para botones o reinicios directos)
    fun reiniciarPartidaManual() {
        _partidaTerminada.value = false
        reiniciarPartidaCompleta()
    }



}