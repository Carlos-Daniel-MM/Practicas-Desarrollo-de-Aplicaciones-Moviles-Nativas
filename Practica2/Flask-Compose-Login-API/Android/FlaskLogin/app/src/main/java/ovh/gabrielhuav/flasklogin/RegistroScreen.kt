package ovh.gabrielhuav.flasklogin

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RegistroScreen(onRegistroExitoso: () -> Unit, onRegresar: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear Cuenta", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nuevo Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            RetrofitClient.instance.registrar(LoginRequest(username, password))
                        }
                        if (response.isSuccessful) {
                            onRegistroExitoso() // Lo mandamos al login si salió bien
                        } else {
                            mensaje = "El usuario ya existe o hubo un error"
                        }
                    } catch (e: Exception) {
                        mensaje = "Error de conexión"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarme")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onRegresar) {
            Text("Volver al Inicio de Sesión")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = mensaje, color = MaterialTheme.colorScheme.error)
        }
    }
}