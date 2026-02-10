package com.ossacontrol.app.ui.screens

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
fun AdminHomeScreen(onLogout: () -> Unit, onNavigateToAddStudent: () -> Unit) {
    val viewModel: AdminViewModel = viewModel()
    val alumnos = viewModel.usuarios.value

    // Al entrar en la pantalla, cargamos los datos
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Gestión de Academia") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddStudent) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }

    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Un buscador o título
            Text(
                "Listado de Alumnos (${alumnos.size})",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            // El LazyColumn es como un RecyclerView: muy eficiente para listas largas
            LazyColumn {
                items(alumnos) { alumno ->
                    CardAlumno(alumno)
                }
            }

            Button(onClick = onLogout, modifier = Modifier.padding(16.dp)) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun CardAlumno(alumno: User) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column {
                Text(text = alumno.nombre, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Cinturón: ${alumno.cinturon}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Clases: ${alumno.clasesAsistidas}", color = MaterialTheme.typography.labelSmall.color)
            }
        }
    }
}