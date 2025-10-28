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
import pe.fintrack.mobile.ui.viewmodel.AuthUiState
import pe.fintrack.mobile.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    // 1. Estados de la UI
    var email by remember { mutableStateOf("usuario@ejemplo.com") }
    var password by remember { mutableStateOf("123") }

    // CAMBIO CLAVE 2: Observar el NUEVO estado de autenticación
    val authState by viewModel.uiState.collectAsState()



    // 2. Efecto para la Navegación y Manejo de Errores
    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            // Mueve el usuario al contenido principal
            navController.navigate(AppScreen.MainContent.route) {
                // Limpia la pila de navegación para que el usuario no pueda volver al login con el botón 'atrás'
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
            onClick = { viewModel.login(email, password) },
            enabled = authState != AuthUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (authState == AuthUiState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar")
            }
        }

        // Mostrar errores directamente del estado AuthUiState.Error
        (authState as? AuthUiState.Error)?.let { errorState ->
            Text(
                text = errorState.message, // Muestra el mensaje del error
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}