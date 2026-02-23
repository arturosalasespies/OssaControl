package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsMartialArts // Icono representativo de lucha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToSignUp: () -> Unit
) {
    // Estados para los campos de texto
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Función interna para conectar con Firebase y loguearse
    fun login() {
        error = null
        loading = true
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email.trim(), pass)
            .addOnCompleteListener { task ->
                loading = false
                if (task.isSuccessful) onLoginSuccess()
                else error = task.exception?.localizedMessage ?: "Credenciales incorrectas"
            }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(32.dp)
                .fillMaxSize(),
            // Centramos todo el contenido verticalmente
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Icono de la Aplicación (Símbolo de lucha)
            Icon(
                imageVector = Icons.Default.SportsMartialArts,
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp), // Tamaño grande para que parezca un logo
                tint = MaterialTheme.colorScheme.primary // Usa el color principal (Negro/Blanco)
            )

            Spacer(Modifier.height(16.dp))

            // 2. Título de bienvenida
            Text("OSSA CONTROL", style = MaterialTheme.typography.headlineLarge)
            Text("Gestión de Academia", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.height(48.dp))

            // 3. Campo de Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium // Bordes redondeados modernos
            )

            Spacer(Modifier.height(12.dp))

            // 4. Campo de Contraseña
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = MaterialTheme.shapes.medium
            )

            // Mensaje de error si falla algo
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(32.dp))

            // 5. Botón de Entrar
            Button(
                onClick = { login() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && email.isNotBlank() && pass.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (loading) "Cargando..." else "ENTRAR")
            }

            Spacer(Modifier.height(16.dp))

            // 6. Botón para crear cuenta
            TextButton(onClick = onGoToSignUp, modifier = Modifier.fillMaxWidth()) {
                Text("¿Aún no tienes cuenta? Regístrate aquí")
            }
        }
    }
}