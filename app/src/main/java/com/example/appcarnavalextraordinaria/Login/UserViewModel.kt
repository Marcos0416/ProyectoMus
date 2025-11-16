package com.example.appcarnavalextraordinaria.Login

import android.content.Context
import androidx.lifecycle.*
import com.example.appcarnavalextraordinaria.Data.UserDao
import com.example.appcarnavalextraordinaria.Data.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(private val userDao: UserDao) : ViewModel() {
    private val _loggedInUser = MutableLiveData<UserEntity?>()
    val loggedInUser: LiveData<UserEntity?> get() = _loggedInUser

    fun login(username: String, password: String, context: Context) {
        viewModelScope.launch {
            val user = userDao.getUserByUsername(username)

            if (user != null) {
                // 🔒 Hasheamos la contraseña introducida
                val hashedInput = PasswordUtils.hashPassword(password)

                // Comparamos con la contraseña hasheada almacenada
                if (user.password == hashedInput) {
                    android.util.Log.d("DEBUG", "Usuario autenticado -> id=${user.id}, username=${user.username}")

                    saveSession(context, user)   // guarda ID + username
                    _loggedInUser.postValue(user)
                } else {
                    android.util.Log.d("DEBUG", "Contraseña incorrecta para usuario=$username")
                    _loggedInUser.postValue(null)
                }
            } else {
                android.util.Log.d("DEBUG", "Usuario no encontrado: $username")
                _loggedInUser.postValue(null)
            }
        }
    }



    fun getUserId(context: Context): Int {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val id = prefs.getInt("user_id", 0)
        android.util.Log.d("DEBUG", "Leyendo user_id desde prefs -> $id")
        return id
    }



    suspend fun registerUser(username: String, password: String, context: Context,email: String,): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser == null) {

            val hashedPassword = PasswordUtils.hashPassword(password)
            val newUser = UserEntity(
                username = username,
                password = hashedPassword,
                email = email
            )


            // 👇 Solo una inserción
            val newId = userDao.insertUser(newUser).toInt()

            // Guardar sesión con el ID real
            saveSession(context, newUser.copy(id = newId))
            _loggedInUser.postValue(newUser.copy(id = newId))
            return true
        }
        return false
    }


    fun saveSession(context: Context, user: UserEntity) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("session_user", user.username)
            .putInt("user_id", user.id)
            .apply()
        android.util.Log.d("DEBUG", "Sesión guardada -> username=${user.username}, id=${user.id}")
    }


    fun getSession(context: Context): String? {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("session_user", null)
    }
    class UserViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                return UserViewModel(userDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }





    fun logout(context: Context) {
        _loggedInUser.value = null
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("session_user").apply()
    }

}