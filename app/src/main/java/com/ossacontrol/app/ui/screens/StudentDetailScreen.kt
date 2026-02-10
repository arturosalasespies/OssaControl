package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexzh.qrcode.compose.QrCodeImage // Importamos el componente de la librería QR
import com.ossacontrol.app.model.User
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(studentEmail: String, onBack: () -> Unit) {
    // Instanciamos el ViewModel para poder usar la función de actualizar y la de asistencia
    val viewModel: AdminViewModel = viewModel()
    
    // Buscamos los datos del alumno actual en nuestra lista
    val alumno = viewModel.usuarios.value.find { it.email == studentEmail }

    // Estados para controlar qué se muestra en pantalla
    var cinturon by remember { mutableStateOf(alumno?.cinturon ?: "") }
    var mostrarDialogoQR by remember { mutableStateOf(false) } // Controla si el QR está visible

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Alumno") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón en la barra superior para abrir el QR (R03F01T02)
                    IconButton(onClick = { mostrarDialogoQR = true }) {
                        Icon(Icons.Default.QrCode, contentDescription = "Generar QR")
                    }
                }
            )
        }
    ) { padding ->
        if (alumno != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                // Info básica del alumno
                Text(text = alumno.nombre, style = MaterialTheme.typography.headlineMedium)
                Text(text = alumno.email, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
                
                Spacer(modifier = Modifier.height(32.dp))

                // Selector de cinturones
                Text(text = "Cinturón: $cinturon", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    FilterChip(selected = cinturon == "Blanco", onClick = { cinturon = "Blanco" }, label = { Text("Blanco") })
                    FilterChip(selected = cinturon == "Azul", onClick = { cinturon = "Azul" }, label = { Text("Azul") })
                    FilterChip(selected = cinturon == "Morado", onClick = { cinturon = "Morado" }, label = { Text("Morado") })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cuadro resumen de asistencia (R03)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "CLASES ASISTIDAS", style = MaterialTheme.typography.labelMedium)
                        Text(text = "${alumno.clasesAsistidas}", style = MaterialTheme.typography.displayMedium)
                        
                        // Botón de "Prueba" para simular el escaneo del QR (R03F01T02P01)
                        Button(
                            onClick = { 
                                viewModel.registrarAsistencia(alumno.email, {}, {}) 
                            },
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Simular Escaneo (+1)")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Guardar cambios de cinturón
                Button(
                    onClick = {
                        val alumnoEditado = alumno.copy(cinturon = cinturon)
                        viewModel.actualizarAlumno(alumnoEditado, { onBack() }, {})
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Guardar Cambios")
                }
            }

            // --- DIÁLOGO DEL QR (Ventana emergente) ---
            if (mostrarDialogoQR) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoQR = false },
                    confirmButton = {
                        TextButton(onClick = { mostrarDialogoQR = false }) { Text("Cerrar") }
                    },
                    title = { Text("QR de Asistencia", modifier = Modifier.fillMaxWidth()) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            // Este componente genera el QR automáticamente con el email del alumno
                            QrCodeImage(
                                data = "asistencia:${alumno.email}",
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pasa este código por el lector de la academia", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                )
            }
        }
    }
}