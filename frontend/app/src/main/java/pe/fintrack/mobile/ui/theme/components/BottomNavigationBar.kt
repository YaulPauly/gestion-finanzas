package pe.fintrack.mobile.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

data class BottomNavItem(
    val screen: AppScreen,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isAction: Boolean = false
)

val bottomNavItems = listOf(
    BottomNavItem(AppScreen.Home, Icons.Filled.Home),
    BottomNavItem(AppScreen.Ingreso, Icons.Filled.Add,isAction = true),
    BottomNavItem(AppScreen.RegistrarGastos, Icons.Filled.Edit, isAction = true),
    BottomNavItem(AppScreen.Movimientos, Icons.Filled.MoreVert)
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val currentRoute = navController.currentDestination?.route // Obtener la ruta actual

        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route ||
                    (!item.isAction && currentRoute == AppScreen.Home.route && item.screen == AppScreen.Home)

            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.screen.title) },
                label = { Text(item.screen.title) },
                selected = isSelected,
                onClick = {
                    // Si el destino es diferente O si es un botón de acción rápida, navegamos.
                    if (currentRoute != item.screen.route || item.isAction) {
                        navController.navigate(item.screen.route) {
                            // Para navegación tab (Home/Movimientos), queremos mantener el estado:
                            if (!item.isAction) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            // Si es acción (Registrar), simplemente lo apilamos (por defecto)
                        }
                    }
                }
            )
        }
    }
}