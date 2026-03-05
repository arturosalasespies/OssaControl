/**
 * ============================================
 * Archivo: StudentDetailScreen.kt
 * Pantalla de detalle y gestión de un alumno (vista ADMIN/Profesor).
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial con 3 cinturones
 *   - Arturo  (14/02): 5 cinturones, stripes, colores reales
 *   - Alberto (25/02): Refactor estilo visual (uppercase, bold, spacing)
 *   - Arturo  (25/02): FUSIÓN DEFINITIVA — combina estilo de Alberto
 *     con funcionalidades de Arturo:
 *     · 5 cinturones BJJ con colores reales (IBJJF)
 *     · Selector de stripes/grados (0-4)
 *     · Barra visual del cinturón actual
 *     · Reset de stripes al cambiar cinturón
 *     · QR eliminado por indicación del profesor (futuro)
 *     · Documentación completa para el equipo
 *   - Alejandra (25/02): Reestructurado para:
 *     - Mostrar la última asistencia del alumno con fecha y hora
 *     - Mostrar historial de las últimas 10 asistencias
 *     - Formatear timestamp
 *     - Scroll en asistencia
 * ============================================
 */
package com.ossacontrol.app.ui.screens

// --- IMPORTS DE ANDROID Y COMPOSE ---
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn

// Imports para Firestore + fecha/hora + modelo asistencia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// --- IMPORTS DEL PROYECTO ---
import com.ossacontrol.app.viewmodel.AdminViewModel
import com.ossacontrol.app.model.Asistencia

// ============================================
// FUNCIONES AUXILIARES
// ============================================

fun colorDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color(0xFFF5F5F5)
        "Azul" -> Color(0xFF1565C0)
        "Morado" -> Color(0xFF6A1B9A)
        "Marrón" -> Color(0xFF5D4037)
        "Negro" -> Color(0xFF212121)
        else -> Color.Gray
    }
}

fun colorTextoDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color.Black
        else -> Color.White
    }
}

// Formatear Timestamp -> "dd/MM/yyyy HH:mm"
private fun formatTimestamp(ts: Timestamp): String {
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "ES"))
    return formato.format(ts.toDate())
}

// ============================================
// PANTALLA PRINCIPAL
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(studentEmail: String, onBack: () -> Unit) {

    // ViewModel que conecta con Firebase para leer/escribir datos de alumnos
    val viewModel: AdminViewModel = viewModel()

    // Cargamos los alumnos desde Firebase al entrar en esta pantalla.
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Buscamos al alumno por email dentro de la lista cargada
    val alumnosData by viewModel.usuarios
    val alumno = alumnosData.find { it.email == studentEmail }

    // --- Estados locales (borrador del admin) ---
    var cinturon by remember { mutableStateOf(alumno?.cinturon ?: "Blanco") }
    var grados by remember { mutableStateOf(alumno?.grados ?: 0) }

    // Snackbar para mostrar errores de Firebase al usuario
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cuando los datos del alumno llegan de Firebase, actualizamos el borrador
    LaunchedEffect(alumno) {
        if (alumno != null) {
            cinturon = alumno.cinturon
            grados = alumno.grados
        }
    }

    // Lista oficial de cinturones
    val cinturones = listOf("Blanco", "Azul", "Morado", "Marrón", "Negro")

    // Estado para historial de asistencias del alumno (últimas 10)
    val db = FirebaseFirestore.getInstance()
    var asistencias by remember { mutableStateOf<List<Asistencia>>(emptyList()) }

    // Escuchamos asistencias cuando ya sabemos el alumno.id
    LaunchedEffect(alumno?.id) {
        val alumnoId = alumno?.id ?: return@LaunchedEffect

        db.collection("users")
            .document(alumnoId)
            .collection("asistencias")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // No spameamos al usuario, pero dejamos log
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Asistencia::class.java)
                } ?: emptyList()

                // serverTimestamp puede venir null un instante
                asistencias = lista.filter { it.timestamp != null }
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
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (alumno != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ===== SECCIÓN 1: Información del alumno =====
                Text(
                    text = alumno.nombre.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = alumno.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ===== SECCIÓN 2: Barra visual del cinturón =====
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorDelCinturon(cinturon))
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "CINTURÓN ${cinturon.uppercase()} · $grados STRIPES",
                        color = colorTextoDelCinturon(cinturon),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 3: Selector de cinturones =====
                Text(
                    text = "CAMBIAR CINTURÓN",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    cinturones.forEach { nombreCinturon ->
                        FilterChip(
                            selected = cinturon == nombreCinturon,
                            onClick = {
                                cinturon = nombreCinturon
                                grados = 0
                            },
                            label = {
                                Text(
                                    text = nombreCinturon.take(3).uppercase(),
                                    color = colorTextoDelCinturon(nombreCinturon),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = colorDelCinturon(nombreCinturon).copy(alpha = 0.3f),
                                selectedContainerColor = colorDelCinturon(nombreCinturon)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 4: Selector de stripes (grados) =====
                Text(
                    text = "STRIPES (GRADOS): $grados",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (0..4).forEach { numGrado ->
                        FilterChip(
                            selected = grados == numGrado,
                            onClick = { grados = numGrado },
                            label = {
                                Text(
                                    "$numGrado",
                                    fontWeight = if (grados == numGrado) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 5: Tarjeta de asistencia (total + botón) =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CLASES REGISTRADAS",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "${alumno.clasesAsistidas}",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                viewModel.registrarAsistencia(
                                    alumnoId = alumno.id,
                                    onSuccess = { /* ok */ },
                                    onError = { mensaje ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Error: $mensaje")
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("AÑADIR ASISTENCIA +1")
                        }
                    }
                }

                // Última asistencia (fecha y hora)
                Spacer(modifier = Modifier.height(16.dp))
                val ultimaTs = asistencias.firstOrNull()?.timestamp
                if (ultimaTs != null) {
                    Text(
                        text = "Última asistencia: ${formatTimestamp(ultimaTs)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        text = "Última asistencia: (sin registros)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Historial de asistencias (últimas 10)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "HISTORIAL (ÚLTIMAS ${asistencias.size})",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))

                // ===== HISTORIAL DE ASISTENCIAS (SCROLL INTERNO) =====
                val historyScroll = rememberScrollState()

                if (asistencias.isEmpty()) {
                    Text(
                        text = "Aún no hay asistencias registradas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .heightIn(max = 220.dp)      // Límite visual elegante
                                .verticalScroll(historyScroll)
                                .padding(16.dp)
                        ) {
                            asistencias.forEachIndexed { index, a ->
                                val ts = a.timestamp ?: return@forEachIndexed

                                Text(
                                    text = "• ${formatTimestamp(ts)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                if (index < asistencias.lastIndex) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }

                // Empuja el botón de guardar al fondo de la pantalla
                Spacer(modifier = Modifier.weight(1f))

                // ===== SECCIÓN 6: Botón guardar cambios =====
                Button(
                    onClick = {
                        val alumnoEditado = alumno.copy(
                            cinturon = cinturon,
                            grados = grados
                        )
                        viewModel.actualizarAlumno(
                            user = alumnoEditado,
                            onSuccess = { onBack() },
                            onError = { mensaje ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al guardar: $mensaje")
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(modifier = Modifier.size(50.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando datos del alumno...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}