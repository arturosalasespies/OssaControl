package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(email: String, onLogout: () -> Unit) {
    // 1. Obtenemos el ViewModel que nos da los datos del alumno
    val viewModel: StudentViewModel = viewModel()
    
    // 2. Escuchamos los datos del alumno: si cambian, esta pantalla se reconstruye sola
    val alumno by viewModel.studentData

    // 3. Al entrar por primera vez, le pedimos al ViewModel que cargue los datos de Firestore
    LaunchedEffect(Unit) {
        viewModel.loadCurrentStudentData()
    }

    Scaffold(
        topBar = {
            // Barra superior con el título "Mi Perfil"
            TopAppBar(title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Si los datos del alumno ya están listos, los mostramos
            if (alumno != null) {
                // --- TARJETA DE PROGRESO ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    // Usamos el color de "superficie" (Gris oscuro/claro) que definimos en el tema
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "NIVEL ACTUAL", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Mostramos el cinturón con una tipografía grande y destacada
                        Text(text = "Cinturón ${alumno?.cinturon}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- TARJETA DE ASISTENCIAS ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "ASISTENCIAS TOTALES", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        // El número de clases en grande para que sea lo más visible
                        Text(text = "${alumno?.clasesAsistidas}", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.ExtraBold)
                        Text(text = "clases completadas", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }

                // Empujamos el botón de logout hacia abajo del todo
                Spacer(modifier = Modifier.weight(1f))

                // --- BOTÓN CERRAR SESIÓN ---
                Button(
                    onClick = onLogout, 
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    // Usamos el color "error" (rojo) para indicar una acción importante/destructiva
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("CERRAR SESIÓN")
                }

            } else {
                // Mientras los datos se cargan, mostramos una rueda de progreso
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando tu progreso...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}