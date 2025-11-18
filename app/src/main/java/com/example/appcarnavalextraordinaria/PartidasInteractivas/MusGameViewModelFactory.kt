package com.example.appcarnavalextraordinaria.PartidasInteractivas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appcarnavalextraordinaria.Data.MovimientoDao
import com.example.appcarnavalextraordinaria.Data.PartidaDao
import com.example.appcarnavalextraordinaria.Data.ProgressDao
import com.example.appcarnavalextraordinaria.Data.UserDao

class MusGameViewModelFactory(
    private val userDao: UserDao,
    private val partidaDao: PartidaDao,
    private val movimientoDao: MovimientoDao,

    private val currentUserId: Int,
    private val currentUsername: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusGameViewModel::class.java)) {
            return MusGameViewModel(
                userDao,
                partidaDao,
                movimientoDao,

                currentUserId,
                currentUsername
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


