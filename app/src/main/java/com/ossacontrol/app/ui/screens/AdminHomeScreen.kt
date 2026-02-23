package com.ossacontrol.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ossacontrol.app.model.User
import com.ossacontrol.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit, 
    onNavigateToAddStudent: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    // Instanciamos el ViewModel que gestiona la lista de alumnos
    val viewModel: AdminViewModel = viewModel()
    val alumnos = viewModel.usuarios.value

    // Cargamos los datos de Firebase al entrar
    LaunchedEffect(Unit) {
        viewModel.obtenerAlumnos()
    }

    Scaffold(
        topBar = {
            // Barra superior con diseño limpio
            TopAppBar(
                title = { Text("GESTIÓN ACADEMIA", fontWeight = FontWeight.Bold) },
                actions = {
                    // Botón de logout en la esquina superior
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            // Botón flotante para añadir alumnos con el color primario (Negro/Blanco)
            FloatingActionButton(
                onClick = onNavigateToAddStudent,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Cabecera con el total de alumnos
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "TOTAL ALUMNOS: ${alumnos.size}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Lista de alumnos con el nuevo diseño de tarjeta
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alumnos) { alumno ->
                    CardAlumno(
                        alumno = alumno, 
                        onClick = { onNavigateToDetail(alumno.email) }
                    )
                }
            }
        }
    }
}

@Composable
fun CardAlumno(alumno: User, onClick: () -> Unit) {
    // Tarjeta del alumno con bordes suaves y elevación ligera
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Nombre del alumno destacado
                Text(
                    text = alumno.nombre.uppercase(), 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Info secundaria (Cinturón y clases)
                Row {
                    Text(
                        text = "Cinturón: ${alumno.cinturon}", 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Clases: ${alumno.clasesAsistidas}", 
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            // Indicador visual de que es clicable
            Text("Ver >", style = MaterialTheme.typography.labelSmall)
        }
    }
}