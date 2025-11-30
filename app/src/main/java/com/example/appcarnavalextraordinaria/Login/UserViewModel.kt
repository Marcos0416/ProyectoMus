package com.example.appcarnavalextraordinaria.Login

import android.content.Context
import androidx.lifecycle.*
import com.example.appcarnavalextraordinaria.Data.UserDao
import com.example.appcarnavalextraordinaria.Data.UserEntity
import kotlinx.coroutines.launch
import android.util.Patterns

enum class RegisterResult {
    SUCCESS,
    INVALID_EMAIL,
    USER_EXISTS
}

// ViewModel que gestiona la lógica de usuario y autenticación
class UserViewModel(private val userDao: UserDao) : ViewModel() {

    // LiveData para exponer el usuario logueado a la UI de forma observable
    private val _loggedInUser = MutableLiveData<UserEntity?>()
    val loggedInUser: LiveData<UserEntity?> get() = _loggedInUser

    // Función para iniciar sesión
    fun login(username: String, password: String, context: Context) {
        // Lanzar corutina para no bloquear la interfaz
        viewModelScope.launch {
            // Buscar usuario en base de datos por nombre
            val user = userDao.getUserByUsername(username)

            if (user != null) {
                // Hashear contraseña introducida para comparar con almacenada (seguridad)
                val hashedInput = PasswordUtils.hashPassword(password)

                if (user.password == hashedInput) {
                    // Debug log para confirmar usuario autenticado
                    android.util.Log.d("DEBUG", "Usuario autenticado -> id=${user.id}, username=${user.username}")

                    // Guardar sesión en preferencias y actualizar LiveData con usuario
                    saveSession(context, user)
                    _loggedInUser.postValue(user)
                } else {
                    // Contraseña incorrecta - limpiar LiveData y loguear error
                    android.util.Log.d("DEBUG", "Contraseña incorrecta para usuario=$username")
                    _loggedInUser.postValue(null)
                }
            } else {
                // Usuario no encontrado - limpiar LiveData y loguear error
                android.util.Log.d("DEBUG", "Usuario no encontrado: $username")
                _loggedInUser.postValue(null)
            }
        }
    }

    // Leer ID de usuario guardado en SharedPreferences - persistencia ligera
    fun getUserId(context: Context): Int {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = prefs.getInt("user_id", 0)
        android.util.Log.d("DEBUG", "Leyendo user_id desde prefs -> $id")
        return id
    }



    suspend fun registerUser(
        username: String,
        password: String,
        context: Context,
        email: String
    ): RegisterResult {

        // Validar email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return RegisterResult.INVALID_EMAIL
        }

        // Comprobar si el usuario existe
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser != null) {
            return RegisterResult.USER_EXISTS
        }

        // Crear el usuario
        val hashedPassword = PasswordUtils.hashPassword(password)
        val newUser = UserEntity(username = username, password = hashedPassword, email = email)
        val newId = userDao.insertUser(newUser).toInt()

        saveSession(context, newUser.copy(id = newId))
        _loggedInUser.postValue(newUser.copy(id = newId))

        return RegisterResult.SUCCESS
    }

    // Guardar datos de sesión en SharedPreferences para persistencia entre reinicios
    fun saveSession(context: Context, user: UserEntity) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("session_user", user.username)
            .putInt("user_id", user.id)
            .apply()
        android.util.Log.d("DEBUG", "Sesión guardada -> username=${user.username}, id=${user.id}")
    }

    // Obtener el nombre de usuario almacenado, si hay sesión activa
    fun getSession(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("session_user", null)
    }

    // Factory para crear UserViewModel con parámetros personalizados (se pasa DAO)
    class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // Cerrar sesión: limpiar LiveData y borrar de SharedPreferences
    fun logout(context: Context) {
        _loggedInUser.value = null
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("session_user").apply()
    }
}