package com.example.appcarnavalextraordinaria.Screen1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    // Estados reactivos

    var Salud by mutableStateOf(0)
        private set

    var Dinero by mutableStateOf(0)
        private set
    var Amor by mutableStateOf(0)
        private set



    fun reset() {
        Salud =0
        Dinero = 0
        Amor = 0

    }

    fun preguntar(){
        Salud = (18..100).random()
        Dinero = (18..100).random()
        Amor = (18..100).random()

    }

    fun Resultado(): Int {

        var resultado = Salud +Dinero+Amor
        resultado /= 3

        return resultado

    }

}