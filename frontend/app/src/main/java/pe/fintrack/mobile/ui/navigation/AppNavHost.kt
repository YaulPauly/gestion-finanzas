package pe.fintrack.mobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.screen.HomeScreen
import pe.fintrack.mobile.ui.theme.screen.LoginScreen
import pe.fintrack.mobile.ui.theme.screen.MovimientosScreen
import pe.fintrack.mobile.ui.theme.screen.RegistrarGastosScreen
import pe.fintrack.mobile.ui.theme.screen.RegistrarIngresoScreen

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

        // 2. Ruta Contenedora del Home
        composable(AppScreen.Home.route) {
            HomeScreen(navController = navController)
        }
        // Pantallas principales de la barra inferior
        composable(AppScreen.Home.route) { HomeScreen(navController = navController) }
        //composable(AppScreen.Movimientos.route) { MovimientosScreen(navController = navController) }

        // Pantallas de registro (las que estás usando en la barra)
        //composable(AppScreen.RegistrarIngreso.route) { RegistrarIngresoScreen (navController = navController) }
        composable(AppScreen.RegistrarGastos.route) { RegistrarGastosScreen(navController = navController) }

    }
}