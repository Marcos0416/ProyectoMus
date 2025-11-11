package com.example.appcarnavalextraordinaria

import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appcarnavalextraordinaria.Navigation.AppNavigation
import com.example.appcarnavalextraordinaria.Screen1.MainViewModel







import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Data.AppDatabase
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instancia de tu base de datos
        val db = AppDatabase.getDatabase(this)

        setContent {
            AplicacionOrdinariaInterfacesTheme {
                Surface {
                    Scaffold { innerPadding ->
                        // Supongamos que obtienes el usuario logueado aquí
                        val currentUserId = remember { mutableStateOf(0) }

                        // Ejemplo: cargamos el primer usuario (ajústalo según tu login)
                        LaunchedEffect(Unit) {
                            val user = db.userDao().getUserByUsername("usuarioLogin")
                            currentUserId.value = user?.id ?: 0
                        }

                        AppNavigation(
                            innerPadding = innerPadding,
                            db = db

                        )
                    }
                }
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicacionOrdinariaInterfacesTheme  {
        MainScreen(navController = rememberNavController())
    }
}

 */




