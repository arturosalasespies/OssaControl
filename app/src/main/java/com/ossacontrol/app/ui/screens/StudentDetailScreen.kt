package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
// import com.alexzh.qrcode.compose.QrCodeImage // ELIMINADO: Quitamos la librería que da problemas
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(studentEmail: String, onBack: () -> Unit) {
    val viewModel: AdminViewModel = viewModel()
    
    val alumnosData by viewModel.usuarios
    val alumno = alumnosData.find { it.email == studentEmail }

    var cinturon by remember { mutableStateOf("") }
    // var mostrarDialogoQR by remember { mutableStateOf(false) } // ELIMINADO: Ya no necesitamos controlar el diálogo

    LaunchedEffect(alumno) {
        if (alumno != null) {
            cinturon = alumno.cinturon
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETALLE ALUMNO", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
                // ELIMINADO: Quitamos el botón del QR de la barra superior
            )
        }
    ) { padding ->
        if (alumno != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = alumno.nombre.uppercase(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Text(text = alumno.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                
                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "CINTURÓN ACTUAL: $cinturon", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(selected = cinturon == "Blanco", onClick = { cinturon = "Blanco" }, label = { Text("BLANCO") })
                    FilterChip(selected = cinturon == "Azul", onClick = { cinturon = "Azul" }, label = { Text("AZUL") })
                    FilterChip(selected = cinturon == "Morado", onClick = { cinturon = "Morado" }, label = { Text("MORADO") })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "CLASES REGISTRADAS", style = MaterialTheme.typography.labelSmall)
                        Text(text = "${alumno.clasesAsistidas}", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Bold)
                        
                        Button(
                            onClick = { viewModel.registrarAsistencia(alumno.email, {}, {}) },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("AÑADIR ASISTENCIA +1")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val alumnoEditado = alumno.copy(cinturon = cinturon)
                        viewModel.actualizarAlumno(alumnoEditado, { onBack() }, {})
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("GUARDAR CAMBIOS")
                }
            }

            // ELIMINADO: Quitamos toda la ventana emergente (AlertDialog) del QR
        }
    }
}