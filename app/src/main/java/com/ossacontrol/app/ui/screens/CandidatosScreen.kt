package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: CandidatosScreen.kt
 * Pantalla que muestra los alumnos candidatos a subir de cinturón.
 *
 * Creado por: Arturo - Fecha: 25/02/2026
 * Motivo: Requisito del profesor José Manuel para subir nota.
 *   "Candidatos a graduación" = listado automático de alumnos que
 *   cumplen los requisitos mínimos IBJJF (tiempo + clases).
 *
 * REGLAS IMPLEMENTADAS (IBJJF simplificadas):
 *   Blanco → Azul:    6 meses  + 80 clases
 *   Azul → Morado:   24 meses + 200 clases
 *   Morado → Marrón: 18 meses + 180 clases
 *   Marrón → Negro:  12 meses + 150 clases
 *
 * NOTA: El profesor siempre tiene la última palabra.
 *   Esto es solo una herramienta de apoyo, no una promoción automática.
 * ============================================
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.viewmodel.AdminViewModel
import com.ossacontrol.app.viewmodel.CandidatoInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatosScreen(onBack: () -> Unit) {

    // ViewModel compartido — mismo que usa AdminHomeScreen
    val viewModel: AdminViewModel = viewModel()

    // Cargamos los alumnos al entrar (esto también calcula los candidatos)
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Escuchamos la lista de candidatos calculada por el ViewModel
    val candidatos by viewModel.candidatos

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CANDIDATOS A GRADUACIÓN", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // ===== CABECERA: Total de candidatos =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ALUMNOS LISTOS PARA SUBIR",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${candidatos.size}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Basado en requisitos mínimos IBJJF",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // ===== LISTA DE CANDIDATOS =====
            if (candidatos.isEmpty()) {
                // Si no hay candidatos, mostramos un mensaje
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NINGÚN CANDIDATO POR AHORA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Los alumnos aparecerán aquí cuando cumplan los requisitos mínimos de tiempo y asistencia para su cinturón actual.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                // Lista eficiente de candidatos (LazyColumn = solo renderiza lo visible)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(candidatos) { candidato ->
                        TarjetaCandidato(candidato = candidato)
                    }
                }
            }
        }
    }
}

/**
 * TarjetaCandidato — Tarjeta visual para cada alumno candidato.
 * Muestra: nombre, cinturón actual → siguiente, meses, clases.
 * Incluye una barra visual con los colores de los cinturones.
 */
@Composable
fun TarjetaCandidato(candidato: CandidatoInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // --- Nombre del alumno ---
            Text(
                text = candidato.alumno.nombre.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Barra visual: cinturón actual → siguiente ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Cinturón actual
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                        .background(colorDelCinturon(candidato.alumno.cinturon))
                        .border(
                            1.dp,
                            Color.Gray,
                            RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = candidato.alumno.cinturon.uppercase(),
                        color = colorTextoDelCinturon(candidato.alumno.cinturon),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Flecha
                Text(
                    text = " → ",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                // Siguiente cinturón
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                        .background(colorDelCinturon(candidato.siguienteCinturon))
                        .border(
                            1.dp,
                            Color.Gray,
                            RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = candidato.siguienteCinturon.uppercase(),
                        color = colorTextoDelCinturon(candidato.siguienteCinturon),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Datos del candidato ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Columna: Tiempo
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${candidato.mesesConCinturon}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "meses",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "(mín: ${candidato.mesesRequeridos})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Columna: Clases
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${candidato.alumno.clasesAsistidas}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "clases",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "(mín: ${candidato.clasesRequeridas})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Columna: Stripes
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${candidato.alumno.grados}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "stripes",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- Aviso importante ---
            Text(
                text = "✓ Cumple requisitos mínimos IBJJF",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}