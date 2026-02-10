package com.ossacontrol.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User

class AdminViewModel : ViewModel() {
    // Instancia de la base de datos de Firestore
    private val db = FirebaseFirestore.getInstance()

    // Lista observable de usuarios para que la UI se actualice automáticamente
    private val _usuarios = mutableStateOf<List<User>>(emptyList())
    val usuarios: State<List<User>> = _usuarios

    // Trae a todos los alumnos y escucha cambios en tiempo real (R02F01T02)
    fun obtenerAlumnos() {
        db.collection("users")
            .whereEqualTo("rol", "alumno")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                // Mapeamos los documentos de Firebase a nuestra clase User
                val lista = snapshot?.documents?.mapNotNull { it.toObject(User::class.java) } ?: emptyList()
                _usuarios.value = lista
            }
    }

    // Crea un nuevo alumno en la base de datos (R02F01T03)
    fun registrarAlumno(nombre: String, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError("Error al registrar: ${exception.localizedMessage}")
            }
    }

    // Actualiza datos editables como el cinturón (R02F01)
    fun actualizarAlumno(user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("users")
            .document(user.email)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError("Error al actualizar: ${exception.localizedMessage}")
            }
    }

    // Incrementa el contador de clases asistidas en +1 (R03F01T02)
    fun registrarAsistencia(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("users")
            .document(email)
            // FieldValue.increment(1) es la forma más segura de sumar 1 en Firebase
            .update("clasesAsistidas", FieldValue.increment(1))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError("Error al marcar asistencia: ${exception.localizedMessage}")
            }
    }
}