package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    // Estados para guardar lo que escribe el usuario
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Función principal para registrar al usuario
    fun signUp() {
        error = null
        loading = true
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        // 1. Creamos el usuario en Authentication (Email y Pass)
        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: ""
                    
                    // 2. Definimos el rol: si el email es este, será admin. Si no, alumno.
                    // CAMBIA "admin@ossa.com" por el email que tú quieras usar.
                    val miRol = if (email.trim().lowercase() == "admin@ossa.com") "admin" else "alumno"

                    // 3. Creamos el objeto usuario para guardarlo en la base de datos (Firestore)
                    val nuevoUsuario = User(
                        id = userId,
                        nombre = nombre,
                        email = email.trim(),
                        rol = miRol
                    )

                    // 4. Guardamos los datos en la colección "users" usando su UID
                    db.collection("users").document(userId)
                        .set(nuevoUsuario)
                        .addOnSuccessListener {
                            loading = false
                            onSignUpSuccess() // Vamos a la Home correspondiente
                        }
                        .addOnFailureListener {
                            loading = false
                            error = "Error al guardar en base de datos"
                        }
                } else {
                    loading = false
                    error = task.exception?.localizedMessage ?: "Error al crear cuenta"
                }
            }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Regístrate en OSSA Control", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            // Campo para el nombre (nuevo)
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre completo") },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // Campo para el email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // Campo para la contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            // Botón para crear cuenta
            Button(
                onClick = { signUp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && nombre.isNotBlank() && email.isNotBlank() && pass.length >= 6
            ) {
                Text(if (loading) "Creando perfil..." else "Registrarse")
            }

            TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Entra aquí")
            }
        }
    }
}