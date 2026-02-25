package com.ossacontrol.app.viewmodel

/**
 * ============================================
 * Archivo: AdminViewModel.kt
 * ViewModel con toda la lógica de gestión del admin/profesor.
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial (CRUD alumnos, asistencia)
 *   - Arturo  (25/02): Añadida lógica de candidatos a graduación
 *     basada en reglas IBJJF simplificadas (requisito del profesor).
 *     Añadida actualización de fechaInicioCinturon al cambiar cinturón.
 *     Corregido bug de IDs: ahora usa doc.id real de Firebase
 *     en vez de email para actualizar y registrar asistencia.
 *   - Arturo (con Claude Code) (25/02): Añadida lógica de alumnos inactivos
 *     (no han asistido en los últimos 30 días). registrarAsistencia()
 *     ahora también actualiza el campo ultimaAsistencia en Firestore.
 *   - Alejandra (25/02): añadida función para registrar asistencia
 * ============================================
 */

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.ossacontrol.app.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class AdminViewModel : ViewModel() {
    // Instancia de la base de datos de Firestore
    private val db = FirebaseFirestore.getInstance()

    // --- Lista observable de todos los alumnos ---
    private val _usuarios = mutableStateOf<List<User>>(emptyList())
    val usuarios: State<List<User>> = _usuarios

    // --- Lista observable de candidatos a graduación ---
    private val _candidatos = mutableStateOf<List<CandidatoInfo>>(emptyList())
    val candidatos: State<List<CandidatoInfo>> = _candidatos

    // --- Lista observable de alumnos inactivos (sin asistir en los últimos DIAS_INACTIVIDAD) ---
    private val _inactivos = mutableStateOf<List<InactivoInfo>>(emptyList())
    val inactivos: State<List<InactivoInfo>> = _inactivos

    // ============================================
    // REGLAS DE GRADUACIÓN (basadas en IBJJF)
    // ============================================
    companion object {
        // Días sin asistir para considerar a un alumno inactivo (configurable)
        const val DIAS_INACTIVIDAD = 30

        val REGLAS_GRADUACION = mapOf(
            "Blanco" to RequisitosGraduacion(
                mesesMinimos = 6,
                clasesMinimas = 80,
                siguienteCinturon = "Azul"
            ),
            "Azul" to RequisitosGraduacion(
                mesesMinimos = 24,      // 2 años mínimo IBJJF
                clasesMinimas = 200,
                siguienteCinturon = "Morado"
            ),
            "Morado" to RequisitosGraduacion(
                mesesMinimos = 18,      // 1.5 años mínimo IBJJF
                clasesMinimas = 180,
                siguienteCinturon = "Marrón"
            ),
            "Marrón" to RequisitosGraduacion(
                mesesMinimos = 12,      // 1 año mínimo IBJJF
                clasesMinimas = 150,
                siguienteCinturon = "Negro"
            )
        )
    }

    // ============================================
    // FUNCIONES DE GESTIÓN DE ALUMNOS
    // ============================================

    /**
     * obtenerAlumnos() — Trae a todos los alumnos y escucha cambios en tiempo real.
     * CORREGIDO: Captura el ID real del documento de Firebase (doc.id)
     * para que actualizar y registrar asistencia funcionen siempre,
     * sin importar si el documento se creó con UID o con email.
     */
    fun obtenerAlumnos() {
        db.collection("users")
            .whereEqualTo("rol", "alumno")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AdminViewModel", "Error al escuchar alumnos: ${error.message}")
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    // copy(id = doc.id) sobreescribe el campo "id" con el ID real del documento.
                    // Esto es clave: funciona sin importar si el doc se creó con UID o con email.
                    doc.toObject(User::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                _usuarios.value = lista

                // Cada vez que llegan datos nuevos, recalculamos candidatos e inactivos
                calcularCandidatos(lista)
                calcularInactivos(lista)
            }
    }

    /**
     * registrarAlumno() — Crea un nuevo alumno en Firestore.
     * Incluye fechaInicioCinturon con la fecha actual.
     */
    fun registrarAlumno(nombre: String, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val nuevoAlumno = User(
            id = email,
            nombre = nombre,
            email = email,
            rol = "alumno",
            cinturon = "Blanco",
            grados = 0,
            clasesAsistidas = 0,
            fechaInicioCinturon = System.currentTimeMillis(),
            ultimaAsistencia = 0L   // Sin asistencias aún
        )

        db.collection("users")
            .document(email)
            .set(nuevoAlumno)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onError("Error al registrar: ${exception.localizedMessage}")
            }
    }

    /**
     * actualizarAlumno() — Actualiza los datos de un alumno en Firestore.
     * Si cambió de cinturón, reinicia la fecha y stripes.
     * CORREGIDO: Usa alumnoFinal.id (ID real del documento) en vez de email.
     */
    fun actualizarAlumno(user: User, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val alumnoActual = _usuarios.value.find { it.email == user.email }

        // Si el cinturón cambió, reiniciamos la fecha de inicio y los stripes a 0.
        // Igual que en la vida real: cinturón nuevo = contador a cero.
        val alumnoFinal = if (alumnoActual != null && alumnoActual.cinturon != user.cinturon) {
            user.copy(
                fechaInicioCinturon = System.currentTimeMillis(),
                grados = 0
            )
        } else {
            user
        }

        Log.d("AdminViewModel", "actualizarAlumno → doc ID: '${alumnoFinal.id}', cinturon: ${alumnoFinal.cinturon}")
        db.collection("users")
            .document(alumnoFinal.id)    // CORREGIDO: usa .id en vez de .email
            .set(alumnoFinal)
            .addOnSuccessListener {
                Log.d("AdminViewModel", "Alumno actualizado OK: ${alumnoFinal.id}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("AdminViewModel", "Error actualizando '${alumnoFinal.id}': ${exception.message}")
                onError("Error al actualizar: ${exception.localizedMessage}")
            }
    }

    /**
     * registrarAsistencia() — Incrementa el contador de clases en +1
     * y actualiza ultimaAsistencia con la fecha y hora actuales.
     *
     * CORREGIDO: Recibe el ID real del documento de Firebase, no el email.
     * ACTUALIZADO: Ahora actualiza dos campos a la vez con un Map, lo que
     * es más eficiente (una sola escritura en Firestore en vez de dos).
     */
    fun registrarAsistencia(alumnoId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val adminUid = FirebaseAuth.getInstance().currentUser?.uid

        // Documento de la asistencia (auto-id)
        val asistencia = hashMapOf(
            "timestamp" to FieldValue.serverTimestamp(), // Hora del servidor
            "createdBy" to adminUid
        )

        // 1) Guardar evento en subcolección
        db.collection("users")
            .document(alumnoId)
            .collection("asistencias")
            .add(asistencia)
            .addOnSuccessListener {
                // 2) Mantener contador + ultimaAsistencia como resumen
                db.collection("users")
                    .document(alumnoId)
                    .update(
                        mapOf(
                            "clasesAsistidas" to FieldValue.increment(1),
                            "ultimaAsistencia" to System.currentTimeMillis()
                        )
                    )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError("Asistencia guardada, pero no se pudo actualizar el contador: ${e.localizedMessage}")
                    }
            }
            .addOnFailureListener { e ->
                onError("Error al guardar asistencia: ${e.localizedMessage}")
            }
    }

    // ============================================
    // LÓGICA DE CANDIDATOS A GRADUACIÓN
    // ============================================

    /**
     * calcularCandidatos() — Recorre todos los alumnos y comprueba si cumplen
     * los requisitos de tiempo y clases para subir de cinturón (reglas IBJJF).
     */
    private fun calcularCandidatos(alumnos: List<User>) {
        val ahora = System.currentTimeMillis()
        val listaCandidatos = mutableListOf<CandidatoInfo>()

        for (alumno in alumnos) {
            // Si el cinturón es Negro, no hay siguiente cinturón
            val reglas = REGLAS_GRADUACION[alumno.cinturon] ?: continue

            // Calculamos los meses que lleva con este cinturón
            val mesesConCinturon = if (alumno.fechaInicioCinturon > 0) {
                val diferencia = ahora - alumno.fechaInicioCinturon
                (diferencia / (1000L * 60 * 60 * 24 * 30)).toInt()
            } else {
                0   // Sin fecha registrada = 0 meses (alumnos antiguos)
            }

            val cumpleTiempo = mesesConCinturon >= reglas.mesesMinimos
            val cumpleClases = alumno.clasesAsistidas >= reglas.clasesMinimas

            if (cumpleTiempo && cumpleClases) {
                listaCandidatos.add(
                    CandidatoInfo(
                        alumno = alumno,
                        siguienteCinturon = reglas.siguienteCinturon,
                        mesesConCinturon = mesesConCinturon,
                        clasesRequeridas = reglas.clasesMinimas,
                        mesesRequeridos = reglas.mesesMinimos
                    )
                )
            }
        }

        _candidatos.value = listaCandidatos
    }

    // ============================================
    // LÓGICA DE ALUMNOS INACTIVOS
    // ============================================

    /**
     * calcularInactivos() — Detecta qué alumnos no han asistido en los últimos
     * DIAS_INACTIVIDAD días (por defecto 30).
     *
     * Casos posibles:
     *  - ultimaAsistencia == 0L → nunca ha asistido → diasSinAsistir = -1 (valor especial)
     *  - ultimaAsistencia > 0L → calculamos los días reales
     *
     * Se llama automáticamente desde obtenerAlumnos() cada vez que Firestore
     * actualiza la lista.
     */
    private fun calcularInactivos(alumnos: List<User>) {
        val ahora = System.currentTimeMillis()
        // Convertimos DIAS_INACTIVIDAD de días a milisegundos para comparar
        val umbralMs = DIAS_INACTIVIDAD * 24L * 60 * 60 * 1000

        val listaInactivos = mutableListOf<InactivoInfo>()

        for (alumno in alumnos) {
            if (alumno.ultimaAsistencia == 0L) {
                // Nunca ha asistido desde que se registró (o campo no existe aún)
                listaInactivos.add(
                    InactivoInfo(alumno = alumno, diasSinAsistir = -1)
                )
            } else {
                val tiempoSinAsistir = ahora - alumno.ultimaAsistencia
                val dias = (tiempoSinAsistir / (1000L * 60 * 60 * 24)).toInt()
                if (tiempoSinAsistir >= umbralMs) {
                    listaInactivos.add(
                        InactivoInfo(alumno = alumno, diasSinAsistir = dias)
                    )
                }
            }
        }

        _inactivos.value = listaInactivos
    }
}

// ============================================
// CLASES DE DATOS AUXILIARES
// ============================================

/** Requisitos mínimos IBJJF para pasar al siguiente cinturón */
data class RequisitosGraduacion(
    val mesesMinimos: Int,
    val clasesMinimas: Int,
    val siguienteCinturon: String
)

/** Alumno que cumple los requisitos para graduarse */
data class CandidatoInfo(
    val alumno: User,
    val siguienteCinturon: String,
    val mesesConCinturon: Int,
    val clasesRequeridas: Int,
    val mesesRequeridos: Int
)

/**
 * Alumno inactivo (sin asistir en los últimos DIAS_INACTIVIDAD días).
 * diasSinAsistir = -1 significa que nunca ha asistido.
 */
data class InactivoInfo(
    val alumno: User,
    val diasSinAsistir: Int    // -1 = nunca ha asistido
)
