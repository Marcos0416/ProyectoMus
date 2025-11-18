package com.example.appcarnavalextraordinaria.Navigation

// Importamos librerías necesarias para construir UI y manejar navegación con Jetpack Compose
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

// Importamos DAOs, ViewModel y pantallas propias de la app para usarlas en navegación
import com.example.appcarnavalextraordinaria.Data.AppDatabase
import com.example.appcarnavalextraordinaria.Login.UserViewModel
import com.example.appcarnavalextraordinaria.PartidasInteractivas.PartidaMusScreen
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.Tutoriales.EstrategiasScreen
import com.example.appcarnavalextraordinaria.Tutoriales.FlujoPartidaMusScreen
import com.example.appcarnavalextraordinaria.Tutoriales.MazoCartasScreen
import com.example.appcarnavalextraordinaria.Tutoriales.PuntuacionScreen
import com.example.appcarnavalextraordinaria.Tutoriales.ReglasBasicasScreen
import com.example.appcarnavalextraordinaria.Senales.SenalesScreen
import com.example.appcarnavalextraordinaria.Test.TestDetailScreen
import com.example.appcarnavalextraordinaria.Test.TestResultScreen
import com.example.appcarnavalextraordinaria.Test.TestsListScreen
import com.example.appcarnavalextraordinaria.Login.LoginScreen
import com.example.appcarnavalextraordinaria.Login.RegistroScreen
import com.example.appcarnavalextraordinaria.Tutoriales.TutorialesScreen

@Composable
fun AppNavigation(
    innerPadding: PaddingValues,     // Espacio para márgenes visibles (bar, notch, etc.)
    db: AppDatabase                 // Instancia de la base de datos para acceder a DAOs
) {
    val navController = rememberNavController()  // Controlador para manejar navegación

    val context = LocalContext.current           // Contexto Android para acceder a recursos y preferencias

    // Inicializamos DAOs necesarios para acceder a datos
    val userDao = db.userDao()
    val partidaDao = db.partidaDao()
    val testResultDao = db.testResultDao()
    val testDao = db.testDao()

    // Creamos ViewModel utilizando la factoría para pasar el userDao
    val userViewModel: UserViewModel = viewModel(factory = UserViewModel.UserViewModelFactory(userDao))

    // Observamos LiveData del usuario logueado desde el ViewModel para usar en UI y lógica
    val loggedInUser by userViewModel.loggedInUser.observeAsState(null)

    // Obtenemos el ID del usuario activo, si no hay, recuperamos de SharedPreferences
    val currentUserId = loggedInUser?.id ?: userViewModel.getUserId(context)
    // Obtenemos nombre usuario activo o usamos valor por defecto "Usuario"
    val currentUsername = loggedInUser?.username ?: userViewModel.getSession(context) ?: "Usuario"


    NavHost(
        navController = navController,     // Controlador para cambiar entre pantallas
        startDestination = "main",          // Pantalla inicial al arrancar la app
        modifier = Modifier.padding(innerPadding)
    ) {
        // Definimos cada ruta de navegación con su pantalla correspondiente

        composable("main") {
            // Pantalla principal de la aplicación
            MainScreen(
                navController = navController,
                userViewModel = userViewModel,
                partidaDao = partidaDao,
                currentUserId = currentUserId,      // ID disponible para mostrar datos personalizados
                currentUsername = currentUsername,  // Nombre visible para la sesión actual
                context = context
            )
        }

        // Pantallas de tutoriales
        composable("Tutoriales") { TutorialesScreen(navController) }
        composable("Reglas") { ReglasBasicasScreen(navController) }
        composable("Mazo") { MazoCartasScreen(navController) }
        composable("Estrategias") { EstrategiasScreen(navController) }
        composable("Funcionamiento") { FlujoPartidaMusScreen(navController) }
        composable("Puntuacion") { PuntuacionScreen(navController) }
        composable("Senales") { SenalesScreen(navController) }

        // Pantalla del juego Mus interactivo
        composable("Partida") {
            PartidaMusScreen(
                navController = navController,
                userDao = db.userDao(),
                partidaDao = db.partidaDao(),
                movimientoDao = db.movimientoDao(),
                currentUserId = currentUserId,
                currentUsername = currentUsername
            )
        }

        // Pantalla de registro con callback para navegación
        composable("Registro") {
            RegistroScreen(
                navController = navController,
                userViewModel = userViewModel,
                context = context,
                onRegistroOk = { navController.popBackStack() }  // Vuelve a pantalla anterior al registrar
            )
        }

        // Pantalla de login con callback para navegación
        composable("Login") {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                context = context,
                onLoginSuccess = { navController.popBackStack() }   // Vuelve a pantalla anterior al loguear
            )
        }

        // Lista de tests para el usuario
        composable("tests") {
            TestsListScreen(
                navController = navController,
                testDao = testDao,
                testResultDao = testResultDao,
                currentUserId = currentUserId
            )
        }

        // Ruta dinámica para detalle de test, recibe parámetro testId en la ruta
        composable("testDetail/{testId}") { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId")?.toIntOrNull() ?: 0
            var testTitle by remember { mutableStateOf("Test") }

            // Cargar título del test cuando cambie el testId (lado UI reactiva)
            LaunchedEffect(testId) {
                val test = testDao.getTestById(testId)
                test?.let { testTitle = it.title }
            }

            TestDetailScreen(
                testId = testId,
                testDao = testDao,
                testResultDao = testResultDao,
                navController = navController,
                currentUserId = currentUserId,
                testTitle = testTitle
            )
        }

        // Ruta dinámica para resultado de test con parámetros score y totalQuestions
        composable("testResult/{testId}/{score}/{totalQuestions}") { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId")?.toIntOrNull() ?: 0
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val totalQuestions = backStackEntry.arguments?.getString("totalQuestions")?.toIntOrNull() ?: 0

            var testTitle by remember { mutableStateOf("Test") }

            LaunchedEffect(testId) {
                val test = testDao.getTestById(testId)
                test?.let { testTitle = it.title }
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
