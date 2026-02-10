package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.model.User
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit, 
    onNavigateToAddStudent: () -> Unit,
    onNavigateToDetail: (String) -> Unit // Añadimos esto para poder ir al detalle usando el email del alumno
) {
    val viewModel: AdminViewModel = viewModel()
    val alumnos = viewModel.usuarios.value

    // Al entrar en la pantalla, cargamos los datos de Firebase
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de Academia") })
        },
        floatingActionButton = {
            // Botón flotante para ir a la pantalla de añadir alumno
            FloatingActionButton(onClick = onNavigateToAddStudent) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Título que muestra cuántos alumnos hay cargados
            Text(
                "Listado de Alumnos (${alumnos.size})",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            // Lista eficiente de alumnos
            LazyColumn {
                items(alumnos) { alumno ->
                    // Ahora la tarjeta es clicable y nos lleva al detalle del alumno
                    CardAlumno(alumno = alumno, onClick = { onNavigateToDetail(alumno.email) })
                }
            }

            // Botón para salir de la cuenta
            Button(onClick = onLogout, modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun CardAlumno(alumno: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }, // Hacemos que toda la tarjeta reaccione al toque
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column {
                // Mostramos nombre, cinturón y clases acumuladas
                Text(text = alumno.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Cinturón: ${alumno.cinturon}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Clases: ${alumno.clasesAsistidas}", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}