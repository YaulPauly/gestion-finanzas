package pe.fintrack.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.screen.LoginScreen


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // El ViewModel para observar el estado de autenticación (Necesitas inyectarlo/obtenerlo)
    //val usuarioViewModel: UsuarioViewModel = hiltViewModel()
    //val usuarioActual by usuarioViewModel.usuarioActual.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        // 1. Ruta de Login
        composable (AppScreen.Login.route) {
            // Reemplaza esto con tu LoginScreen
            LoginScreen(navController = navController)
        }

        // RUTA DEL CONTENIDO PRINCIPAL
        composable(route = AppScreen.MainContent.route) {
            // Esto lleva a la pantalla con el Scaffold, BottomBar, etc.
            MainContentNavGraph(navControllerRaiz = navController)
        }

    }
}