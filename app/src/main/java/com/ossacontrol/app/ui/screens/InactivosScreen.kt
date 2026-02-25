package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: InactivosScreen.kt
 * Pantalla que muestra los alumnos que llevan más de 30 días sin asistir.
 *
 * Creado por: Arturo (con Claude Code) - Fecha: 25/02/2026
 * Motivo: Requisito del profesor José Manuel.
 *   "Definid inactivo como no ha asistido en 30 días (configurable).
 *    Pantalla/filtro rápido para el coach."
 *
 * LÓGICA:
 *   - Inactivo = ultimaAsistencia == 0L (nunca asistió) O más de 30 días sin venir
 *   - El umbral de 30 días está definido en AdminViewModel.DIAS_INACTIVIDAD
 *   - Se puede cambiar esa constante para ajustar el criterio
 *
 * HISTORIAL DE CAMBIOS:
 *   - Arturo (con Claude Code) (25/02): Creación inicial
 * ============================================
 */

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.viewmodel.AdminViewModel
import com.ossacontrol.app.viewmodel.InactivoInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InactivosScreen(onBack: () -> Unit) {

    // ViewModel que nos da la lista de inactivos calculada
    val viewModel: AdminViewModel = viewModel()

    // Cargamos los alumnos al entrar (esto también calcula los inactivos automáticamente)
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Escuchamos la lista de inactivos y el total de alumnos
    val inactivos by viewModel.inactivos
    val totalAlumnos = viewModel.usuarios.value.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ALUMNOS INACTIVOS", fontWeight = FontWeight.Bold) },
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

            // ===== CABECERA: Resumen de inactividad =====
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    // Usamos el color de error (rojo/naranja) para llamar la atención
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SIN ASISTIR MÁS DE ${AdminViewModel.DIAS_INACTIVIDAD} DÍAS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${inactivos.size}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Mostramos el porcentaje de alumnos inactivos
                    val porcentaje = if (totalAlumnos > 0)
                        (inactivos.size * 100 / totalAlumnos)
                    else 0
                    Text(
                        text = "de $totalAlumnos alumnos ($porcentaje%)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // ===== LISTA DE INACTIVOS =====
            if (inactivos.isEmpty()) {
                // Si no hay inactivos, mostramos un mensaje positivo
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "¡TODOS ACTIVOS!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Ningún alumno lleva más de ${AdminViewModel.DIAS_INACTIVIDAD} días sin asistir. ¡Excelente asistencia!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            } else {
                // Lista de alumnos inactivos ordenados: primero los que llevan más días sin venir
                val inactivosOrdenados = inactivos.sortedByDescending { it.diasSinAsistir }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(inactivosOrdenados) { inactivo ->
                        TarjetaInactivo(inactivo = inactivo)
                    }
                }
            }
        }
    }
}

/**
 * TarjetaInactivo — Tarjeta que muestra la información de un alumno inactivo.
 * Muestra: nombre, cinturón, días sin asistir (o "Nunca ha asistido").
 */
@Composable
fun TarjetaInactivo(inactivo: InactivoInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Columna izquierda: Nombre y cinturón ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = inactivo.alumno.nombre.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CINTURÓN ${inactivo.alumno.cinturon.uppercase()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // --- Columna derecha: Días sin asistir ---
            Column(horizontalAlignment = Alignment.End) {
                if (inactivo.diasSinAsistir == -1) {
                    // diasSinAsistir == -1 es el código especial para "nunca ha asistido"
                    Text(
                        text = "NUNCA",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "ha asistido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                } else {
                    Text(
                        text = "${inactivo.diasSinAsistir}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "días sin venir",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
