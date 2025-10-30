package pe.fintrack.mobile.ui.theme.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.data.viewmodel.AuthViewModel
import pe.fintrack.mobile.ui.theme.data.viewmodel.LoginUiState


@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()){
    var email by remember { mutableStateOf("juan.perez@example.com") } // Valor por defecto para prueba
    var password by remember { mutableStateOf("hashed_password_123") } // Valor por defecto
    val loginState by authViewModel.loginState.collectAsState()

    // Observa el estado del login para navegar o mostrar mensajes
    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Success) {
            navController.navigate(AppScreen.Home.route) {
                popUpTo(navController.graph.id) { inclusive = true } // Limpia el stack de navegación
            }
        }
    }

    Column (
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()

        )
        Spacer(modifier = Modifier.height(24.dp))
        // Muestra el botón o un indicador de carga
        when (loginState) {
            is LoginUiState.Loading -> CircularProgressIndicator()
            else -> Button(
                onClick = { authViewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }
        }

        // Muestra mensaje de error si existe
        if (loginState is LoginUiState.Error) {
            Spacer(modifier = Modifier.height(16.dp))
            Text((loginState as LoginUiState.Error).message, color = MaterialTheme.colorScheme.error)
        }
    }

}