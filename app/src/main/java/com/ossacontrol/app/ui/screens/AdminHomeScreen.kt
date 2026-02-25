package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: AdminHomeScreen.kt
 * Pantalla principal del admin/profesor con lista de alumnos.
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial con listado y FAB
 *   - Alberto (25/02): Refactor visual (uppercase, iconos, diseño)
 *   - Arturo  (25/02): Añadido botón de Candidatos a Graduación
 *   - Arturo (con Claude Code) (25/02):
 *     · Añadido buscador de alumnos en tiempo real (filtra por nombre)
 *     · Añadida tarjeta de acceso a Inactivos
 *     · Añadida tarjeta de acceso a Estadísticas
 * ============================================
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.model.User
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit,
    onNavigateToAddStudent: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCandidatos: () -> Unit,
    onNavigateToInactivos: () -> Unit,       // AÑADIDO: acceso a la pantalla de inactivos
    onNavigateToEstadisticas: () -> Unit     // AÑADIDO: acceso a la pantalla de estadísticas
) {
    val viewModel: AdminViewModel = viewModel()
    val alumnos = viewModel.usuarios.value

    // Escuchamos candidatos e inactivos para mostrar sus contadores en las tarjetas
    val candidatos by viewModel.candidatos
    val inactivos by viewModel.inactivos

    // --- Estado del buscador ---
    // busqueda guarda el texto que el admin está escribiendo en el campo de búsqueda
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Filtramos la lista de alumnos según el texto del buscador.
    // Si busqueda está vacío, mostramos todos. Si no, filtramos por nombre.
    // ignoreCase = true hace que "juan" encuentre "JUAN" o "Juan"
    val alumnosFiltrados = if (busqueda.isBlank()) {
        alumnos
    } else {
        alumnos.filter { it.nombre.contains(busqueda, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GESTIÓN ACADEMIA", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddStudent,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir alumno")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // ===== CABECERA: Total de alumnos =====
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "TOTAL ALUMNOS: ${alumnos.size}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // ===== TARJETA: Candidatos a Graduación =====
            TarjetaAccesoRapido(
                icono = { Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                titulo = "CANDIDATOS A GRADUACIÓN",
                descripcion = "Alumnos que cumplen requisitos IBJJF",
                contador = candidatos.size,
                onClick = onNavigateToCandidatos
            )

            // ===== TARJETA: Alumnos Inactivos =====
            // El color del contador cambia a rojo si hay inactivos
            TarjetaAccesoRapido(
                icono = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                titulo = "ALUMNOS INACTIVOS",
                descripcion = "Sin asistir en los últimos ${AdminViewModel.DIAS_INACTIVIDAD} días",
                contador = inactivos.size,
                colorContador = if (inactivos.isNotEmpty()) MaterialTheme.colorScheme.error else null,
                onClick = onNavigateToInactivos
            )

            // ===== TARJETA: Estadísticas =====
            TarjetaAccesoRapido(
                icono = { Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                titulo = "ESTADÍSTICAS",
                descripcion = "Distribución, asistencias y top alumnos",
                contador = null,   // Las estadísticas no tienen un contador específico
                onClick = onNavigateToEstadisticas
            )

            // ===== BUSCADOR DE ALUMNOS =====
            // Campo de texto para filtrar la lista en tiempo real
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text("Buscar alumno por nombre") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Si el buscador está activo, mostramos cuántos resultados hay
            if (busqueda.isNotBlank()) {
                Text(
                    text = "${alumnosFiltrados.size} resultado(s) para \"$busqueda\"",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // ===== LISTA DE ALUMNOS =====
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mostramos la lista filtrada (todos o solo los que coinciden con la búsqueda)
                items(alumnosFiltrados) { alumno ->
                    CardAlumno(
                        alumno = alumno,
                        onClick = { onNavigateToDetail(alumno.email) }
                    )
                }

                // Si la búsqueda no encuentra resultados, mostramos un mensaje
                if (alumnosFiltrados.isEmpty() && busqueda.isNotBlank()) {
                    item {
                        Text(
                            text = "No hay alumnos con ese nombre",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * TarjetaAccesoRapido — Componente reutilizable para los accesos rápidos del admin.
 * Muestra un icono, título, descripción y un badge con el contador.
 * Si contador = null, no muestra el badge.
 */
@Composable
private fun TarjetaAccesoRapido(
    icono: @Composable () -> Unit,
    titulo: String,
    descripcion: String,
    contador: Int?,
    colorContador: androidx.compose.ui.graphics.Color? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la sección
            icono()
            Spacer(modifier = Modifier.width(12.dp))
            // Título y descripción
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            // Badge con el contador (si aplica)
            if (contador != null) {
                Badge(
                    containerColor = colorContador ?: MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = "$contador",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * CardAlumno — Tarjeta individual en la lista de alumnos.
 */
@Composable
fun CardAlumno(alumno: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alumno.nombre.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Cinturón: ${alumno.cinturon}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Clases: ${alumno.clasesAsistidas}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Text("Ver >", style = MaterialTheme.typography.labelSmall)
        }
    }
}
