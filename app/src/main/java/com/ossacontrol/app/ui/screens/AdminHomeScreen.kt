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
 * ============================================
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
    onNavigateToCandidatos: () -> Unit          // AÑADIDO: Navegación a candidatos
) {
    val viewModel: AdminViewModel = viewModel()
    val alumnos = viewModel.usuarios.value

    // AÑADIDO: Escuchamos también los candidatos para mostrar el contador
    val candidatos by viewModel.candidatos

    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
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
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Cabecera con total de alumnos
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

            // ===== AÑADIDO: Botón de Candidatos a Graduación =====
            // Este botón lleva a la pantalla donde se ven los alumnos
            // que cumplen requisitos IBJJF para subir de cinturón.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onNavigateToCandidatos() },
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
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Candidatos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CANDIDATOS A GRADUACIÓN",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Alumnos que cumplen requisitos IBJJF",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    // Muestra el número de candidatos como badge
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Text(
                            text = "${candidatos.size}",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Lista de alumnos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alumnos) { alumno ->
                    CardAlumno(
                        alumno = alumno,
                        onClick = { onNavigateToDetail(alumno.email) }
                    )
                }
            }
        }
    }
}

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