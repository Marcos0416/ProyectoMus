package com.example.appcarnavalextraordinaria.Screen1

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Data.ProgressDao
import com.example.appcarnavalextraordinaria.Data.ProgressEntity
import com.example.appcarnavalextraordinaria.Login.UserViewModel
import com.example.appcarnavalextraordinaria.Navigation.Bars
import com.example.appcarnavalextraordinaria.R
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    progressDao: ProgressDao,
    currentUserId: Int,         // <--- agregar esto
    currentUsername: String,    // <--- agregar esto
    context: Context
) {
    val loggedUsername = userViewModel.getSession(context)
    val progressList = remember { mutableStateListOf<ProgressEntity>() }

    // Cargar progresos del usuario
    LaunchedEffect(currentUserId) {
        progressDao.getProgressByUser(currentUserId).collect { list ->
            progressList.clear()
            progressList.addAll(list)
        }
    }

    Bars(navController = navController) { modifier ->
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    // --- Encabezado y avatar ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!loggedUsername.isNullOrEmpty()) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = loggedUsername.first().uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = loggedUsername,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = {
                                    userViewModel.logout(context)
                                    navController.navigate("Login") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = "Cerrar sesión",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        } else {
                            Text(
                                text = "No tienes sesión iniciada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // --- Imagen y descripción ---
                    Image(
                        painter = painterResource(id = R.drawable.imagenmustfg),
                        contentDescription = "Logo Mus",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    )
                    Text(
                        text = "Mus Educativo",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Descubre reglas, señales y aprende a jugar al Mus con una app interactiva.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // --- Menú principal ---
                item {
                    CardMenuItem(
                        title = "Tutoriales",
                        description = "Explicaciones desde lo más básico hasta avanzado.",
                        imageRes = R.drawable.tutorial,
                        onClick = { navController.navigate("Tutoriales") },
                    )
                    CardMenuItem(
                        title = "Diccionario de Señales",
                        description = "Aprende las señales con imágenes claras.",
                        imageRes = R.drawable.senalesmus,
                        onClick = { navController.navigate("Senales") }
                    )
                    CardMenuItem(
                        title = "Simulación de partida",
                        description = "Practica partidas y mejora tu habilidad.",
                        imageRes = R.drawable.partidasmus,
                        onClick = { navController.navigate("Partida") }
                    )
                    CardMenuItem(
                        title = "Test de conocimientos",
                        description = "Ponte a prueba sobre lo que has aprendido.",
                        imageRes = R.drawable.examenmus,
                        onClick = { navController.navigate("test") }
                    )
                }

                // --- Progreso del usuario ---
                if (progressList.isNotEmpty()) {
                    item {
                        Text(
                            text = "Tu progreso",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        )
                    }

                    items(progressList.size) { index ->
                        val item = progressList[index]
                        val fechaLegible = java.text.SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date(item.lastAccess))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {

                                    Text("Puntos: ${item.score}")
                                    Text("Último acceso: $fechaLegible")
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = "Aún no tienes progreso registrado.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


// Composable para un elemento de menú visual
@Composable
fun CardMenuItem(
    title: String,
    description: String,
    imageRes: Int,            // ¡Usa el ID del recurso drawable aquí!
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()

            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            )
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}





/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicacionOrdinariaInterfacesTheme  {
        MainScreen(navController = rememberNavController(), userViewModel: UserViewModel,
            context: Context )
    }
}

 */

