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
 *   - Alejandra (09/03):
 *     · Añadido campo de "notas" del alumno
 *     · Fix de scroll usando LazyColumn
 *     · Historial editado para no tener scrolls dentro de scrolls
 *     · Campo de última asistencia eliminado por redundancia con el historial
 *     · Ajustado espacio agrupando título + contenido
 *
 * ============================================
 */
package com.ossacontrol.app.ui.screens

// --- IMPORTS DE ANDROID Y COMPOSE ---
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    var notas by remember { mutableStateOf(alumno?.notas ?: "") }

    // Snackbar para mostrar errores de Firebase al usuario
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Cuando los datos del alumno llegan de Firebase, actualizamos el borrador
    LaunchedEffect(alumno) {
        if (alumno != null) {
            cinturon = alumno.cinturon
            grados = alumno.grados
            notas = alumno.notas
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
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull {
                    it.toObject(Asistencia::class.java)
                } ?: emptyList()

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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ===== SECCIÓN 1: Información del alumno =====

                item {
                    Column {
                        Text(
                            text = alumno.nombre.uppercase(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = alumno.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                // ===== SECCIÓN 2: Barra visual del cinturón =====

                item {
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
                }

                // ===== SECCIÓN 3: Selector de cinturones =====

                item {
                    Column {
                        Text(
                            text = "CAMBIAR CINTURÓN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                    }
                }

                // ===== SECCIÓN 4: Selector de stripes =====

                item {
                    Column {
                        Text(
                            text = "STRIPES (GRADOS): $grados",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            (0..4).forEach { num ->
                                FilterChip(
                                    selected = grados == num,
                                    onClick = { grados = num },
                                    label = { Text("$num") }
                                )
                            }
                        }
                    }
                }

                // ===== SECCIÓN 5: Asistencias =====

                item {
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
                            Text("CLASES REGISTRADAS")

                            Text(
                                text = "${alumno.clasesAsistidas}",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = {
                                    viewModel.registrarAsistencia(
                                        alumno.id,
                                        {},
                                        {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(it)
                                            }
                                        }
                                    )
                                }
                            ) {
                                Text("AÑADIR ASISTENCIA +1")
                            }
                        }
                    }
                }

                // ===== HISTORIAL =====

                item {
                    Column {
                        Text(
                            text = "HISTORIAL",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (asistencias.isEmpty()) {
                            Text(
                                text = "Aún no hay asistencias registradas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                asistencias.forEach { asistencia ->
                                    val ts = asistencia.timestamp ?: return@forEach
                                    Text("• ${formatTimestamp(ts)}")
                                }
                            }
                        }
                    }
                }

                // ===== SECCIÓN 6: NOTAS =====

                item {
                    Column {
                        Text(
                            text = "NOTAS DEL ALUMNO",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            placeholder = {
                                Text("Objetivos, actitud en clase, puntos a mejorar, lesiones…")
                            }
                        )
                    }
                }

                // ===== GUARDAR =====

                item {
                    Button(
                        onClick = {
                            val alumnoEditado = alumno.copy(
                                cinturon = cinturon,
                                grados = grados,
                                notas = notas.trim()
                            )

                            viewModel.actualizarAlumno(
                                alumnoEditado,
                                { onBack() },
                                {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(it)
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("GUARDAR CAMBIOS")
                    }
                }
            }

        } else {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}