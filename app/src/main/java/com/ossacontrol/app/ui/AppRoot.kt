package com.ossacontrol.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ossacontrol.app.ui.screens.HomeScreen
import com.ossacontrol.app.ui.screens.LoginScreen
import com.ossacontrol.app.ui.screens.SignUpScreen

// Rutas de navegación
object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
}

@Composable
fun AppRoot() {

    // NavController controla la navegación
    val navController = rememberNavController()

    // Si el usuario ya tiene la sesión iniciada, entramos directamente a Home
    // Si no, empezamos en Login
    val start = if (FirebaseAuth.getInstance().currentUser != null) Routes.Home else Routes.Login

    // NavHost contiene las pantallas de la app y define las rutas
    NavHost(navController = navController, startDestination = start) {

        // Pantalla Login
        composable(Routes.Login) {
            LoginScreen(

                // Se ejecuta cuando el login es correcto
                onLoginSuccess = {

                    // Vamos a Home y eliminamos Login del historial
                    // para que al pulsar "atrás" no volvamos a Login
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },

                // Ir a la pantalla de registro
                onGoToSignUp = { navController.navigate(Routes.SignUp) }
            )
        }

        // Pantalla Registro
        composable(Routes.SignUp) {
            SignUpScreen(

                // Se ejecuta cuando se crea el usuario correctamente
                onSignUpSuccess = {

                    // Vamos a Home y limpiamos Login del historial
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },

                // Vuelve a Login
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Pantalla Home
        composable(Routes.Home) {
            HomeScreen(
                onLogout = {

                    // Cerramos sesión en Firebase
                    FirebaseAuth.getInstance().signOut()

                    // Volvemos a Login y eliminamos Home del historial
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                }
            )
        }
    }
}