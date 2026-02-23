package com.ossacontrol.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User

// Este ViewModel se encarga únicamente de la lógica de la pantalla del Alumno.
class StudentViewModel : ViewModel() {
    // Instancia de la base de datos para poder leer documentos.
    private val db = FirebaseFirestore.getInstance()
    // Instancia de la autenticación para saber QUIÉN ha iniciado sesión.
    private val auth = FirebaseAuth.getInstance()

    // Creamos un estado privado para guardar los datos del alumno una vez cargados.
    private val _studentData = mutableStateOf<User?>(null)
    // Exponemos los datos del alumno de forma pública y de solo lectura para la UI.
    val studentData: State<User?> = _studentData

    // Función para cargar los datos del alumno que tiene la sesión iniciada (R04F01T01).
    fun loadCurrentStudentData() {
        // Obtenemos el ID único del usuario que ha hecho login.
        val userId = auth.currentUser?.uid

        // Si tenemos un ID válido, procedemos a buscar sus datos.
        if (userId != null) {
            // Buscamos en la colección "users" el documento que tenga exactamente ese ID.
            db.collection("users").document(userId)
                // Usamos addSnapshotListener para que los datos se refresquen solos si cambian en la nube.
                .addSnapshotListener { snapshot, error ->
                    // Si hay un error de conexión, no hacemos nada.
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    // Si el documento existe, lo convertimos a nuestro objeto User.
                    if (snapshot != null && snapshot.exists()) {
                        _studentData.value = snapshot.toObject(User::class.java)
                    }
                }
        }
    }
}