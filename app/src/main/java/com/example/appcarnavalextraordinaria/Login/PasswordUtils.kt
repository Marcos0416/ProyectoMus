package com.example.appcarnavalextraordinaria.Login



import java.security.MessageDigest

// PasswordUtils: Utilidad para hashear contraseñas con SHA-256
object PasswordUtils {

    // Función que convierte la contraseña en un hash hexadecimal SHA-256
    fun hashPassword(password: String): String {
        // Convertir la contraseña en bytes para procesarla
        val bytes = password.toByteArray()
        // Obtener instancia del algoritmo SHA-256
        val md = MessageDigest.getInstance("SHA-256")
        // Calcular el hash de la contraseña
        val digest = md.digest(bytes)
        // Convertir los bytes hash a una cadena hexadecimal
        return digest.joinToString("") { "%02x".format(it) }
    }
}
