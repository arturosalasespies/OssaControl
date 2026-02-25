package com.ossacontrol.app.model

import com.google.firebase.Timestamp

/**
 * ============================================
 * Modelo: Asistencia
 *
 * Representa una asistencia registrada de un alumno.
 *
 * CAMPOS:
 *  - timestamp → Fecha y hora exacta en la que se registró la asistencia.
 *
 *  - createdBy → Email o ID del usuario que registró la asistencia.
 *                 Profesor/Admin que pulsó "+1".
 * ============================================
 */
data class Asistencia(
    val timestamp: Timestamp? = null,
    val createdBy: String? = null
)