package pe.fintrack.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.components.FinTrackTopBar
import pe.fintrack.mobile.ui.theme.screen.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge() // Puedes descomentar esto si manejas los insets manualmente
        setContent {
            FintrackMobileTheme {
                AppNavigation()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = listOf(
        AppScreen.Home,
        AppScreen.ListaIngreso,
        AppScreen.ListaGastos,
        AppScreen.ListaMovimientos
    )

    Scaffold(
        topBar = {
            // La barra superior solo aparece en la pantalla de inicio
            if (currentDestination?.route == AppScreen.Home.route) {
                FinTrackTopBar(
                    nombreUsuario = "Franco Peralta", // Eventualmente, esto vendría de un ViewModel
                    onNotificationClick = { /* TODO: Implementar navegación a notificaciones */ }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        // ✅ SECCIÓN DE ÍCONOS AÑADIDA
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    AppScreen.Home -> Icons.Default.Home
                                    AppScreen.ListaIngreso -> Icons.Default.Add
                                    AppScreen.ListaGastos -> Icons.Default.Edit
                                    AppScreen.ListaMovimientos -> Icons.Default.MoreVert
                                    else -> Icons.Default.Home // Un ícono por defecto
                                },
                                contentDescription = screen.route
                            )
                        },
                        label = {
                            Text(
                                text = screen.route.split("_")[0].replaceFirstChar { it.uppercase() },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.route) {
                HomeScreen()
            }
            composable(route = AppScreen.ListaIngreso.route) {
                IngresoScreen()
            }
            composable(route = AppScreen.ListaGastos.route) {
                GastosScreen()
            }
            composable(route = AppScreen.ListaMovimientos.route) {
                MovimientosScreen()
            }
        }
    }
}