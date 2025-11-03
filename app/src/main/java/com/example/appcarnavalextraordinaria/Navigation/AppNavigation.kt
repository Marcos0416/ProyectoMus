package com.example.appcarnavalextraordinaria.Navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Data.AppDatabase

import com.example.appcarnavalextraordinaria.Login.UserViewModel
import com.example.appcarnavalextraordinaria.PartidasInteractivas.PartidaMusScreen
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.Screen1.MainViewModel
import com.example.appcarnavalextraordinaria.Screen2.EncuestaViewModel
import com.example.appcarnavalextraordinaria.Screen2.EstrategiasScreen
import com.example.appcarnavalextraordinaria.Screen2.FlujoPartidaMusScreen
import com.example.appcarnavalextraordinaria.Screen2.MazoCartasScreen
import com.example.appcarnavalextraordinaria.Screen2.PuntuacionScreen
import com.example.appcarnavalextraordinaria.Screen2.ReglasBasicasScreen
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.Login.LoginScreen
import com.example.appcarnavalextraordinaria.Login.RegistroScreen



import com.example.appcarnavalextraordinaria.Screen2.TutorialesScreen


import com.example.appcarnavalextraordinaria.Screen3.SenalesScreen


// Función Composable que configura la navegación de la aplicación
@Composable
fun AppNavigation(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Creas base de datos y userViewModel solo una vez, para compartir entre pantallas
    val db = AppDatabase.getDatabase(context)
    val userDao = db.userDao()
    val factory = UserViewModel.UserViewModelFactory(userDao)
    val userViewModel: UserViewModel = viewModel(factory = factory)

    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreen(navController, userViewModel, context)
        }






        composable("Tutoriales") { TutorialesScreen(navController) }
        composable("Reglas") { ReglasBasicasScreen(navController) }
        composable("Mazo") { MazoCartasScreen(navController) }

        composable("Estrategias") { EstrategiasScreen(navController) }
        composable("Funcionamiento") { FlujoPartidaMusScreen(navController) }
        composable("Puntuacion") { PuntuacionScreen(navController) }
        composable("Senales") { SenalesScreen(navController) }
        composable("Partida") { PartidaMusScreen(navController) }

        composable("Registro") {
            RegistroScreen(
                navController = navController,
                userViewModel = userViewModel,
                context = context,
                onRegistroOk = { navController.popBackStack() }
            )
        }
        composable("Login") {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                context = context,
                onLoginSuccess = { navController.popBackStack() }
            )
        }



    }
}

