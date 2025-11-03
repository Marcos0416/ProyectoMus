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
            val user = userDao.authenticateUser(username, password)
            if (user != null) {
                saveSession(context, user.username)
                _loggedInUser.postValue(user)
            } else {
                _loggedInUser.postValue(null)
            }
        }
    }


    suspend fun registerUser(username: String, password: String, context: Context): Boolean {
        val existingUser = userDao.getUserByUsername(username)
        if (existingUser == null) {
            val newUser = UserEntity(username = username, password = password, email = "")
            userDao.insertUser(newUser)
            saveSession(context, username)
            _loggedInUser.postValue(newUser)
            return true
        }
        return false
    }

    fun saveSession(context: Context, username: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("session_user", username).apply()
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