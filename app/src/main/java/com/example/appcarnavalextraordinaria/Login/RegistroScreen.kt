package com.example.appcarnavalextraordinaria.Login

// Imports de librerías para construir la UI con Jetpack Compose y manejar estado, navegación y temas
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcarnavalextraordinaria.Navigation.Bars
import com.example.appcarnavalextraordinaria.R
import com.example.appcarnavalextraordinaria.ui.theme.AplicacionOrdinariaInterfacesTheme
import kotlinx.coroutines.launch

// Función composable que representa la pantalla de registro de usuario
@Composable
fun RegistroScreen(
    navController: NavController, // Controlador para navegación entre pantallas
    userViewModel: UserViewModel, // ViewModel que maneja la lógica y datos de usuario
    context: Context,              // Contexto Android para recursos y operaciones dependientes del sistema
    onRegistroOk: () -> Unit       // Callback que se ejecuta cuando el registro es exitoso
) {
    // Variables de estado para almacenar los valores introducidos en los campos de texto
    var email by remember { mutableStateOf("") }            // Email de usuario
    var username by remember { mutableStateOf("") }         // Nombre de usuario
    var password by remember { mutableStateOf("") }         // Contraseña
    var confirmPassword by remember { mutableStateOf("") }  // Confirmación de contraseña
    var errorMsg by remember { mutableStateOf("") }         // Mensaje de error para mostrar validaciones

    val scope = rememberCoroutineScope()                    // Scope para lanzar corutinas (tareas asíncronas)

    // Container principal que cubre toda la pantalla y establece el fondo
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Organiza los elementos en columna centrada horizontalmente y verticalmente
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)    // Padding alrededor para separar de los bordes
                .fillMaxWidth()    // Ocupa todo el ancho disponible
        ) {
            // Texto de título grande y negrita
            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp)) // Separador vertical

            // Campo de texto para email con icono de persona adelante
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },            // Actualiza estado al escribir
                label = { Text("Email") },                  // Etiqueta del campo
                singleLine = true,                          // Solo una línea
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp)) // Separador vertical

            // Campo para el nombre de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de Usuario") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para la contraseña con icono y texto oculto
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto introducido
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para confirmar contraseña, igual que el anterior
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // Si hay un mensaje de error, lo mostramos en rojo y centrado
            if (errorMsg.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para registrar, que al pulsar ejecuta lógica de validaciones y registro
            Button(
                onClick = {
                    when {
                        // Validación 1: Verifica que ningún campo esté vacío
                        email.isBlank() || username.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            errorMsg = "Por favor, completa todos los campos"
                        }
                        // Validación 2: Compara que ambas contraseñas sean iguales
                        password != confirmPassword -> {
                            errorMsg = "Las contraseñas no coinciden"
                        }
                        // Si pasa validaciones, intenta registrar el usuario
                        else -> {
                            errorMsg = "" // Limpia mensajes previos
                            scope.launch { // Lanza corutina para no bloquear UI
                                when (userViewModel.registerUser(username, password, context, email)) {
                                    RegisterResult.SUCCESS -> onRegistroOk()
                                    RegisterResult.INVALID_EMAIL -> errorMsg = "Formato de email inválido"
                                    RegisterResult.USER_EXISTS -> errorMsg = "El usuario ya existe"
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp) // Bordes redondeados para estilo moderno
            ) {
                Text(
                    text = "Registrar",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver a la pantalla principal sin registrar usuario
            OutlinedButton(
                onClick = { navController.navigate("main") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Volver")
            }
        }
    }
}