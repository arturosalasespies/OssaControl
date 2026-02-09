package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(onLogout: () -> Unit) { // Cerrar sesión

    // Usuario actual
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text("Home", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))

            // Mostramos el email del usuario
            Text("Sesión iniciada como: ${user?.email ?: "-"}")

            Spacer(Modifier.height(16.dp))

            // Botón para cerrar sesión
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}