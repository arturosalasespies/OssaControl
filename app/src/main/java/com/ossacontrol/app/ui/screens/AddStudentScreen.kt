package com.ossacontrol.app.ui.screens

/**
 * ============================================
 * Archivo: AddStudentScreen.kt
 * Pantalla para dar de alta a un nuevo alumno (solo admin).
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial básica (campos nombre y email, sin validación)
 *   - Arturo (con Claude Code) (25/02): Limpieza - Arturo 25/02/2026
 *     · Añadidas validaciones: formato email (@ y .), nombre mínimo 2 caracteres
 *     · Validación de email duplicado en Firestore (consulta por campo email)
 *     · Estado de carga mientras se procesa (botón deshabilitado)
 *     · Mensajes de error por campo (debajo de cada OutlinedTextField)
 *     · Error general para fallos de conexión
 *     · Estilo consistente: TopAppBar bold, botón con shape, texto uppercase
 *     · Pantalla gestiona su propio ViewModel (patrón del resto de pantallas)
 * ============================================
 */

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(onBack: () -> Unit) {

    // ViewModel para registrar al alumno en Firestore
    val viewModel: AdminViewModel = viewModel()

    // --- Estados de los campos ---
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // Estado de procesamiento (mientras consultamos Firestore o guardamos)
    var loading by remember { mutableStateOf(false) }

    // Errores individuales por campo y un error general de red
    var errorNombre by remember { mutableStateOf<String?>(null) }
    var errorEmail by remember { mutableStateOf<String?>(null) }
    var errorGeneral by remember { mutableStateOf<String?>(null) }

    // --- Validación local del nombre ---
    // Regla: no vacío y al menos 2 caracteres
    fun validarNombre(): Boolean {
        return if (nombre.trim().length < 2) {
            errorNombre = "El nombre debe tener al menos 2 caracteres"
            false
        } else {
            errorNombre = null
            true
        }
    }

    // --- Validación local del email ---
    // Regla básica: debe contener @ y un punto después del @
    fun validarEmail(): Boolean {
        val emailTrimmed = email.trim()
        return if (!emailTrimmed.contains("@") || !emailTrimmed.substringAfter("@").contains(".")) {
            errorEmail = "Introduce un email válido (debe contener @ y .)"
            false
        } else {
            errorEmail = null
            true
        }
    }

    // --- Proceso completo de alta ---
    // 1) Validación local → 2) Comprobación duplicado en Firestore → 3) Registro
    fun darDeAlta() {
        val nombreOk = validarNombre()
        val emailOk = validarEmail()
        if (!nombreOk || !emailOk) return

        loading = true
        errorGeneral = null

        // Comprobamos si ya existe algún documento con ese email (campo "email"),
        // sin importar si fue creado con UID o con email como ID de documento.
        FirebaseFirestore.getInstance()
            .collection("users")
            .whereEqualTo("email", email.trim())
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Email ya registrado → mostramos error debajo del campo
                    errorEmail = "Ya existe un alumno con este email"
                    loading = false
                } else {
                    // Email libre → registramos el alumno vía ViewModel
                    viewModel.registrarAlumno(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        onSuccess = { onBack() },
                        onError = { mensaje ->
                            Log.e("AddStudentScreen", "Error al registrar alumno: $mensaje")
                            errorGeneral = mensaje
                            loading = false
                        }
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AddStudentScreen", "Error al verificar email duplicado: ${exception.message}")
                errorGeneral = "Error de conexión. Inténtalo de nuevo."
                loading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DAR DE ALTA ALUMNO", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, enabled = !loading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {

            // --- Campo: Nombre completo ---
            // Limpia el error del campo al empezar a escribir
            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    if (errorNombre != null) errorNombre = null
                },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorNombre != null,
                shape = MaterialTheme.shapes.medium,
                supportingText = {
                    if (errorNombre != null) {
                        Text(errorNombre!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- Campo: Email del alumno ---
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (errorEmail != null) errorEmail = null
                },
                label = { Text("Email del alumno") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = errorEmail != null,
                shape = MaterialTheme.shapes.medium,
                supportingText = {
                    if (errorEmail != null) {
                        Text(errorEmail!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // Error general (problema de conexión o error de Firebase)
            if (errorGeneral != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorGeneral!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón: Dar de alta ---
            // Deshabilitado si está procesando, o si los campos están vacíos
            Button(
                onClick = { darDeAlta() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !loading && nombre.isNotBlank() && email.isNotBlank(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = if (loading) "REGISTRANDO..." else "DAR DE ALTA",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading
            ) {
                Text("CANCELAR")
            }
        }
    }
}
