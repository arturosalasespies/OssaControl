package com.ossacontrol.app.viewmodel


//Esto nos sirve para conectar el Login con Firestore para que la app sepa quién es Admin y quién es Alumno.

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Este estado guardará el rol del usuario ("admin" o "alumno")
    private val _userRole = mutableStateOf<String?>(null)
    val userRole: State<String?> = _userRole

    // Función para mirar en la base de datos qué rol tiene el usuario logueado
    fun checkUserRole() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val rolEncontrado = document.getString("rol")
                        _userRole.value = rolEncontrado
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        _userRole.value = null
    }
}