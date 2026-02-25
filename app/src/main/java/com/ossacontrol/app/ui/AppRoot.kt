package com.ossacontrol.app.ui

/**
 * ============================================
 * Archivo: AppRoot.kt
 * Navegación principal de la app (mapa de pantallas).
 *
 * HISTORIAL DE CAMBIOS:
 *   - Alejandra (09/02): Versión inicial (login, registro, home)
 *   - Alberto  (25/02): Refactor con objeto Routes, roles, navegación dinámica
 *   - Arturo   (25/02): Añadida ruta de Candidatos a Graduación
 *   - Arturo (con Claude Code) (25/02): Añadidas rutas de Inactivos y Estadísticas
 * ============================================
 */

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

// Centralizamos los nombres de las rutas para no cometer errores al escribirlos
object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val HomeSelector = "home_selector"
    const val AdminHome = "admin_home"
    const val StudentHome = "student_home"
    const val AddStudent = "add_student"
    const val StudentDetail = "student_detail"
    const val Candidatos = "candidatos"        // Alumnos que cumplen requisitos IBJJF
    const val Inactivos = "inactivos"          // AÑADIDO: Alumnos sin asistir en 30 días
    const val Estadisticas = "estadisticas"    // AÑADIDO: Estadísticas generales de la academia
}

@Composable
fun AppRoot() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val userRole by authViewModel.userRole

    // Vigilante global: redirige según el rol del usuario
    LaunchedEffect(userRole) {
        when (userRole) {
            "admin" -> navController.navigate(Routes.AdminHome) { popUpTo(0) }
            "alumno" -> navController.navigate(Routes.StudentHome) { popUpTo(0) }
        }
    }

    val start = if (FirebaseAuth.getInstance().currentUser != null) Routes.HomeSelector else Routes.Login

    NavHost(navController = navController, startDestination = start) {

        // --- RUTA: LOGIN ---
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = { authViewModel.checkUserRole() },
                onGoToSignUp = { navController.navigate(Routes.SignUp) }
            )
        }

        // --- RUTA: REGISTRO ---
        composable(Routes.SignUp) {
            SignUpScreen(
                onSignUpSuccess = { authViewModel.checkUserRole() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // --- RUTA: SELECTOR (carga de rol) ---
        composable(Routes.HomeSelector) {
            LaunchedEffect(Unit) { authViewModel.checkUserRole() }
        }

        // --- RUTA: INICIO ADMINISTRADOR ---
        composable(Routes.AdminHome) {
            AdminHomeScreen(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login) { popUpTo(0) }
                },
                onNavigateToAddStudent = { navController.navigate(Routes.AddStudent) },
                onNavigateToDetail = { email -> navController.navigate("${Routes.StudentDetail}/$email") },
                onNavigateToCandidatos = { navController.navigate(Routes.Candidatos) },
                onNavigateToInactivos = { navController.navigate(Routes.Inactivos) },
                onNavigateToEstadisticas = { navController.navigate(Routes.Estadisticas) }
            )
        }

        // --- RUTA: AÑADIR ALUMNO ---
        composable(Routes.AddStudent) {
            val adminViewModel: AdminViewModel = viewModel()
            AddStudentScreen(
                onStudentAdded = { nombre, email ->
                    adminViewModel.registrarAlumno(
                        nombre = nombre,
                        email = email,
                        onSuccess = { navController.popBackStack() },
                        onError = { println("Error: $it") }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: DETALLE DEL ALUMNO ---
        // Ruta dinámica que recibe el email del alumno como parámetro
        composable(
            route = "${Routes.StudentDetail}/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            StudentDetailScreen(
                studentEmail = email,
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: CANDIDATOS A GRADUACIÓN ---
        // Pantalla con los alumnos que cumplen requisitos IBJJF para subir de cinturón
        composable(Routes.Candidatos) {
            CandidatosScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: ALUMNOS INACTIVOS (AÑADIDO) ---
        // Pantalla con los alumnos que llevan más de 30 días sin asistir
        composable(Routes.Inactivos) {
            InactivosScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: ESTADÍSTICAS (AÑADIDO) ---
        // Pantalla con estadísticas generales de la academia
        composable(Routes.Estadisticas) {
            EstadisticasScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA: INICIO ALUMNO ---
        composable(Routes.StudentHome) {
            StudentHomeScreen(
                email = FirebaseAuth.getInstance().currentUser?.email ?: "Usuario",
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Login) { popUpTo(0) }
                }
            )
        }
    }
}
