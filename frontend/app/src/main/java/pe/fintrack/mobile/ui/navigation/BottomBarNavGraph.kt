package pe.fintrack.mobile.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.components.FinTrackTopBar
import pe.fintrack.mobile.ui.theme.screen.GastosScreen
import pe.fintrack.mobile.ui.theme.screen.HomeScreen
import pe.fintrack.mobile.ui.theme.screen.IngresoScreen
import pe.fintrack.mobile.ui.theme.screen.MovimientosScreen
import pe.fintrack.mobile.ui.theme.screen.RegistrarIngresoScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContentNavGraph(navControllerRaiz: NavController) {
    val navController = rememberNavController() // Este controlador gestionará SOLO la navegación interna (Home, Ingreso, Gastos)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val navigationItems = listOf(
        AppScreen.Home,
        AppScreen.Ingreso,
        AppScreen.Gastos,
        AppScreen.Movimientos
    )

    Scaffold(
        topBar = {
            if (currentDestination?.route == AppScreen.Home.route) {
                FinTrackTopBar(
                    nombreUsuario = "Franco Peralta",
                    onNotificationClick = { /* TODO: Implementar navegación a notificaciones */ }
                )
            }
        },
        bottomBar = {
            NavigationBar {
                navigationItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = when (screen) {
                                    AppScreen.Login -> TODO()
                                    AppScreen.MainContent -> TODO()
                                    AppScreen.Home -> Icons.Default.Home
                                    AppScreen.Ingreso -> Icons.Default.Add
                                    AppScreen.Gastos -> Icons.Default.Edit
                                    AppScreen.Movimientos -> Icons.Default.MoreVert
                                    AppScreen.EditarGastos -> TODO()
                                    AppScreen.EditarIngreso -> TODO()
                                    AppScreen.RegistrarGastos -> TODO()
                                    AppScreen.RegistrarIngreso -> TODO()

                                },
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
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
            composable(route = AppScreen.Home.route) { HomeScreen(navController = navController) }
            composable(route = AppScreen.RegistrarIngreso.route) { RegistrarIngresoScreen(onNavigateBack = {navController.popBackStack()}) }
            //composable(route = AppScreen.RegistrarGastos.route) { RegistrarGastos()}
            composable(route = AppScreen.Ingreso.route) { IngresoScreen(navController = navController) }
            composable(route = AppScreen.Gastos.route) { GastosScreen() }
            composable(route = AppScreen.Movimientos.route) { MovimientosScreen() }
        }
    }
}