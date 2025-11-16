package com.example.appcarnavalextraordinaria.Navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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

import com.example.appcarnavalextraordinaria.Screen2.EstrategiasScreen
import com.example.appcarnavalextraordinaria.Screen2.FlujoPartidaMusScreen
import com.example.appcarnavalextraordinaria.Screen2.MazoCartasScreen
import com.example.appcarnavalextraordinaria.Screen2.PuntuacionScreen
import com.example.appcarnavalextraordinaria.Screen2.ReglasBasicasScreen
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.appcarnavalextraordinaria.Login.LoginScreen
import com.example.appcarnavalextraordinaria.Login.RegistroScreen



import com.example.appcarnavalextraordinaria.Screen2.TutorialesScreen


import com.example.appcarnavalextraordinaria.Screen3.SenalesScreen
import com.example.appcarnavalextraordinaria.Test.TestDetailScreen
import com.example.appcarnavalextraordinaria.Test.TestResultScreen
import com.example.appcarnavalextraordinaria.Test.TestsListScreen


@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    db: AppDatabase
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // UserViewModel
    val userDao = db.userDao()
    val partidaDao = db.partidaDao()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.UserViewModelFactory(userDao))

    // Obtener el usuario logueado
    val loggedInUser by userViewModel.loggedInUser.observeAsState(null)
    val currentUserId = loggedInUser?.id ?: userViewModel.getUserId(context)
    val currentUsername = loggedInUser?.username ?: userViewModel.getSession(context) ?: "Usuario"

    val testDao = db.testDao()

    NavHost(
        navController = navController,
        startDestination = "main",
        modifier = Modifier.padding(innerPadding)
    ) {
        composable("main") {
            MainScreen(
                navController = navController,
                userViewModel = userViewModel,
                partidaDao = partidaDao, // Cambiar progressDao por partidaDao
                currentUserId = currentUserId,
                currentUsername = currentUsername,
                context = context
            )
        }

        composable("Tutoriales") { TutorialesScreen(navController) }
        composable("Reglas") { ReglasBasicasScreen(navController) }
        composable("Mazo") { MazoCartasScreen(navController) }
        composable("Estrategias") { EstrategiasScreen(navController) }
        composable("Funcionamiento") { FlujoPartidaMusScreen(navController) }
        composable("Puntuacion") { PuntuacionScreen(navController) }
        composable("Senales") { SenalesScreen(navController) }

        composable("Partida") {
            PartidaMusScreen(
                navController = navController,
                userDao = db.userDao(),
                partidaDao = db.partidaDao(),
                movimientoDao = db.movimientoDao(),
                progressDao = db.progressDao(),
                currentUserId = currentUserId,
                currentUsername = currentUsername
            )
        }

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

        composable("tests") {
            TestsListScreen(
                navController = navController,
                testDao = db.testDao(),
                currentUserId = currentUserId // ¡Este debe ser el ID correcto!
            )
        }

        // Ruta corregida para testDetail
        composable(
            "testDetail/{testId}",
            arguments = listOf(navArgument("testId") { type = NavType.IntType })
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getInt("testId") ?: 0

            TestDetailScreen(
                testId = testId,
                testDao = testDao,
                navController = navController
            )
        }

        // Ruta corregida para testResult
        composable(
            "testResult/{testId}/{score}/{total}",
            arguments = listOf(
                navArgument("testId") { type = NavType.IntType },
                navArgument("score") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getInt("testId") ?: 0
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val total = backStackEntry.arguments?.getInt("total") ?: 0

            TestResultScreen(
                navController = navController,
                score = score,
                totalQuestions = total,
                testTitle = "Test" // O puedes obtenerlo de la BD si quieres
            )
        }
    }
}


