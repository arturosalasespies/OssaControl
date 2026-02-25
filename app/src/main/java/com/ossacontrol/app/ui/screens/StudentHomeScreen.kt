package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: StudentHomeScreen.kt
 * Pantalla de perfil del alumno (vista del propio alumno).
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial
 *   - Alberto (23/02): Rediseño visual con tarjetas (cinturón y asistencias)
 *   - Arturo (con Claude Code) (25/02):
 *     · Añadida barra visual con el color real del cinturón (como en StudentDetailScreen)
 *     · Mostrar stripes/grados del alumno
 *     · Mostrar fecha desde cuándo tiene ese cinturón
 *     · Mostrar días desde la última asistencia (activo/inactivo)
 * ============================================
 *
 * NOTA TÉCNICA sobre colorDelCinturon() y colorTextoDelCinturon():
 *   Esas funciones están definidas en StudentDetailScreen.kt, en el mismo package
 *   (com.ossacontrol.app.ui.screens). En Kotlin, las funciones de nivel de paquete
 *   en el mismo paquete son accesibles desde este archivo sin necesidad de importar.
 *
 * NOTA: El parámetro email fue eliminado (Limpieza - Arturo 25/02/2026) porque
 *   no se usaba en el cuerpo de la función. Los datos se cargan vía StudentViewModel,
 *   que obtiene el UID del usuario desde FirebaseAuth internamente.
 */

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.viewmodel.AdminViewModel
import com.ossacontrol.app.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(onLogout: () -> Unit) {

    // ViewModel que carga los datos del alumno logueado desde Firestore
    val viewModel: StudentViewModel = viewModel()

    // Escuchamos los datos del alumno: si cambian, la pantalla se actualiza sola
    val alumno by viewModel.studentData

    // Al entrar, pedimos al ViewModel que cargue los datos del alumno actual
    LaunchedEffect(Unit) {
        viewModel.loadCurrentStudentData()
    }

    // scrollState para que la pantalla sea desplazable si hay mucho contenido
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("MI PERFIL", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (alumno != null) {

                // ===== SECCIÓN 1: Nombre del alumno =====
                Text(
                    text = alumno!!.nombre.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = alumno!!.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ===== SECCIÓN 2: Barra visual del cinturón =====
                // Muestra el cinturón con su color real, igual que en la pantalla del admin.
                // colorDelCinturon() y colorTextoDelCinturon() están en StudentDetailScreen.kt,
                // mismo paquete → accesibles sin importar.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorDelCinturon(alumno!!.cinturon))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CINTURÓN ${alumno!!.cinturon.uppercase()} · ${alumno!!.grados} STRIPE${if (alumno!!.grados != 1) "S" else ""}",
                        color = colorTextoDelCinturon(alumno!!.cinturon),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ===== SECCIÓN 3: Fecha desde cuándo tiene este cinturón =====
                val textoCinturon = if (alumno!!.fechaInicioCinturon > 0L) {
                    // Formateamos la fecha en formato día/mes/año (español)
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
                    "Cinturón desde: ${formato.format(Date(alumno!!.fechaInicioCinturon))}"
                } else {
                    "Cinturón desde: fecha no registrada"
                }
                Text(
                    text = textoCinturon,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ===== SECCIÓN 4: Tarjeta de asistencias =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ASISTENCIAS TOTALES",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // El número en grande es lo más visible de la pantalla
                        Text(
                            text = "${alumno!!.clasesAsistidas}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "clases completadas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 5: Estado de actividad =====
                // Mostramos cuántos días lleva el alumno sin asistir,
                // o si está activo (asistió recientemente)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ESTADO",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            if (alumno!!.ultimaAsistencia == 0L) {
                                // Sin asistencias registradas todavía
                                Text(
                                    text = "Sin asistencias registradas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            } else {
                                val ahora = System.currentTimeMillis()
                                val diasDesdeUltima = ((ahora - alumno!!.ultimaAsistencia) / (1000L * 60 * 60 * 24)).toInt()
                                val umbralInactivo = AdminViewModel.DIAS_INACTIVIDAD

                                if (diasDesdeUltima < umbralInactivo) {
                                    // Activo: ha venido recientemente
                                    Text(
                                        text = "ACTIVO",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Última asistencia hace $diasDesdeUltima día${if (diasDesdeUltima != 1) "s" else ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                } else {
                                    // Inactivo: lleva demasiado tiempo sin venir
                                    Text(
                                        text = "INACTIVO",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Llevas $diasDesdeUltima días sin asistir",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }

                // Separador antes del botón de logout
                Spacer(modifier = Modifier.height(32.dp))

                // ===== BOTÓN CERRAR SESIÓN =====
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("CERRAR SESIÓN", fontWeight = FontWeight.Bold)
                }

            } else {
                // Mientras los datos se cargan, mostramos indicador de progreso
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando tu perfil...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
