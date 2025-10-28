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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
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
import pe.fintrack.mobile.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContentNavGraph(
    navControllerRaiz: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
    ) {
    val navController = rememberNavController() // Este controlador gestionará SOLO la navegación interna (Home, Ingreso, Gastos)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val homeUiState by homeViewModel.uiState.collectAsState()

    val navigationItems = listOf(
        AppScreen.Home,
        AppScreen.Ingreso,
        AppScreen.Gastos,
        AppScreen.Movimientos
    )

    Scaffold(
        topBar = {
            // Lógica de TopBar solo para la pantalla Home
            if (currentDestination?.route == AppScreen.Home.route) {
                FinTrackTopBar(
                    nombreUsuario = homeUiState.nombreUsuario,
                    onNotificationClick = { /* si hay notificaciones */ }
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
                                    // SOLO INCLUIMOS LAS RUTAS QUE ESTÁN EN navigationItems
                                    AppScreen.Home -> Icons.Default.Home
                                    AppScreen.Ingreso -> Icons.Default.Add
                                    AppScreen.Gastos -> Icons.Default.Edit
                                    AppScreen.Movimientos -> Icons.Default.MoreVert
                                    else -> Icons.Default.Home
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
            composable(route = AppScreen.Home.route) { HomeScreen(navController = navController, viewModel = homeViewModel) }
            composable(route = AppScreen.RegistrarIngreso.route) { RegistrarIngresoScreen(onNavigateBack = {navController.popBackStack()}) }
            composable(route = AppScreen.Ingreso.route) { IngresoScreen(navController = navController) }
            composable(route = AppScreen.Gastos.route) { GastosScreen() }
            composable(route = AppScreen.Movimientos.route) { MovimientosScreen() }
        }
    }
}