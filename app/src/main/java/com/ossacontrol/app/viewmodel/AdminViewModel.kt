package com.ossacontrol.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User
import com.google.firebase.auth.FirebaseAuth

class AdminViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    //Creamos una lista que la pantalla estará vigilando
    private val _usuarios = mutableStateOf<List<User>>(emptyList())
    val usuarios: State<List<User>> = _usuarios

    //Función para traer a todos los alumnos de Firebase
    fun obtenerAlumnos() {
        db.collection("users")
            .whereEqualTo("rol", "alumno") // Solo queremos traer a los alumnos, no a otros admin
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                // Convertimos los documentos de Firebase en nuestra lista de objetos User
                val lista = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
                _usuarios.value = lista
            }
    }

    fun registrarAlumno(nombre: String, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        // Creamos el objeto del alumno aquí dentro para que pueda usar 'nombre' y 'email'
        val nuevoAlumno = User(
            id = email, 
            nombre = nombre,
            email = email,
            rol = "alumno",
            cinturon = "Blanco",
            clasesAsistidas = 0
        )

        db.collection("users")
            .document(email)
            .set(nuevoAlumno)
            .addOnSuccessListener {
                // Si sale bien, avisamos a la pantalla para que cierre el formulario
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Generamos el mensaje de error detallado
                onError("Error al registrar en la base de datos: ${exception.localizedMessage}")
            }
    }
}