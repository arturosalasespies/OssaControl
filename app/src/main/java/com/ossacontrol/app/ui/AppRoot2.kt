package com.ossacontrol.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.ossacontrol.app.ui.screens.*
import com.ossacontrol.app.viewmodel.AuthViewModel
import com.ossacontrol.app.viewmodel.AdminViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppRoot2() {
    // El navController nos sirve para saber en qué pantalla estamos y a cuál ir.
    val navController = rememberNavController()

    // ViewModel para gestionar quién está logueado y qué permisos tiene.
    val authViewModel: AuthViewModel = viewModel()

    // Vigilamos el rol del usuario (admin o alumno) para saber a dónde mandarlo.
    val userRole by authViewModel.userRole

    // Si ya hay una sesión abierta en Firebase, vamos directo al selector, si no, al login.
    val start = if (FirebaseAuth.getInstance().currentUser != null) "home_selector" else "login"

    // El NavHost es el mapa de todas las pantallas de nuestra app.
    NavHost(navController = navController, startDestination = start) {

        // --- RUTA: LOGIN ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = { authViewModel.checkUserRole() }, // Si entra bien, miramos su rol.
                onGoToSignUp = { navController.navigate("signup") } // Ir al registro.
            )
            // Si el rol cambia a admin o alumno, saltamos a su pantalla correspondiente.
            LaunchedEffect(userRole) {
                if (userRole == "admin") {
                    navController.navigate("admin_home") { popUpTo("login") { inclusive = true } }
                } else if (userRole == "alumno") {
                    navController.navigate("student_home") { popUpTo("login") { inclusive = true } }
                }
            }
        }

        // --- RUTA: REGISTRO ---
        composable("signup") {
            SignUpScreen(
                onSignUpSuccess = { authViewModel.checkUserRole() }, // Tras registrarse, miramos rol.
                onBackToLogin = { navController.popBackStack() } // Volver atrás.
            )
        }

        // --- RUTA SELECTOR (Carga de rol) ---
        composable("home_selector") {
            LaunchedEffect(Unit) { authViewModel.checkUserRole() }
            LaunchedEffect(userRole) {
                if (userRole == "admin") {
                    navController.navigate("admin_home") { popUpTo("home_selector") { inclusive = true } }
                } else if (userRole == "alumno") {
                    navController.navigate("student_home") { popUpTo("home_selector") { inclusive = true } }
                }
            }
        }

        // --- RUTA: HOME PROFESOR ---
        composable("admin_home") {
            AdminHomeScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                },
                onNavigateToAddStudent = {
                    navController.navigate("add_student") // Vamos a la pantalla de crear.
                },
                onNavigateToDetail = { email ->
                    // Vamos a la pantalla de detalle pasando el email del alumno como argumento.
                    navController.navigate("student_detail/$email")
                }
            )
        }

        // --- RUTA: AÑADIR ALUMNO (Crear nuevo) ---
        composable("add_student") {
            val adminViewModel: AdminViewModel = viewModel()
            AddStudentScreen(
                onStudentAdded = { nombre, email ->
                    adminViewModel.registrarAlumno(
                        nombre = nombre,
                        email = email,
                        onSuccess = { navController.popBackStack() }, // Si se crea, volvemos a la lista.
                        onError = { println(it) }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: DETALLE DEL ALUMNO (Gestionar uno existente) ---
        // Definimos que esta ruta espera un argumento llamado "email".
        composable(
            route = "student_detail/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extraemos el email del alumno que viene en la URL de la navegación.
            val email = backStackEntry.arguments?.getString("email") ?: ""
            StudentDetailScreen(
                studentEmail = email,
                onBack = { navController.popBackStack() } // Volver a la lista de administración.
            )
        }

        // --- RUTA: HOME ALUMNO ---
        composable("student_home") {
            StudentHomeScreen(
                email = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                }
            )
        }
    }
}