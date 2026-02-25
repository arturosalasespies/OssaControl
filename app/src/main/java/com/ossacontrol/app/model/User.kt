package com.ossacontrol.app.model

/**
 * ============================================
 * Archivo: User.kt
 * Modelo de datos del usuario en Firestore.
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alberto (11/02): Versión inicial (email, nombre, rol, cinturon, clasesAsistidas)
 *   - Arturo  (14/02): Añadido campo grados (stripes)
 *   - Arturo  (25/02): Añadido campo fechaInicioCinturon para calcular
 *     candidatos a graduación (requisito del profesor José Manuel)
 * ============================================
 *
 * Cada campo tiene un valor por defecto ("" o 0) para que Firebase
 * pueda crear objetos User aunque falte algún dato en la base de datos.
 * Sin estos valores por defecto, toObject(User::class.java) fallaría.
 */
data class User(
    val id: String = "",
    val email: String = "",
    val nombre: String = "",
    val rol: String = "alumno",            // "admin" o "alumno"
    val cinturon: String = "Blanco",       // Blanco, Azul, Morado, Marrón, Negro
    val grados: Int = 0,                   // Stripes: 0-4
    val clasesAsistidas: Int = 0,          // Contador total de clases
    val fechaInicioCinturon: Long = 0L     // Fecha en milisegundos de cuándo se puso este cinturón
    // Se usa para calcular si es candidato a graduación
    // 0L = no registrada (alumnos antiguos)
)