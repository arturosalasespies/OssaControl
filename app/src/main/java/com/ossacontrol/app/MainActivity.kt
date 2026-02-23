package com.ossacontrol.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ossacontrol.app.ui.AppRoot // Volvemos a importar AppRoot, que ahora tiene todo lo nuevo
import com.ossacontrol.app.ui.theme.OSSAControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent es el punto de entrada para nuestra interfaz en Jetpack Compose
        setContent {
            // Aplicamos el tema visual de la aplicación
            OSSAControlTheme {
                // Llamamos a AppRoot(), que ahora es el único encargado de gestionar
                // la navegación por roles (Admin/Alumno) y todas las pantallas.
                AppRoot()
            }
        }
    }
}