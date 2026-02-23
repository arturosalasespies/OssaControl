package com.ossacontrol.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Definimos la paleta de colores para el Modo Oscuro
private val DarkColorScheme = darkColorScheme(
    primary = White,            // El blanco resaltará sobre el fondo negro
    secondary = GrayLight,
    tertiary = AccentRed,
    background = Black,         // Fondo de la app negro
    surface = GrayDark,         // Fondo de las tarjetas gris oscuro
    onPrimary = Black,          // Texto sobre botones primarios en negro
    onBackground = White,       // Texto sobre el fondo en blanco
    onSurface = White           // Texto sobre las tarjetas en blanco
)

// Definimos la paleta de colores para el Modo Claro
private val LightColorScheme = lightColorScheme(
    primary = Black,            // El negro es el color principal (botones, barras)
    secondary = GrayDark,
    tertiary = AccentRed,
    background = White,         // Fondo de la app blanco
    surface = GrayLight,        // Fondo de las tarjetas gris clarito
    onPrimary = White,          // Texto sobre botones negros en blanco
    onBackground = Black,       // Texto sobre el fondo en negro
    onSurface = Black           // Texto sobre las tarjetas en negro
)

@Composable
fun OSSAControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Hemos desactivado el color dinámico (dynamicColor = false) para que la app
    // siempre use NUESTROS colores corporativos y no los del sistema Android.
    dynamicColor: Boolean = false, 
    content: @Composable () -> Unit
) {
    // Elegimos el esquema de colores dependiendo de si el móvil está en modo oscuro o claro
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Aplicamos el tema a toda la aplicación
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Usamos la tipografía por defecto (puedes cambiarla en Type.kt)
        content = content
    )
}