package com.example.appcarnavalextraordinaria.Navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

import com.example.appcarnavalextraordinaria.Tutoriales.EstrategiasScreen
import com.example.appcarnavalextraordinaria.Tutoriales.FlujoPartidaMusScreen
import com.example.appcarnavalextraordinaria.Tutoriales.MazoCartasScreen
import com.example.appcarnavalextraordinaria.Tutoriales.PuntuacionScreen
import com.example.appcarnavalextraordinaria.Tutoriales.ReglasBasicasScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.appcarnavalextraordinaria.Data.TestResultDao
import com.example.appcarnavalextraordinaria.Login.LoginScreen
import com.example.appcarnavalextraordinaria.Login.RegistroScreen



import com.example.appcarnavalextraordinaria.Tutoriales.TutorialesScreen


import com.example.appcarnavalextraordinaria.Senales.SenalesScreen
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
    val testResultDao = db.testResultDao()
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
                partidaDao = partidaDao,
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
                testDao = testDao,
                testResultDao = testResultDao, // ← PASA EL NUEVO DAO
                currentUserId = currentUserId
            )
        }

        // Ruta para el detalle del test
        composable("testDetail/{testId}") { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId")?.toIntOrNull() ?: 0
            // Obtener el título del test si es posible
            var testTitle by remember { mutableStateOf("Test") }

            LaunchedEffect(testId) {
                // Obtener el título real del test desde la base de datos
                val test = testDao.getTestById(testId)
                test?.let {
                    testTitle = it.title
                }
            }

            TestDetailScreen(
                testId = testId,
                testDao = testDao,
                testResultDao = testResultDao, // ← PASA EL NUEVO DAO
                navController = navController,
                currentUserId = currentUserId,
                testTitle = testTitle
            )
        }

        // Ruta corregida para testResult
        composable("testResult/{testId}/{score}/{totalQuestions}") { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId")?.toIntOrNull() ?: 0
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments?.getString("totalQuestions")?.toIntOrNull() ?: 0

            var testTitle by remember { mutableStateOf("Test") }

            LaunchedEffect(testId) {
                val test = testDao.getTestById(testId)
                test?.let {
                    testTitle = it.title
                }
            }

            TestResultScreen(
                navController = navController,
                score = score,
                totalQuestions = totalQuestions,
                testTitle = testTitle,
                testId = testId
            )
        }
    }
}


