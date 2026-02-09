package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SignUpScreen(
    onSignUpSuccess: () -> Unit, // Login correcto
    onBackToLogin: () -> Unit // Ir a registro
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Crear cuenta con email y password
    fun signUp() {
        error = null
        loading = true
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) onSignUpSuccess()
                else error = task.exception?.localizedMessage ?: "Error al crear cuenta"
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
            Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
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
                label = { Text("Contraseña (mín. 6)") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            // Si hay error, lo mostramos
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            // Botón crear cuenta
            Button(
                onClick = { signUp() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading && email.isNotBlank() && pass.length >= 6
            ) {
                Text(if (loading) "Creando..." else "Crear cuenta")
            }

            // Botón para volver a Login
            TextButton(onClick = onBackToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("Volver")
            }
        }
    }
}