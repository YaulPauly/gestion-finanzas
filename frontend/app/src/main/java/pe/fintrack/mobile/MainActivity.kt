package pe.fintrack.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pe.fintrack.mobile.ui.theme.FintrackMobileTheme
import pe.fintrack.mobile.ui.theme.components.AppScreen
import pe.fintrack.mobile.ui.theme.components.FinTrackTopBar
import pe.fintrack.mobile.ui.theme.data.TokenManager
import pe.fintrack.mobile.ui.theme.screen.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        enableEdgeToEdge()
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
    val startDestination = if (TokenManager.hasToken()) {
        AppScreen.Home.route
    } else {
        AppScreen.Login.route
    }
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
            NavigationBar (containerColor = Color(0xFF3C467B)){
                navigationItems.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            val iconoColor = if(isSelected) Color(0xFF3C467B) else Color(0xFFFFFFFF)
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFFFF))
                            )
                            {
                                Icon(tint= iconoColor,
                                    imageVector = when (screen) {
                                        AppScreen.Login -> Icons.Filled.Person
                                        AppScreen.Home -> Icons.Default.Home
                                        AppScreen.Ingreso -> Icons.Default.Add
                                        AppScreen.Gastos -> Icons.Default.Edit
                                        AppScreen.Movimientos -> Icons.AutoMirrored.Filled.Send
                                        AppScreen.CrearMeta -> TODO()
                                        AppScreen.ListaMetas -> TODO()
                                        AppScreen.EditarGastos -> TODO()
                                        AppScreen.EditarIngreso -> TODO()
                                        AppScreen.RegistrarGastos -> TODO()
                                        AppScreen.RegistrarIngreso -> TODO()
                                    },
                                    contentDescription = screen.title
                                )

                            }
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color(0xFFFFFFFF)
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
            startDestination = startDestination, // <-- RUTA INICIAL DINÁMICA
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- Rutas de Autenticación ---
            composable(route = AppScreen.Login.route) {
                LoginScreen(navController = navController)
            }

            // --- Rutas Principales ---
            composable(route = AppScreen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(route = AppScreen.Ingreso.route) {
                IngresoScreen(navController = navController)
            }
            composable(route = AppScreen.Gastos.route) {
                GastosScreen(navController = navController, modifier = Modifier)
            }
            composable(route = AppScreen.Movimientos.route) {
                MovimientosScreen(/*navController = navController*/) // Pasa navController si lo necesita
            }

            // --- Rutas de Metas (Goals) ---
            composable(route = AppScreen.ListaMetas.route) {
                ListaMetas(navController = navController)
            }
            composable(route = AppScreen.CrearMeta.route) {
                CrearMetaScreen(navController = navController)
            }

            // --- Rutas de Creación (CRUD) ---
            composable(route = AppScreen.RegistrarIngreso.route) {
                RegistrarIngresoScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(route = AppScreen.RegistrarGastos.route) {
                RegistrarGastoScreen(navController = navController)
            }

            // --- Rutas de Edición (CRUD) ---
            composable(
                route = AppScreen.EditarIngreso.route, // "editar_ingresos/{transactionId}"
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId")
                if (transactionId != null) {
                    // Debes crear esta pantalla "EditarIngresoScreen"
                    // EditarIngresoScreen(transactionId = transactionId, navController = navController)
                    // Por ahora, usamos RegistrarIngreso como placeholder si no la tienes:
                    Text("Placeholder para Editar Ingreso ID: $transactionId")
                    // RegistrarIngresoScreen(onNavigateBack = { navController.popBackStack() })
                } else {
                    navController.popBackStack()
                }
            }

            composable(
                route = AppScreen.EditarGastos.route, // "editar_gastos/{transactionId}"
                arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId")
                if (transactionId != null) {
                    // Debes crear esta pantalla "EditarGastoScreen"
                    // EditarGastoScreen(transactionId = transactionId, navController = navController)
                    // Por ahora, usamos RegistrarGastos como placeholder:
                    Text("Placeholder para Editar Gasto ID: $transactionId")
                    // RegistrarGastosScreen(navController = navController)
                } else {
                    navController.popBackStack()
                }
            }
        }
    }
}
