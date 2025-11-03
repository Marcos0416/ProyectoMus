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
import com.example.appcarnavalextraordinaria.Screen2.EncuestaViewModel






import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Screen1.MainScreen
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionOrdinariaInterfacesTheme {
                Surface {
                    Scaffold { innerPadding ->
                        // Simplemente pasamos innerPadding si lo necesitas
                        AppNavigation(innerPadding = innerPadding)
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




