package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // Login correcto
    onGoToSignUp: () -> Unit // Ir a Registro
) {

    // Estados de la pantalla
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Iniciar sesión
    fun login() {
        error = null
        loading = true
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) onLoginSuccess()
                else error = task.exception?.localizedMessage ?: "Error al iniciar sesión"
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
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Campo email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            // Campo password
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                // Oculta el texto de la contraseña
                visualTransformation = PasswordVisualTransformation()
            )

            // Si hay error, lo mostramos
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // Botón login
            Button(
                onClick = { login() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && email.isNotBlank() && pass.isNotBlank()
            ) {
                Text(if (loading) "Entrando..." else "Entrar")
            }

            // Botón para ir a Registro
            TextButton(onClick = onGoToSignUp, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
        }
    }
}