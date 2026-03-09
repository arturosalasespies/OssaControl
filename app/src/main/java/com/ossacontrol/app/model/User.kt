package com.ossacontrol.app.model

/**
 * ============================================
 * Archivo: User.kt
 * Modelo de datos del usuario en Firestore.
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial (email, nombre, rol, cinturon, clasesAsistidas)
 *   - Arturo  (14/02): Añadido campo grados (stripes)
 *   - Arturo  (25/02): Añadido campo fechaInicioCinturon para candidatos a graduación
 *   - Arturo (con Claude Code) (25/02): Añadido campo ultimaAsistencia para
 *     detectar alumnos inactivos (requisito del profesor José Manuel)
 *   - Alejandra (09/03): Añadido campo de notas para introducir anotaciones sobre cada alumno
 * ============================================
 *
 * Cada campo tiene un valor por defecto ("" o 0) para que Firebase
 * pueda crear objetos User aunque falte algún dato en la base de datos.
 * Sin estos valores por defecto, toObject(User::class.java) fallaría.
 *
 * NOTA SOBRE ultimaAsistencia:
 *   Los alumnos existentes sin este campo recibirán 0L por defecto,
 *   lo que los marcará como "nunca ha asistido". Es el comportamiento
 *   correcto: sin datos, asumimos inactividad.
 *
 * NOTA SOBRE notas:
 *   Es un campo de texto libre para que el admin/profesor pueda guardar
 *   observaciones del alumno: objetivos, lesiones, actitud, detalles técnicos, etc.
 *   Los usuarios antiguos que no tengan este campo recibirán "" por defecto.
 *
 */
data class User(
    val id: String = "",
    val email: String = "",
    val nombre: String = "",
    val rol: String = "alumno",            // "admin" o "alumno"
    val cinturon: String = "Blanco",       // Blanco, Azul, Morado, Marrón, Negro
    val grados: Int = 0,                   // Stripes: 0-4
    val clasesAsistidas: Int = 0,          // Contador total de clases
    val fechaInicioCinturon: Long = 0L,    // Milisegundos de cuándo se puso este cinturón
                                           // 0L = no registrada (alumnos antiguos)
    val ultimaAsistencia: Long = 0L,        // Milisegundos de la última clase registrada
                                           // 0L = nunca ha asistido o dato no registrado
    val notas: String = ""                 // Notas internas del admin/profesor
)