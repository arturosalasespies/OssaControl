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

// Centralizamos los nombres de las rutas en un objeto para no cometer errores al escribir los nombres
object Routes {
    const val Login = "login"
    const val SignUp = "signup"
    const val HomeSelector = "home_selector"
    const val AdminHome = "admin_home"
    const val StudentHome = "student_home"
    const val AddStudent = "add_student"
    const val StudentDetail = "student_detail" // Ruta base para el detalle
}

@Composable
fun AppRoot() {
    // 1. Inicializamos el controlador que se encarga de cambiar entre pantallas
    val navController = rememberNavController()

    // 2. Accedemos al ViewModel de autenticación para gestionar el acceso por roles
    val authViewModel: AuthViewModel = viewModel()

    // 3. Obtenemos el rol actual del usuario (admin o alumno)
    val userRole by authViewModel.userRole

    // --- VIGILANTE GLOBAL ---
    // Este bloque "escucha" cambios en el rol y nos redirige automáticamente a la pantalla correcta
    LaunchedEffect(userRole) {
        when (userRole) {
            "admin" -> navController.navigate(Routes.AdminHome) { popUpTo(0) }
            "alumno" -> navController.navigate(Routes.StudentHome) { popUpTo(0) }
        }
    }

    // 4. Decidimos la pantalla de inicio: si ya hay sesión, vamos al selector; si no, al login
    val start = if (FirebaseAuth.getInstance().currentUser != null) Routes.HomeSelector else Routes.Login

    // 5. Definimos el mapa de navegación (NavHost)
    NavHost(navController = navController, startDestination = start) {

        // --- RUTA: LOGIN ---
        composable(Routes.Login) {
            LoginScreen(
                // Si el login es correcto, pedimos al ViewModel que verifique el rol del usuario
                onLoginSuccess = { authViewModel.checkUserRole() },
                // Si el usuario quiere registrarse, lo mandamos a la pantalla de SignUp
                onGoToSignUp = { navController.navigate(Routes.SignUp) }
            )
        }

        // --- RUTA: REGISTRO ---
        composable(Routes.SignUp) {
            SignUpScreen(
                // Al registrarse con éxito, también verificamos su rol inmediatamente
                onSignUpSuccess = { authViewModel.checkUserRole() },
                // Si cancela, volvemos atrás en la pila de navegación
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // --- RUTA: SELECTOR (Paso intermedio para cargar el rol tras abrir la app) ---
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
                // Navegamos a la pantalla de añadir alumno
                onNavigateToAddStudent = { navController.navigate(Routes.AddStudent) },
                // Al pulsar un alumno, navegamos a su detalle usando su email como parámetro
                onNavigateToDetail = { email -> navController.navigate("${Routes.StudentDetail}/$email") }
            )
        }

        // --- RUTA: AÑADIR ALUMNO ---
        composable(Routes.AddStudent) {
            val adminViewModel: AdminViewModel = viewModel()
            AddStudentScreen(
                onStudentAdded = { nombre, email ->
                    // Registramos al alumno y si todo va bien volvemos a la lista
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
        // Usamos una ruta dinámica que recibe un parámetro "{email}"
        composable(
            route = "${Routes.StudentDetail}/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            // Extraemos el email del alumno de los argumentos de la ruta
            val email = backStackEntry.arguments?.getString("email") ?: ""
            StudentDetailScreen(
                studentEmail = email,
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