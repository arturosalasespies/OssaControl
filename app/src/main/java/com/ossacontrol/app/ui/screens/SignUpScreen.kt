package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsMartialArts // Icono de artes marciales para el logo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    // Función principal para registrar al usuario en Firebase y Firestore
    fun signUp() {
        error = null
        loading = true
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: ""
                    
                    // Lógica de roles: si es este email, será admin. Si no, alumno.
                    val miRol = if (email.trim().lowercase() == "admin@ossa.com") "admin" else "alumno"

                    val nuevoUsuario = User(
                        id = userId,
                        nombre = nombre,
                        email = email.trim(),
                        rol = miRol
                    )

                    db.collection("users").document(userId)
                        .set(nuevoUsuario)
                        .addOnSuccessListener {
                            loading = false
                            onSignUpSuccess()
                        }
                        .addOnFailureListener {
                            loading = false
                            error = "Error al guardar perfil"
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
                .padding(32.dp)
                .fillMaxSize(),
            // Centramos todo para que sea coherente con el Login
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Icono representativo (Logo temporal)
            Icon(
                imageVector = Icons.Default.SportsMartialArts,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            // 2. Títulos
            Text("NUEVA CUENTA", style = MaterialTheme.typography.headlineMedium)
            Text("Únete a la academia", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.height(32.dp))

            // 3. Campo de Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre completo") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(12.dp))

            // 4. Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            Spacer(Modifier.height(12.dp))

            // 5. Campo de Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña (mín. 6)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = MaterialTheme.shapes.medium
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(32.dp))

            // 6. Botón Registrarse
            Button(
                onClick = { signUp() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && nombre.isNotBlank() && email.isNotBlank() && pass.length >= 6,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (loading) "CREANDO PERFIL..." else "REGISTRARSE")
            }

            Spacer(Modifier.height(16.dp))

            // Botón para volver atrás
            TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Entra aquí")
            }
        }
    }
}