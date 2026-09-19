package ovh.gabrielhuav.flasklogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ovh.gabrielhuav.flasklogin.ui.theme.FlaskLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlaskLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnrutadorPrincipal()
                }
            }
        }
    }
}

@Composable
fun EnrutadorPrincipal() {
    // Controla en qué pantalla estamos
    var pantallaActual by remember { mutableStateOf("login") }
    // Guarda el token cuando iniciamos sesión
    var tokenUsuario by remember { mutableStateOf("") }

    when (pantallaActual) {
        "login" -> {
            LoginScreen(
                onLoginSuccess = { token ->
                    tokenUsuario = token
                    pantallaActual = "crud" // Si el login es exitoso, pasamos al CRUD
                },
                onNavigateToRegister = {
                    pantallaActual = "registro"
                }
            )
        }
        "registro" -> {
            RegistroScreen(
                onRegistroExitoso = {
                    pantallaActual = "login" // Regresa al login para que entre
                },
                onRegresar = {
                    pantallaActual = "login"
                }
            )
        }
        "crud" -> {
            CrudScreen(
                token = tokenUsuario,
                onLogout = {
                    tokenUsuario = ""
                    pantallaActual = "login"
                }
            )
        }
    }
}