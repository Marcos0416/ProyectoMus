package com.example.appcarnavalextraordinaria.Test

import org.junit.Test
import org.junit.Assert.*

class MusGameTest {

    @Test
    fun testCalcularPuntajeTotal() {
        val puntosJugador1 = 10
        val puntosJugador2 = 5
        val puntosJugador3 = 7
        val puntosJugador4 = 8

        val puntajeTotal = puntosJugador1 + puntosJugador2 +
                puntosJugador3 + puntosJugador4
        assertEquals(30, puntajeTotal)
    }

}

