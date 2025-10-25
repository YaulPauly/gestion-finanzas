package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.viewmodel.UsuarioViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: UsuarioViewModel = hiltViewModel() // Inyección del ViewModel
) {
    // 1. Estados de la UI
    var email by remember { mutableStateOf("usuario@ejemplo.com") } // Valores pre-llenados para fácil prueba
    var password by remember { mutableStateOf("123") }

    // Observar el estado de carga y el mensaje
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    // Observar el usuario actual (la clave para la navegación)
    val usuarioActual by viewModel.usuarioActual.collectAsState()

    // 2. Efecto para la Navegación (Una vez que el usuario inicia sesión)
    LaunchedEffect (usuarioActual) {
        if (usuarioActual != null) {
            // Navega a la pantalla Home.
            navController.navigate(AppScreen.Home.route) {
                popUpTo(AppScreen.Login.route) { inclusive = true }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Inicio de Sesión", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button (
            onClick = { viewModel.iniciarSesion(email, password) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar")
            }
        }

        // Muestra el mensaje de error o éxito
        message?.let {
            Text(
                text = it,
                color = if (usuarioActual != null) Color.Green.copy(alpha = 0.8f) else Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
            // Llama a clearMessage() después de un breve periodo si es un error
            LaunchedEffect(it) {
                if (usuarioActual == null) {
                    delay(3000)
                    viewModel.clearMessage()
                }
            }
        }
    }
}