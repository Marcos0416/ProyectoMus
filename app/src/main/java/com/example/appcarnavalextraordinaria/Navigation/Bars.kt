package com.example.appcarnavalextraordinaria.Navigation

import androidx.compose.foundation.Image // Importa la clase para mostrar imágenes
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.* // Importa para el uso de Layouts (Column, Row, etc.)
import androidx.compose.material.icons.Icons // Importa el objeto Icons para los íconos predeterminados
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.* // Importa los íconos llenos predeterminados
import androidx.compose.material3.* // Importa componentes de Material Design 3
import androidx.compose.runtime.Composable // Importa la anotación Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier // Importa para modificar componentes
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Importa para cargar recursos de imágenes
import androidx.compose.ui.unit.dp // Importa para manejar unidades de densidad independiente
import androidx.compose.ui.unit.sp // Importa para manejar unidades de tamaño de fuente
import androidx.navigation.NavController
import com.example.appcarnavalextraordinaria.R


// Anotación que habilita características experimentales de Material3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bars(
    navController: NavController,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.imagenmustfg),
                            contentDescription = "Imagen de mus",
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mus Educativo",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {


                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Registro",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) { navController.navigate("Registro") }
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Login, // Si no está, usa Icon.Default.Lock o Icon.Default.AccountCircle
                        contentDescription = "Login",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) { navController.navigate("Login") }
                    )




                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },

        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    /*
                    IconButton(onClick = { /* acción vacía */ }) {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    }
                    IconButton(onClick = { /* acción vacía */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar")
                    }
                    IconButton(onClick = { /* acción vacía */ }) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }

                     */
                }
            }
        },

        ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}


