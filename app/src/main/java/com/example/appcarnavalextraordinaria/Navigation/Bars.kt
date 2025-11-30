package com.example.appcarnavalextraordinaria.Navigation

// Importaciones necesarias para UI, navegación y manejo de recursos
import android.util.Log
import androidx.compose.foundation.Image // Para mostrar imágenes en la UI
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable // Para hacer elementos clicables
import androidx.compose.foundation.interaction.MutableInteractionSource // Controla interacciones táctiles
import androidx.compose.foundation.layout.* // Para usar layouts como Row, Column, Spacer, etc.
import androidx.compose.material.icons.Icons // Conjunto de íconos predeterminados
import androidx.compose.material.icons.automirrored.filled.Login // Ícono Login con espejo automático
import androidx.compose.material.icons.filled.* // Varios íconos llenos por defecto
import androidx.compose.material3.* // Componentes Material Design 3 para Android
import androidx.compose.runtime.Composable // Indica función composable para Jetpack Compose
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment // Para alinear elementos visuales
import androidx.compose.ui.Modifier // Para modificar propiedades y comportamiento visual
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Para cargar imágenes desde recursos
import androidx.compose.ui.unit.dp // Unidad de medida para densidad independiente (distancias, tamaños)
import androidx.compose.ui.unit.sp // Unidad de medida para tamaños de texto
import androidx.navigation.NavController // Navegación entre pantallas
import com.example.appcarnavalextraordinaria.R // Recursos generados, como imágenes o strings


// Función composable que crea las barras superior e inferior de la aplicación,
// usando Scaffold que gestiona el layout base
// Habilita funcionalidades experimentales de Material3 con @OptIn
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Bars(
    navController: NavController,                     // Controlador para navegar entre pantallas
    content: @Composable (Modifier) -> Unit,           // Contenido principal que se dibuja con un modifier
) {
    // Scaffold establece estructura básica: top bar, bottom bar y contenido principal
    Scaffold(
        topBar = {
            // Barra superior (AppBar) con título y acciones
            TopAppBar(
                title = {
                    // Título compuesto por imagen y texto en fila horizontal, centrados verticalmente
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Imagen logo o decorativa, tamaño fijo 52dp x 52dp
                        Image(
                            painter = painterResource(id = R.drawable.imagenmustfg),
                            contentDescription = "Imagen de mus",
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Separador horizontal de 8dp

                        // Texto con nombre de la app, estilo y color definidos por tema Material
                        Text(
                            text = "Mus Educativo",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    // Icono para registrarse, clicable para navegar a pantalla "Registro"
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Registro",
                        modifier = Modifier
                            .size(40.dp) // Tamaño del icono
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() }, // Controla feedback interacción
                                indication = LocalIndication.current                         // Indicación visual al clicar
                            ) { navController.navigate("Registro") } // Navega a pantalla Registro
                    )

                    // Icono para login, clicable para navegar a pantalla "Login"
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Login, // Ícono mirroring auto para RTL
                        contentDescription = "Login",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) { navController.navigate("Login") }
                    )
                },
                // Colores personalizados para la barra superior
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },



        // Contenido principal que se dibuja debajo de las barras, recibe padding para no solaparse
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}
