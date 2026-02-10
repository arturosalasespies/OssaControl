package com.ossacontrol.app.ui.screens

//esto lo creamos para separar al profesor del alumno

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StudentHomeScreen(email: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Perfil de ALUMNO", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Hola: $email")
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onLogout) {
            Text("Cerrar Sesión")
        }
    }
}