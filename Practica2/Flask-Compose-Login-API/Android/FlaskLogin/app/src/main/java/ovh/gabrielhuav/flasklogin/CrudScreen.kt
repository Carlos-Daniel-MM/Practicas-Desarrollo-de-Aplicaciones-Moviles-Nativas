package ovh.gabrielhuav.flasklogin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CrudScreen(token: String, onLogout: () -> Unit) {
    var tareas by remember { mutableStateOf(listOf<Tarea>()) }
    var nuevoTitulo by remember { mutableStateOf("") }
    var nuevaDescripcion by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Variables para controlar el cuadro emergente de edición
    var tareaAEditar by remember { mutableStateOf<Tarea?>(null) }
    var tituloEditado by remember { mutableStateOf("") }
    var descripcionEditada by remember { mutableStateOf("") }

    fun cargarTareas() {
        scope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.obtenerTareas(token)
                }
                if (response.isSuccessful && response.body() != null) {
                    tareas = response.body()!!
                }
            } catch (e: Exception) { }
        }
    }

    LaunchedEffect(Unit) { cargarTareas() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Formulario para nueva tarea
        Text("Nueva Tarea", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(value = nuevoTitulo, onValueChange = { nuevoTitulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = nuevaDescripcion, onValueChange = { nuevaDescripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitClient.instance.crearTarea(token, Tarea(titulo = nuevoTitulo, descripcion = nuevaDescripcion))
                        }
                        if (response.isSuccessful) {
                            nuevoTitulo = ""
                            nuevaDescripcion = ""
                            cargarTareas()
                        }
                    } catch (e: Exception) { }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Agregar Tarea") }

        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Text("Mis Tareas", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(tareas) { tarea ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(tarea.titulo, style = MaterialTheme.typography.titleMedium)
                            Text(tarea.descripcion, style = MaterialTheme.typography.bodyMedium)
                        }
                        // Botones de acción (Editar y Borrar)
                        Row {
                            IconButton(onClick = {
                                // Al tocar editar, pre-llenamos los datos y mostramos el cuadro
                                tareaAEditar = tarea
                                tituloEditado = tarea.titulo
                                descripcionEditada = tarea.descripcion
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = {
                                scope.launch {
                                    try {
                                        tarea.id?.let { id ->
                                            val response = withContext(Dispatchers.IO) { RetrofitClient.instance.borrarTarea(token, id) }
                                            if (response.isSuccessful) cargarTareas()
                                        }
                                    } catch (e: Exception) { }
                                }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Borrar")
                            }
                        }
                    }
                }
            }
        }

        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión") }
    }

    // Diseño del cuadro de diálogo emergente para editar
    if (tareaAEditar != null) {
        AlertDialog(
            onDismissRequest = { tareaAEditar = null },
            title = { Text("Editar Tarea") },
            text = {
                Column {
                    OutlinedTextField(value = tituloEditado, onValueChange = { tituloEditado = it }, label = { Text("Título") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = descripcionEditada, onValueChange = { descripcionEditada = it }, label = { Text("Descripción") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            tareaAEditar?.id?.let { id ->
                                val response = withContext(Dispatchers.IO) {
                                    RetrofitClient.instance.actualizarTarea(token, id, Tarea(titulo = tituloEditado, descripcion = descripcionEditada))
                                }
                                if (response.isSuccessful) {
                                    tareaAEditar = null // Cerramos el cuadro
                                    cargarTareas() // Recargamos la lista
                                }
                            }
                        } catch (e: Exception) { }
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { tareaAEditar = null }) { Text("Cancelar") }
            }
        )
    }
}