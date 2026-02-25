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

// --- IMPORTS DEL PROYECTO ---
import com.ossacontrol.app.viewmodel.AdminViewModel

// ============================================
// FUNCIONES AUXILIARES
// ============================================

/**
 * colorDelCinturon() — Devuelve el color visual real de cada cinturón BJJ.
 * Los colores están basados en los cinturones oficiales IBJJF.
 * Se usa para los chips del selector y la barra visual.
 */
fun colorDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color(0xFFF5F5F5)  // Blanco hueso
        "Azul"   -> Color(0xFF1565C0)  // Azul oscuro
        "Morado" -> Color(0xFF6A1B9A)  // Púrpura
        "Marrón" -> Color(0xFF5D4037)  // Marrón
        "Negro"  -> Color(0xFF212121)  // Negro
        else     -> Color.Gray
    }
}

/**
 * colorTextoDelCinturon() — Devuelve el color del texto para que sea legible
 * sobre el fondo del cinturón. Texto oscuro sobre fondo claro y viceversa.
 */
fun colorTextoDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color.Black
        else     -> Color.White
    }
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
    // Sin esto, la lista estaría vacía porque cada pantalla tiene su propia
    // copia del ViewModel (como pizarras independientes por sala).
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Buscamos al alumno por email dentro de la lista cargada
    val alumnosData by viewModel.usuarios
    val alumno = alumnosData.find { it.email == studentEmail }

    // --- Estados locales (borrador del admin) ---
    // Estos valores se editan en pantalla pero NO se guardan en Firebase
    // hasta que el admin pulse "GUARDAR CAMBIOS".
    var cinturon by remember { mutableStateOf(alumno?.cinturon ?: "Blanco") }
    var grados by remember { mutableStateOf(alumno?.grados ?: 0) }

    // Cuando los datos del alumno llegan de Firebase, actualizamos el borrador
    LaunchedEffect(alumno) {
        if (alumno != null) {
            cinturon = alumno.cinturon
            grados = alumno.grados
        }
    }

    // Lista oficial de los 5 cinturones de adulto en BJJ (orden IBJJF)
    val cinturones = listOf("Blanco", "Azul", "Morado", "Marrón", "Negro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DETALLE ALUMNO", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
                // QR eliminado por indicación del profesor José Manuel (futuro)
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
                // Muestra una barra con el color real del cinturón seleccionado,
                // como ver el cinturón del alumno colgado en la pared de la academia.
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
                // 5 chips con los colores reales de cada cinturón (IBJJF)
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
                                // Al cambiar de cinturón, reseteamos los grados a 0
                                // Igual que en la vida real: cinturón nuevo, stripes a cero
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
                // En BJJ cada cinturón puede tener de 0 a 4 stripes.
                // Las stripes son marcas en la punta del cinturón que indican progreso.
                // 4 stripes = candidato potencial a subir de cinturón.
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

                // ===== SECCIÓN 5: Tarjeta de asistencia =====
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

                        // Botón para simular registro de asistencia (+1 clase)
                        Button(
                            onClick = {
                                viewModel.registrarAsistencia(alumno.id, {}, {})
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("AÑADIR ASISTENCIA +1")
                        }
                    }
                }

                // Empuja el botón de guardar al fondo de la pantalla
                Spacer(modifier = Modifier.weight(1f))

                // ===== SECCIÓN 6: Botón guardar cambios =====
                // Crea una copia del alumno con cinturón y grados nuevos
                // y lo envía a Firebase a través del ViewModel
                Button(
                    onClick = {
                        val alumnoEditado = alumno.copy(
                            cinturon = cinturon,
                            grados = grados
                        )
                        viewModel.actualizarAlumno(alumnoEditado, { onBack() }, {})
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
            // Mientras carga, mostramos indicador de progreso
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