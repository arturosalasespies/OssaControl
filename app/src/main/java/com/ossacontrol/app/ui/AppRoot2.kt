package com.ossacontrol.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.ossacontrol.app.ui.screens.*
import com.ossacontrol.app.viewmodel.AuthViewModel
import com.ossacontrol.app.viewmodel.AdminViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppRoot2() {
    //El NavController es el objeto que controla hacia dónde va el usuario
    val navController = rememberNavController()

    //Instanciamos el AuthViewModel para gestionar la lógica de roles desde Firestore
    val authViewModel: AuthViewModel = viewModel()

    //Obtenemos el rol actual del usuario desde el ViewModel ("admin", "alumno" o null)
    val userRole by authViewModel.userRole

    //Decidimos la ruta de inicio: si hay sesión iniciada, vamos a "Home", si no, a "Login"
    val start = if (FirebaseAuth.getInstance().currentUser != null) "home_selector" else "login"

    //El NavHost define el mapa de navegación de toda la aplicación
    NavHost(navController = navController, startDestination = start) {

        // --- RUTA: LOGIN ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Al loguearse con éxito, llamamos a Firestore para saber qué rol tiene el usuario
                    authViewModel.checkUserRole()
                },
                onGoToSignUp = { navController.navigate("signup") }
            )

            // Efecto lanzado: cuando detectamos que el rol ha sido cargado, saltamos de pantalla
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
                onSignUpSuccess = {
                    // Al registrarse, por defecto los mandamos a chequear rol (será alumno)
                    authViewModel.checkUserRole()
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // --- RUTA INTERMEDIA: SELECTOR (Para usuarios que ya tienen sesión abierta) ---
        composable("home_selector") {
            // Si el usuario ya estaba logueado al abrir la app, disparamos la carga del rol
            LaunchedEffect(Unit) {
                authViewModel.checkUserRole()
            }

            //aquí redirigimos al cambiar userRole
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
                    // Limpiamos sesión en Firebase y volvemos a Login
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo(0) }
                },
                onNavigateToAddStudent = {
                    navController.navigate("add_student")
                }
            )
        }

        // --- RUTA: AÑADIR ALUMNO ---
        composable("add_student") {
            val adminViewModel: AdminViewModel = viewModel()
            AddStudentScreen(
                onStudentAdded = { nombre, email ->
                    // Llamamos a la función de guardar que modificamos en el ViewModel
                    adminViewModel.registrarAlumno(
                        nombre = nombre,
                        email = email,
                        onSuccess = {
                            navController.popBackStack()
                        },
                        onError = { errorMsg ->
                            // Por ahora solo imprimimos el error, podrías mostrar un Toast
                            println(errorMsg)
                        }
                    )
                },
                onBack = { navController.popBackStack() }
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