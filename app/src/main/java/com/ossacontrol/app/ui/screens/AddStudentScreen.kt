package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(onStudentAdded: (String, String) -> Unit, onBack: () -> Unit) {
    // Definimos variables para guardar lo que el usuario escribe
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Registrar nuevo alumno") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Campo para el nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo para el email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email del alumno") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para guardar
            Button(
                onClick = {
                    if (nombre.isNotBlank() && email.isNotBlank()) {
                        onStudentAdded(nombre, email)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dar de alta")
            }

            TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    }
}