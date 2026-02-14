/**
 * ============================================
 * Archivo: StudentDetailScreen.kt
 * Pantalla de detalle de un alumno (vista del ADMIN).
 * Modificado por: Arturo
 * Fecha: 14/02/2026
 * Cambios:
 *   - Sustituida librería QR rota (alexzh) por ZXing de Google
 *   - Añadidos los 5 cinturones de BJJ: Blanco, Azul, Morado, Marrón, Negro
 *   - Añadido selector de stripes/grados (0-4)
 *   - Cada cinturón tiene su color real en el selector
 *   - Función generarCodigoQR() crea el bitmap del QR con ZXing
 * ============================================
 */
package com.ossacontrol.app.ui.screens

// --- IMPORTS DE ANDROID Y COMPOSE ---
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect

// --- IMPORT DE ZXING (la librería QR de Google) ---
// BarcodeFormat define el tipo de código (QR, barras, etc.)
// MultiFormatWriter es el "dibujante" que genera la cuadrícula del código
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

// --- IMPORTS DEL PROYECTO ---
import com.ossacontrol.app.viewmodel.AdminViewModel

/**
 * generarCodigoQR() — Función que crea una imagen QR a partir de un texto.
 *
 * ¿Cómo funciona? Piensa en ello como un sello personalizado:
 * 1. Le damos un texto (por ejemplo "asistencia:juan@email.com")
 * 2. ZXing lo convierte en una cuadrícula de puntos (BitMatrix)
 * 3. Nosotros recorremos cada punto y lo pintamos de negro (dato) o blanco (vacío)
 * 4. El resultado es un Bitmap (imagen) que Compose puede mostrar
 *
 * @param texto  El contenido que se codifica dentro del QR
 * @param tamaño El ancho y alto en píxeles de la imagen QR generada
 * @return       Un Bitmap con el código QR listo para mostrar
 */
fun generarCodigoQR(texto: String, tamaño: Int = 512): Bitmap {
    // 1. Creamos la cuadrícula de bits usando ZXing
    //    MultiFormatWriter puede generar muchos tipos de códigos,
    //    nosotros le pedimos un QR_CODE específicamente
    val bitMatrix = MultiFormatWriter().encode(
        texto,              // El texto que se esconde dentro del QR
        BarcodeFormat.QR_CODE,  // Tipo: código QR (no barras ni otros)
        tamaño,             // Ancho en píxeles
        tamaño              // Alto en píxeles (cuadrado)
    )

    // 2. Creamos un Bitmap vacío donde vamos a "pintar" el QR
    val bitmap = Bitmap.createBitmap(tamaño, tamaño, Bitmap.Config.RGB_565)

    // 3. Recorremos cada píxel de la cuadrícula
    //    Si el punto está "encendido" (true) → lo pintamos de NEGRO
    //    Si está "apagado" (false) → lo pintamos de BLANCO
    for (x in 0 until tamaño) {
        for (y in 0 until tamaño) {
            bitmap.setPixel(
                x, y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK
                else android.graphics.Color.WHITE
            )
        }
    }

    return bitmap
}

/**
 * colorDelCinturon() — Devuelve el color visual para cada cinturón de BJJ.
 * Así los chips del selector muestran el color real del cinturón,
 * como verlos colgados en la pared de la academia.
 */
fun colorDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color.White
        "Azul"   -> Color(0xFF1565C0)   // Azul oscuro
        "Morado" -> Color(0xFF6A1B9A)   // Púrpura
        "Marrón" -> Color(0xFF5D4037)   // Marrón
        "Negro"  -> Color.Black
        else     -> Color.Gray
    }
}

/**
 * colorTextoDelCinturon() — Devuelve el color del texto según el cinturón.
 * Para cinturones oscuros (Azul, Morado, Marrón, Negro) usamos texto blanco.
 * Para Blanco usamos texto negro para que se lea bien.
 */
fun colorTextoDelCinturon(cinturon: String): Color {
    return when (cinturon) {
        "Blanco" -> Color.Black
        else     -> Color.White
    }
}

// ============================================
// PANTALLA PRINCIPAL: StudentDetailScreen
// ============================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(studentEmail: String, onBack: () -> Unit) {

    // ViewModel que nos conecta con Firebase para leer y escribir datos
    val viewModel: AdminViewModel = viewModel()
    // AÑADIDO: Cargamos los alumnos desde Firebase al entrar en esta pantalla
    // Sin esto, la lista estaría vacía porque cada pantalla tiene su propia
    // copia del ViewModel (como una pizarra independiente por cada sala)
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }
    // Buscamos al alumno por email dentro de la lista cargada
    val alumno = viewModel.usuarios.value.find { it.email == studentEmail }

    // --- Estados locales de la pantalla ---
    // Estos "remember" guardan valores temporales mientras el admin edita.
    // Es como un borrador: cambias cosas pero no se guardan hasta que pulsas "Guardar".
    var cinturon by remember { mutableStateOf(alumno?.cinturon ?: "Blanco") }
    var grados by remember { mutableStateOf(alumno?.grados ?: 0) }
    var mostrarDialogoQR by remember { mutableStateOf(false) }

    // Lista completa de los 5 cinturones de BJJ para adultos
    // Orden oficial: Blanco → Azul → Morado → Marrón → Negro
    val cinturones = listOf("Blanco", "Azul", "Morado", "Marrón", "Negro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Alumno") },
                // Flecha para volver atrás a la lista
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón de QR en la barra superior (el icono de cuadradito QR)
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
                // ===== SECCIÓN 1: Información básica del alumno =====
                Text(
                    text = alumno.nombre,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = alumno.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // ===== SECCIÓN 2: Selector de cinturón =====
                // Muestra 5 chips con los colores reales de cada cinturón
                Text(
                    text = "Cinturón: $cinturon",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Recorremos la lista de cinturones y creamos un chip por cada uno
                    cinturones.forEach { nombreCinturon ->
                        FilterChip(
                            selected = cinturon == nombreCinturon,
                            onClick = { cinturon = nombreCinturon },
                            label = { Text(nombreCinturon, color = colorTextoDelCinturon(nombreCinturon)) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = colorDelCinturon(nombreCinturon).copy(alpha = 0.3f),
                                selectedContainerColor = colorDelCinturon(nombreCinturon)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 3: Selector de stripes (grados) =====
                // En BJJ, cada cinturón puede tener de 0 a 4 stripes
                // Las stripes son marcas en el cinturón que indican progreso
                Text(
                    text = "Stripes (grados): $grados",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Creamos chips del 0 al 4
                    (0..4).forEach { numGrado ->
                        FilterChip(
                            selected = grados == numGrado,
                            onClick = { grados = numGrado },
                            label = { Text("$numGrado") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== SECCIÓN 4: Tarjeta de asistencia =====
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CLASES ASISTIDAS",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${alumno.clasesAsistidas}",
                            style = MaterialTheme.typography.displayMedium
                        )

                        // Botón para simular el escaneo de QR (+1 clase)
                        // En el futuro esto se hará escaneando el QR con la cámara
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

                // Empuja el botón de guardar al fondo de la pantalla
                Spacer(modifier = Modifier.weight(1f))

                // ===== SECCIÓN 5: Botón guardar cambios =====
                // Crea una copia del alumno con el cinturón y grados nuevos
                // y lo envía a Firebase a través del ViewModel
                Button(
                    onClick = {
                        val alumnoEditado = alumno.copy(
                            cinturon = cinturon,
                            grados = grados
                        )
                        viewModel.actualizarAlumno(alumnoEditado, { onBack() }, {})
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Guardar Cambios")
                }
            }

            // ===== DIÁLOGO QR (ventana emergente) =====
            // Se abre cuando el admin pulsa el icono QR en la barra superior
            if (mostrarDialogoQR) {
                // Generamos el QR con el email del alumno
                // "remember" evita que se regenere cada vez que Compose redibuja
                val qrBitmap = remember(alumno.email) {
                    generarCodigoQR("asistencia:${alumno.email}", 512)
                }

                AlertDialog(
                    onDismissRequest = { mostrarDialogoQR = false },
                    confirmButton = {
                        TextButton(onClick = { mostrarDialogoQR = false }) {
                            Text("Cerrar")
                        }
                    },
                    title = {
                        Text("QR de Asistencia", modifier = Modifier.fillMaxWidth())
                    },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Mostramos el QR generado por ZXing como imagen
                            // asImageBitmap() convierte el Bitmap de Android
                            // en algo que Compose puede pintar en pantalla
                            Image(
                                bitmap = qrBitmap.asImageBitmap(),
                                contentDescription = "Código QR de ${alumno.nombre}",
                                modifier = Modifier.size(200.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Pasa este código por el lector de la academia",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                )
            }
        }
    }
}