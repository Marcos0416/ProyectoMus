package com.example.appcarnavalextraordinaria.Screen2

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class EncuestaViewModel : ViewModel() {
    // Estado de la encuesta
    var Linea1 = mutableStateOf(false)


    var Linea2 = mutableStateOf(false)


    var Linea5 = mutableStateOf(false)



    var mostrarResultados = mutableStateOf(false)

    // Función para actualizar el valor de los campos
    fun actualizarLinea1(tiene: Boolean) {
        Linea1.value = tiene
    }

    fun actualizarLinea2(tiene: Boolean) {
        Linea2.value = tiene
    }

    fun actualizarLinea5(tiene: Boolean) {
        Linea5.value = tiene
    }



    fun mostrarResultados() {
        mostrarResultados.value = true
    }
}