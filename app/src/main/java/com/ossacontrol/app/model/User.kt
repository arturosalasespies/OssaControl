package com.ossacontrol.app.model

//esto nos sirve para definir qué datos tiene un usuario en la base de datos.

data class User(
    val id: String = "",
    val email: String = "",
    val nombre: String = "",
    val rol: String = "alumno", // Importante: "admin" o "alumno"
    val cinturon: String = "Blanco",
    val grados: Int = 0,
    val clasesAsistidas: Int = 0
)