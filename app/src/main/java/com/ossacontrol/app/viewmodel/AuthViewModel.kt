package com.ossacontrol.app.viewmodel

// Esto nos sirve para conectar el Login con Firestore para que la app sepa quién es Admin y quién es Alumno.

import android.util.Log
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

    // Función para mirar en la base de datos qué rol tiene el usuario logueado.
    // Busca primero por UID (alumnos registrados vía SignUpScreen).
    // Si no encuentra documento, busca por email (alumnos creados por el admin,
    // cuyo documento usa el email como ID en lugar del UID).
    fun checkUserRole() {
        val userId = auth.currentUser?.uid
        val userEmail = auth.currentUser?.email
        if (userId == null) return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    _userRole.value = document.getString("rol")
                } else if (userEmail != null) {
                    // Fallback: el alumno fue creado por el admin y su documento
                    // tiene el email como ID, no el UID
                    db.collection("users")
                        .whereEqualTo("email", userEmail)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { query ->
                            val doc = query.documents.firstOrNull()
                            if (doc != null) {
                                _userRole.value = doc.getString("rol")
                            } else {
                                Log.w("AuthViewModel", "No se encontró usuario con email: $userEmail")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("AuthViewModel", "Error buscando por email: ${exception.message}")
                        }
                } else {
                    Log.w("AuthViewModel", "No se encontró documento de usuario para UID: $userId")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthViewModel", "Error al obtener rol del usuario: ${exception.message}")
            }
    }

    fun logout() {
        auth.signOut()
        _userRole.value = null
    }
}