package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: EstadisticasScreen.kt
 * Pantalla de estadísticas generales de la academia.
 *
 * Creado por: Arturo (con Claude Code) - Fecha: 25/02/2026
 * Motivo: Requisito del profesor José Manuel.
 *   "1 pantalla de estadísticas básicas: total activos, asistencias semana,
 *    top asistencia"
 *
 * SECCIONES:
 *   1. Resumen: total de alumnos, activos vs inactivos con porcentaje
 *   2. Distribución por cinturón (cuántos hay de cada color)
 *   3. Top 5 alumnos con más clases asistidas
 *
 * HISTORIAL DE CAMBIOS:
 *   - Arturo (con Claude Code) (25/02): Creación inicial
 * ============================================
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(onBack: () -> Unit) {

    // ViewModel que nos proporciona los datos de los alumnos e inactivos
    val viewModel: AdminViewModel = viewModel()

    // Cargamos los alumnos al entrar (calcularInactivos() se llama automáticamente)
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    // Obtenemos los datos que necesitamos
    val alumnos by viewModel.usuarios
    val inactivos by viewModel.inactivos

    // ===== CÁLCULOS DE ESTADÍSTICAS =====
    // Todos los cálculos se hacen aquí para mantener la UI limpia

    val totalAlumnos = alumnos.size
    val totalInactivos = inactivos.size
    val totalActivos = totalAlumnos - totalInactivos

    // Porcentaje de activos (evitamos división por cero con max(1, ...))
    val porcentajeActivos = if (totalAlumnos > 0) (totalActivos * 100 / totalAlumnos) else 0

    // Agrupamos alumnos por cinturón y contamos cuántos hay de cada uno
    // El orden es el orden oficial de los cinturones BJJ de adulto
    val cinturones = listOf("Blanco", "Azul", "Morado", "Marrón", "Negro")
    val distribucionCinturones = cinturones.map { cinturon ->
        Pair(cinturon, alumnos.count { it.cinturon == cinturon })
    }

    // Top 5 alumnos con más clases, ordenados de mayor a menor
    val top5 = alumnos.sortedByDescending { it.clasesAsistidas }.take(5)

    // scrollState para que la pantalla sea desplazable si hay mucho contenido
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ESTADÍSTICAS", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),  // Permite hacer scroll si el contenido es largo
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ===== SECCIÓN 1: Resumen general =====
            Text(
                text = "RESUMEN GENERAL",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )

            // Tarjeta con total de alumnos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total de alumnos
                    EstadisticaItem(
                        valor = "$totalAlumnos",
                        etiqueta = "TOTAL"
                    )
                    // Separador vertical (Box de 1dp de ancho)
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    // Activos
                    EstadisticaItem(
                        valor = "$totalActivos",
                        etiqueta = "ACTIVOS",
                        subtexto = "$porcentajeActivos%"
                    )
                    // Separador vertical
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    )
                    // Inactivos
                    EstadisticaItem(
                        valor = "$totalInactivos",
                        etiqueta = "INACTIVOS",
                        subtexto = "${100 - porcentajeActivos}%",
                        colorValor = if (totalInactivos > 0) MaterialTheme.colorScheme.error else null
                    )
                }
            }

            // ===== SECCIÓN 2: Distribución por cinturón =====
            Text(
                text = "POR CINTURÓN",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    distribucionCinturones.forEach { (cinturon, cantidad) ->
                        FilaCinturon(
                            cinturon = cinturon,
                            cantidad = cantidad,
                            total = totalAlumnos
                        )
                    }
                }
            }

            // ===== SECCIÓN 3: Top 5 asistencias =====
            Text(
                text = "TOP 5 ASISTENCIAS",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )

            if (top5.isEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No hay datos de asistencia aún",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        top5.forEachIndexed { indice, alumno ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Posición (1º, 2º, etc.)
                                Text(
                                    text = "${indice + 1}º",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.width(36.dp),
                                    // El primero va en color primario, el resto en secundario
                                    color = if (indice == 0) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.secondary
                                )
                                // Nombre del alumno
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = alumno.nombre.uppercase(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = alumno.cinturon,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                // Número de clases
                                Text(
                                    text = "${alumno.clasesAsistidas}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "clases",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            // Línea divisora entre alumnos (excepto después del último)
                            if (indice < top5.lastIndex) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * EstadisticaItem — Componente pequeño para mostrar un número grande con etiqueta.
 * Se usa en la tarjeta de resumen general.
 */
@Composable
private fun EstadisticaItem(
    valor: String,
    etiqueta: String,
    subtexto: String? = null,
    colorValor: Color? = null
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold,
            color = colorValor ?: MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        if (subtexto != null) {
            Text(
                text = subtexto,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * FilaCinturon — Fila para la distribución de cinturones.
 * Muestra una barra de progreso visual proporcional al total de alumnos.
 */
@Composable
private fun FilaCinturon(cinturon: String, cantidad: Int, total: Int) {
    // Color del cinturón usando la función compartida del paquete
    val colorCinturon = colorDelCinturon(cinturon)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nombre del cinturón
        Text(
            text = cinturon.uppercase(),
            modifier = Modifier.width(64.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        // Barra visual proporcional
        val fraccion = if (total > 0) cantidad.toFloat() / total.toFloat() else 0f
        Box(
            modifier = Modifier
                .weight(1f)
                .height(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
        ) {
            // La barra interior tiene el color real del cinturón
            if (fraccion > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraccion)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorCinturon)
                        .border(
                            width = if (cinturon == "Blanco") 1.dp else 0.dp,
                            color = if (cinturon == "Blanco") MaterialTheme.colorScheme.outline.copy(alpha = 0.3f) else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }

        // Número de alumnos
        Text(
            text = "$cantidad",
            modifier = Modifier.width(28.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End
        )
    }
}
