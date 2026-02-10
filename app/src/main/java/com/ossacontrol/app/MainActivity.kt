package com.ossacontrol.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ossacontrol.app.ui.AppRoot2
import com.ossacontrol.app.ui.theme.OSSAControlTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OSSAControlTheme {
                // Llamamos a AppRoot2 que es la que tiene las pantallas nuevas
                AppRoot2()
            }
        }
    }
}