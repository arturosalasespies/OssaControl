package com.ossacontrol.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ossacontrol.app.ui.AppRoot
import com.ossacontrol.app.ui.theme.OSSAControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent inicia Compose
        setContent {

            // Aplicamos el tema
            OSSAControlTheme {

                // En AppRoot se produce la navegación entre las distintas pantallas
                AppRoot()
            }
        }
    }
}